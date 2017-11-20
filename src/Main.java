import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Main {

	public static void main(String[] args) {
		// if(System.getSecurityManager()==null)
		// System.setSecurityManager(new SecurityManager());
		Config.init();
		new MainWindow();

	}

}
