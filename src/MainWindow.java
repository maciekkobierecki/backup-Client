import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainWindow extends JFrame implements FileUploadedListener{
	private FileTable onServerTable;
	private JButton uploadButton;
	private JButton downloadButton;
	private JButton stopArchivization;
	private BackupClient backupClient;
	private JLabel infoLabel;
	private JFileChooser fileChooser;
	private RMIClient rmiClient;

	public MainWindow() {
		super();
		rmiClient = new RMIClient();
		uploadButton = new JButton(Config.getProperty("UploadButtonLabel"));
		downloadButton = new JButton(Config.getProperty("downloadButtonLabel"));
		stopArchivization = new JButton(Config.getProperty("stopArchivizationButtonLabel"));
		this.setLayout(new BorderLayout());
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new GridLayout(1, 3));
		menuPanel.add(uploadButton);
		menuPanel.add(downloadButton);
		menuPanel.add(stopArchivization);
		stopArchivization.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileMetadata metadata = onServerTable.getSelectedRow();
				onServerTable.removeMetadata(metadata);
				try {
					rmiClient.stopArchivization(metadata);
					FileWatcherManager.removeWatcher(metadata.getFileDirectory());
					refreshFileTable();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		downloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileMetadata selected = onServerTable.getSelectedRow();
				backupClient.downloadFile(selected);
			}
		});

		fileChooser = new JFileChooser();
		fileChooser.setApproveButtonText(Config.getProperty("UploadButtonLabel"));
		infoLabel = new JLabel("nothing");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent wEvent) {
				System.exit(0);
			}
		});

		try {
			backupClient = new BackupClient(infoLabel,this);
			FileWatcherManager.init(backupClient);
		} catch (IOException | ClassNotFoundException | NotBoundException | AlreadyBoundException e) {
			infoLabel.setText("error");
			e.printStackTrace();
		} 		

		add(menuPanel, BorderLayout.PAGE_END);
		JFrame frame = this;

		uploadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					int returnedValue = fileChooser.showOpenDialog(frame);
					if (returnedValue == JFileChooser.APPROVE_OPTION) {
						String uploadPath = (fileChooser).getSelectedFile().getAbsolutePath();
						backupClient.sendFile(uploadPath);
						FileWatcherManager.addWatcher(uploadPath);
						refreshFileTable();
					} else
						System.out.println("cancel was selected");
				} catch (UnknownHostException e) {
					infoLabel.setText("unable to connect with server");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
		try {
			onServerTable = new FileTable(rmiClient.getFilesMetadata());
			add(onServerTable);
		} catch (RemoteException e1) {
			infoLabel.setText("Unable to connect with server.");
		}
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				onServerTable.saveData();
				System.out.println("Closing.... ");
				FileWatcherManager.serializeWatchList();
			}
		});
		pack();
		setVisible(true);
		refreshFileTable();
	}
	

	public void refreshFileTable() {
		try {
			ArrayList<FileMetadata> fetchedMetadata = rmiClient.getFilesMetadata();
			onServerTable.replace(fetchedMetadata);
			onServerTable.dataChanged();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void fileUploaded() {
		refreshFileTable();
		
	}

}
