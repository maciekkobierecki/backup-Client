import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class SendFileHelper implements Runnable {
	private Socket socket;
	private OutputStream os;
	private InputStream is;
	private BufferedReader responseReader;
	private final int fragmentSize=1048576;
	private String filePath;
	JLabel infoLabel;
	public SendFileHelper(String filePath,String host, int port, JLabel infoLabel) throws UnknownHostException, IOException{
		socket=new Socket(host, port);		
		os=socket.getOutputStream();
		is=socket.getInputStream();
		this.filePath=filePath;
		this.infoLabel=infoLabel;
		responseReader=new BufferedReader(new InputStreamReader(is));
	}
	
	public String sendFile(String filePath) throws IOException{
		DataInputStream dis=new DataInputStream(new FileInputStream(filePath));
		byte[] fileFragment=new byte[fragmentSize];
		int length=0;
		while((length=dis.read(fileFragment)) != -1){
			os.write(fileFragment, 0, length);
		}
		String response=responseReader.readLine();
		socket.close();
		return response;
		
	}

	@Override
	public void run() {
		try {
			sendFile(filePath);
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
