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

public class BackupClient {
	private Socket socket;
	private OutputStream os;
	private InputStream is;
	private BufferedReader responseReader;
	private final int fragmentSize=1048576;
	public BackupClient(String host, int port) throws UnknownHostException, IOException{
		socket=new Socket(host, port);		
		os=socket.getOutputStream();
		is=socket.getInputStream();
		responseReader=new BufferedReader(new InputStreamReader(is));
	}
	
	public String sendFile(Path filePath) throws IOException{
		DataInputStream dis=new DataInputStream(new FileInputStream(filePath.toString()));
		byte[] fileFragment=new byte[fragmentSize];
		int length=0;
		while((length=dis.read(fileFragment)) != -1){
			os.write(fileFragment, 0, length);
		}
		String response=responseReader.readLine();
		socket.close();
		return response;
		
	}
	
}
