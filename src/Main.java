import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Main {

	public static void main(String[] args) {
		Config.init();
		new MainWindow();
		String url="rmi://localhost/server";
		try {
			Remote remote=Naming.lookup("backupServer");
			ServerInterface server=null;
			if(remote instanceof ServerInterface)
				server=(ServerInterface)remote;
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
