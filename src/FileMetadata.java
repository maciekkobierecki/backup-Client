import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileMetadata implements Serializable {
	private static final int EXTENSION_POSITION = 1;
	private static final long serialVersionUID = -994332227976061506L;
	private File file;
	private String date;
	private String onServerName;

	public FileMetadata(String onServerName, String fileDirectory, String date) {
		file = new File(fileDirectory);
		this.date = date;
		this.onServerName = onServerName;
	}

	public void setDate(String date) {
		// ROZWAZYC ZMIANE DATE NA TYP DATE
	}

	public String getOnServerName() {
		return onServerName;
	}

	public String getFileDirectory() {
		return file.getAbsolutePath();
	}

	public String getDate() {
		return date;
	}

	public String getFileName() {
		return file.getName();
	}

	public String getFileExtension() {
		String fileName = getFileName();
		String[] data = fileName.split("\\.");
		return data[EXTENSION_POSITION];
	}

	// date format must be dd/MM/yyyy HH:ss:mm
	public Boolean isOlderThan(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:ss:mm");
		Date myDate = sdf.parse(this.date);
		Date foreignDate = sdf.parse(date);
		if (myDate.compareTo(foreignDate) < 0)
			return true;
		else
			return false;

	}
}