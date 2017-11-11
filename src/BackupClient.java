import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class BackupClient implements ClientRMIInterface{
	private Remote remote;
	ServerInterface server;
	JLabel infoLabel;
	Registry rmiRegistry;
	public BackupClient(JLabel info) throws MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException{
		remote=Naming.lookup("backupServer");
		if(!(remote instanceof ServerInterface))
			throw new RemoteException();
		else
			server=(ServerInterface)remote;
		infoLabel=info;
		start();
	}
	public String getModificationDate(String fileLocalPath) throws RemoteException{
		return server.lastModificationDate(fileLocalPath);
	}
	public void sendFile(String path) throws UnknownHostException{
		SendFileHelper sfHelper;
			try {
				sfHelper = new SendFileHelper(path,"localhost", 1010, infoLabel);
				new Thread(sfHelper).start();
			} catch (IOException e) {
				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {
						infoLabel.setText("Unable to send file");
						
					}
					
				});
				e.printStackTrace();
			}

		

	}
	
	public String getFileModificationDate(String path){
		return "1996-02-29";
	}
	public void start() throws RemoteException, AlreadyBoundException{
		rmiRegistry=LocateRegistry.createRegistry(Integer.parseInt(Config.getProperty("RMIport")));
		rmiRegistry.bind("client", this);
	}
}
