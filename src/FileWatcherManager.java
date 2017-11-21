import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileWatcherManager {
	private static ArrayList<FileWatcher> fileWatchers;
	private static ArrayList<String> filesToWatch;
	private static BackupClient backupClient;

	public static void init(BackupClient bcpclient) throws ClassNotFoundException, IOException {
		fileWatchers = new ArrayList<FileWatcher>();
		backupClient = bcpclient;
		filesToWatch = Config.deserializeArrayList("watchedFilesList");
		watchFilesFromWatchList();
	}

	static public void addWatcher(String uploadPath, BackupClient backupClient) {
		if (!filesToWatch.contains(uploadPath)) {
			FileWatcher filewatcher = new FileWatcher(new File(uploadPath), (FileChangedListener) backupClient);
			fileWatchers.add(filewatcher);
			Thread t = new Thread((Runnable) filewatcher);
			t.start();
		}
	}

	static public void removeWatcher(String filename) {
		for (FileWatcher watcher : fileWatchers) {
			if (watcher.getFilename().equals(filename)) {
				watcher.stopThread();
				// Czy to nie jest nierozsądne?
				filesToWatch.remove(watcher.getFilename());
				fileWatchers.remove(watcher);
			}
		}
	}

	private static void watchFilesFromWatchList() {
		for (String path : filesToWatch) {
			addWatcher(path, backupClient);
		}
	}

	static public void serializeWatchList() {
		Config.serializeArrayList(filesToWatch);
	}

	static public void saveFilesToWatchList(ArrayList<FileMetadata> filesOnServer) {
		String directory;
		for (FileMetadata fileMetadata : filesOnServer) {
			directory = fileMetadata.getFileDirectory();
			if (!filesToWatch.contains(directory))
				addWatcher(directory, backupClient);
		}

	}

}
