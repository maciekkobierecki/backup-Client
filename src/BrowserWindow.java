import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class BrowserWindow extends JFrame{
	
	private JFileChooser fileChooser;
	private JLabel infoLabel;
	public BrowserWindow(BackupClient backupClient, JLabel infoLabel){
		super();
		fileChooser=new JFileChooser();
		this.add(fileChooser);
		this.infoLabel=infoLabel;
		fileChooser.setApproveButtonText(Config.getProperty("UploadButtonLabel"));
		fileChooser.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String updateLabel=Config.getProperty("UploadButtonLabel");
				String cancelLabel=Config.getProperty("Cancel");
				String source=((JButton) arg0.getSource()).getText();
				if(source.equals(updateLabel)){
					try {
						backupClient.sendFile(((JFileChooser)arg0.getSource()).getSelectedFile().getAbsolutePath());
					} catch (UnknownHostException e) {
						infoLabel.setText("unable to connect with server");
						e.printStackTrace();
					}
				}
				else if(source.equals(cancelLabel)){
					setVisible(false);
					dispose();
				}
				
			}
			
		});
		pack();
		setVisible(true);
		
	}
}
