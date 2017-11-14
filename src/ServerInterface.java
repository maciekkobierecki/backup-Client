import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {
		public ArrayList<FileMetadata> getFilesExistOnServer() throws RemoteException;
	}

