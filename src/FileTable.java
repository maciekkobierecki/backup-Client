import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class FileTable extends JPanel{
	private FileTableModel model;
	public FileTable(ArrayList<FileMetadata> metadata){
		super(new GridLayout(1,0));
		model=new FileTableModel(metadata);
		JTable table=new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(500,70));
		JScrollPane scrollPane=new JScrollPane(table);
		add(scrollPane);
	}
	public void replace(ArrayList<FileMetadata> metadata){
			model.replace(metadata);
	}
	public void dataChanged(){
		model.fireTableDataChanged();
	}
	
}
