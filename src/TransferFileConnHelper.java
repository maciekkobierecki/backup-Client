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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class TransferFileConnHelper implements Runnable {
	public final static String PERMISSION="PERMISSION";
	public final static String UPLOAD_FUNCTION="upload";
	public final static String DOWNLOAD_FUNCTION="download";
	public final static String ACK="ACK";
	private Socket socket;
	private OutputStream os;
	private InputStream is;
	private ObjectOutputStream oos;
	private DataInputStream dinStream;
	private BufferedReader responseReader;
	private final int fragmentSize=1048576;
	private String filePath;
	private JLabel infoLabel;
	private String function;
	private FileMetadata fileMetadata;
	public TransferFileConnHelper(String filePath,String host, int port, JLabel infoLabel, String function) throws UnknownHostException, IOException{
		socket=new Socket(host, port);		
		os=socket.getOutputStream();
		is=socket.getInputStream();
		dinStream=new DataInputStream(is);
		oos=new ObjectOutputStream(os);
		this.filePath=filePath;
		this.infoLabel=infoLabel;
		this.function=function;
		responseReader=new BufferedReader(new InputStreamReader(is));
	}
	public TransferFileConnHelper(String filePath,String host, int port, JLabel infoLabel, String function, FileMetadata metadata) throws UnknownHostException, IOException{
		socket=new Socket(host, port);		
		os=socket.getOutputStream();
		is=socket.getInputStream();
		dinStream=new  DataInputStream(is);
		oos=new ObjectOutputStream(os);
		this.filePath=filePath;
		this.infoLabel=infoLabel;
		this.function=function;
		fileMetadata=metadata;
		responseReader=new BufferedReader(new InputStreamReader(is));
	}
	public Boolean uploadFile(String filePath) throws IOException{
		sendFunctionToServer(UPLOAD_FUNCTION);
		FileMetadata metadata=createMetadata("upload", filePath);
		sendMetadataOverSocket(metadata);
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
			responseReader.close();
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
	public Boolean downloadFile() throws IOException{
		sendFunctionToServer(DOWNLOAD_FUNCTION);
		sendMetadataOverSocket(fileMetadata);
		String response=responseReader.readLine();
			if(response.equals(PERMISSION)){
				saveFile(filePath);
				socket.shutdownOutput();
				System.out.println("downloaded");
				socket.close();
				return true;
			}
			else{
				socket.close();
				return false;
			}
	}
	private void sendACK() throws IOException{
		oos.writeObject(ACK);
		oos.flush();
		
	}
	private void sendFunctionToServer(String function) throws IOException{
		oos.writeObject(function);
		oos.flush();
	}
	private void sendMetadataOverSocket(FileMetadata metadata) throws IOException{
		oos.writeObject(metadata);
		oos.flush();
	}
	public void saveFile(String filePath) throws IOException{
		FileOutputStream fos=new FileOutputStream(filePath);
		byte []fileFragment=new byte[1024*1024];
		int available=-1;
		while((available=dinStream.read(fileFragment))>0){
			fos.write(fileFragment, 0, available);
			fos.flush();
		}
		fos.close();
	}
	public String getFileNameWithoutPath(String filePath){
		String[] path=filePath.split("/");
		String fileNameAndExtension=path[path.length-1];
		return fileNameAndExtension;
	}
	public FileMetadata createMetadata(String function,String filePath){
		String modificationDate=getModificationDate(filePath);
		FileMetadata metadata=new FileMetadata(null, filePath, modificationDate);
		return metadata;
	}
	private String getModificationDate(String filePath){
		File file=new File(filePath);
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:ss:mm");
		String date= sdf.format(file.lastModified());
		return date;
	}
	@Override
	public void run() {
		try {
			if(function.equals(UPLOAD_FUNCTION))
				uploadFile(filePath);
			else if(function.equals(DOWNLOAD_FUNCTION))
				downloadFile();
			
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
