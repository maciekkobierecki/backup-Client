import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {
	private String[] columnNames = { "File Name", "file path", "modification date" };
	private ArrayList<FileMetadata> filesOnServer;

	public FileTableModel(ArrayList<FileMetadata> files) {
		filesOnServer = files;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return filesOnServer.size();
	}

	public FileMetadata getSelectedMetadataObject(JTable table) {
		int row = table.getSelectedRow();
		int modelRow = table.convertRowIndexToModel(row);
		return filesOnServer.get(modelRow);
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object temp = null;
		switch (col) {
		case 0:
			temp = filesOnServer.get(row).getFileName();
			break;
		case 1:
			temp = filesOnServer.get(row).getFileDirectory();
			break;
		case 2:
			temp = filesOnServer.get(row).getDate();
			break;
		}
		return temp;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Class getColumnClass(int col) {
		Object temp;
		temp = getValueAt(0, col);
		return temp.getClass();
	}

	public void replace(ArrayList<FileMetadata> metadata) {
		filesOnServer = metadata;

	}

	public void removeMetadata(FileMetadata metadata) {
		filesOnServer.remove(metadata);

	}

	public void saveData() {
		// Config.saveFileMetadataList(filesOnServer);
		Config.serializeArrayList(filesOnServer, "metadataFile");

	}


}
