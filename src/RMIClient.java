import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import com.sun.corba.se.spi.activation.Server;

public class RMIClient {
	private ServerInterface server;
	
	public RMIClient() {
		Remote remote;
		try {
			Registry registry=LocateRegistry.getRegistry(Config.getProperty("serverIP"), Integer.parseInt(Config.getProperty("serverRMIport")));
			remote = registry.lookup("backupServer");
			if(remote instanceof ServerInterface)
				server=(ServerInterface)remote;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public ArrayList<FileMetadata> getFilesMetadata() throws RemoteException{
		ArrayList<FileMetadata> metadata=server.getFilesExistingOnServer();
		return metadata;
	}
	
}
