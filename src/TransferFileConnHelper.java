import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class TransferFileConnHelper implements Runnable {
	public final static String PERMISSION="PERMISSION";
	public final static String UPLOAD_FUNCTION="UPLOAD_FUNCTION";
	public final static String DOWNLOAD_FUNCTION="DOWNLAOD_FUNCTION";
	private Socket socket;
	private OutputStream os;
	private InputStream is;
	private BufferedReader responseReader;
	private final int fragmentSize=1048576;
	private String filePath;
	private JLabel infoLabel;
	private String function;
	public TransferFileConnHelper(String filePath,String host, int port, JLabel infoLabel, String function) throws UnknownHostException, IOException{
		socket=new Socket(host, port);		
		os=socket.getOutputStream();
		is=socket.getInputStream();
		this.filePath=filePath;
		this.infoLabel=infoLabel;
		this.function=function;
		responseReader=new BufferedReader(new InputStreamReader(is));
	}
	public Boolean uploadFile(String filePath) throws IOException{
		String metadata=createMetadata("upload", filePath);
		byte[] metadataBytes=metadata.getBytes();
		int metadataLength=metadataBytes.length;
		sendIntegerOverSocket(os, metadataLength);
		os.write(metadataBytes);
		String response=responseReader.readLine();
		System.out.println(response);
		if(response.equals(PERMISSION)){
			DataInputStream dis=new DataInputStream(new FileInputStream(filePath));
			byte[] fileFragment=new byte[fragmentSize];
			int length=0;
			while((length=dis.read(fileFragment)) != -1){
				os.write(fileFragment, 0, length);
			}
			socket.shutdownOutput();
			String response1=responseReader.readLine();
			System.out.println(response1);
			dis.close();
			socket.close();
			return true;	
		}
		else {
			return false;
		}
	}
	//Request format:
	//4 byte number that saying about length of metadata
	//function \n (download/upload)
	//fileDirectory \n 
	//Date (dd:mm:yyyy hh:ss:mm)
	//returns true if file download was successful ended
	//otherwise returns false
	public Boolean downloadFile(String filePath) throws IOException{
		String metadata=createMetadata("download", filePath);
		byte[] metadataBytes=metadata.getBytes();
		os.write(metadataBytes, 0, metadataBytes.length);
		String response=responseReader.readLine();
			if(response.equals(PERMISSION)){
				saveFile(filePath);
				socket.close();
				return true;
			}
			else{
				socket.close();
				return false;
			}
	}
	public void sendIntegerOverSocket(OutputStream sockout, int length) throws IOException{
		sockout.write((byte)( length >> 24 ));
		sockout.write((byte)( (length << 8) >> 24 ));
		sockout.write((byte)( (length << 16) >> 24 ));
		sockout.write((byte)( (length << 24) >> 24 ));
	}
	public void saveFile(String filePath) throws IOException{
		BufferedInputStream buffIn=new BufferedInputStream(is);
		BufferedOutputStream buffOut=new BufferedOutputStream(new FileOutputStream(filePath));
		byte []fileFragment=new byte[1024*1024];
		int available=-1;
		while((available=buffIn.read(fileFragment))>0)
			buffOut.write(fileFragment, 0, available);
		buffOut.close();
		buffIn.close();
	}
	public String getFileNameWithoutPath(String filePath){
		String[] path=filePath.split("/");
		String fileNameAndExtension=path[path.length-1];
		return fileNameAndExtension;
	}
	public String createMetadata(String function,String filePath){
		String createdMetadata=function+"\n";
		createdMetadata+=filePath;
		createdMetadata+="\n";
		File file=new File(filePath);
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:ss:mm");
		String date= sdf.format(file.lastModified());
		createdMetadata+=date;
		createdMetadata+="\n";
		return createdMetadata;
	}

	@Override
	public void run() {
		try {
			uploadFile(filePath);
		} catch (IOException e) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					infoLabel.setText("Unable to send File 1");
				}
			});
			e.printStackTrace();
		}
		
	}
	
}
