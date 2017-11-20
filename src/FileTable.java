import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class FileTable extends JPanel{
	private FileTableModel model;
	private JTable table;
	public FileTable(ArrayList<FileMetadata> metadata){
		super(new GridLayout(1,0));
		model=new FileTableModel(metadata);
		table=new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(500,70));
		JScrollPane scrollPane=new JScrollPane(table);
		add(scrollPane);
	}
	public void replace(ArrayList<FileMetadata> metadata){
			model.replace(metadata);
	}
	public FileMetadata getSelectedRow(){
		FileMetadata selectedMetadata=model.getSelectedMetadataObject(table);
		return selectedMetadata;
	}
	public void removeMetadata(FileMetadata metadata){
		model.removeMetadata(metadata);
		dataChanged();
	}
	public void dataChanged(){
		model.fireTableDataChanged();
	}
	public void saveData() {
		model.saveData();
		
	}
	
}
