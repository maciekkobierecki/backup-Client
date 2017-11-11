import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRMIInterface extends Remote {
	public String getFileModificationDate(String Path) throws RemoteException;
}