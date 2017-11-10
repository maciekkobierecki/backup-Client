import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class BrowserWindow extends JFrame{
	
	private JFileChooser fileChooser;
	
	public BrowserWindow(){
		super();
		fileChooser=new JFileChooser();
		this.add(fileChooser);
		fileChooser.setApproveButtonText(Config.getProperty("UploadButtonLabel"));
		pack();
		setVisible(true);
		
	}
}
