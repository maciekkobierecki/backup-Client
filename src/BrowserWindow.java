import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class BrowserWindow extends JFrame{
	
	private JFileChooser fileChooser;
	
	public BrowserWindow(){
		super();
		fileChooser=new JFileChooser();
		this.add(fileChooser);
		fileChooser.setApproveButtonText(Config.getProperty("UploadButtonLabel"));
		fileChooser.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String updateLabel=Config.getProperty("UploadButtonLabel");
				String cancelLabel=Config.getProperty("Cancel");
				String source=((JButton) arg0.getSource()).getText();
				if(source.equals(updateLabel)){
					//rozpocznij wrzucanie
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
