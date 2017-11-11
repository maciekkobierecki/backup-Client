import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class MainWindow extends JFrame {
	private JTable onServerTable;
	private JButton uploadButton;
	private JButton refreshButton;
	private BackupClient backupClient;
	private JLabel infoLabel;
	public MainWindow(){
		super();
		onServerTable=new JTable();
		uploadButton=new JButton(Config.getProperty("UploadButtonLabel"));
		refreshButton=new JButton(Config.getProperty("refreshButtonLabel"));
		this.setLayout(new BorderLayout());
		onServerTable.setPreferredSize(new Dimension(300,200));
		JPanel menuPanel=new JPanel();
		menuPanel.setLayout(new GridLayout(1,2));
		menuPanel.add(refreshButton);
		menuPanel.add(uploadButton,2,1);
		infoLabel=new JLabel("nothing");
		add(onServerTable);
		try {
			backupClient=new BackupClient(infoLabel);
		} catch (MalformedURLException | RemoteException | NotBoundException | AlreadyBoundException e) {
			infoLabel.setText("error");
			e.printStackTrace();
		}
		add(menuPanel, BorderLayout.PAGE_END);
		uploadButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new BrowserWindow(backupClient, infoLabel);
				
			}
			
		});
		pack();
		setVisible(true);
	}

}
