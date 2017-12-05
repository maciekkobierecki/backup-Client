import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class BackupClient implements FileChangedListener{
	private Remote remote;
	ServerInterface server;
	JLabel infoLabel;
	Registry rmiRegistry;

	public BackupClient(JLabel info)
			throws MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		remote = Naming.lookup("backupServer");
		if (!(remote instanceof ServerInterface))
			throw new RemoteException();
		else
			server = (ServerInterface) remote;
		infoLabel = info;
		// start();
	}

	public void sendFile(String path) throws UnknownHostException {
		TransferFileConnHelper connHelper;
		try {
			connHelper = new TransferFileConnHelper(path, Config.getProperty("serverIP"),
					Integer.parseInt(Config.getProperty("port")), infoLabel, TransferFileConnHelper.UPLOAD_FUNCTION);
			new Thread(connHelper).start();
		} catch (IOException e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					infoLabel.setText("Unable to send file");
				}

			});
			e.printStackTrace();
		}
	}

	public String getFileModificationDate(String path) {
		return "1996-02-29";
	}

	public void downloadFile(FileMetadata metadata) {
		TransferFileConnHelper connHelper;
		try {
			connHelper = new TransferFileConnHelper(metadata.getFileDirectory(), Config.getProperty("serverIP"),
					Integer.parseInt(Config.getProperty("port")), infoLabel, TransferFileConnHelper.DOWNLOAD_FUNCTION,
					metadata);
			new Thread(connHelper).start();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * public void start() throws RemoteException, AlreadyBoundException{
	 * rmiRegistry=LocateRegistry.createRegistry(Integer.parseInt(Config.
	 * getProperty("clientRMIport"))); rmiRegistry.bind("client", this); }
	 * public void stop(){ try { rmiRegistry.unbind("client"); } catch
	 * (RemoteException | NotBoundException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * }
	 */

	@Override
	public void fileChanged(File file) {
		String filePath=file.getAbsolutePath();
		try {
			sendFile(filePath);
		} catch (UnknownHostException e) {
			infoLabel.setText("cant send changed file. Unknown host exception");
			e.printStackTrace();
		}
		
	}
}
