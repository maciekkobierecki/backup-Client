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
			System.out.println("Adding to watchlist: " + uploadPath);	
			FileWatcher filewatcher = new FileWatcher(new File(uploadPath), (FileChangedListener) backupClient);
			filesToWatch.add(uploadPath);
			fileWatchers.add(filewatcher);
			Thread t = new Thread((Runnable) filewatcher);
			t.start();
		}
	}

	static public void removeWatcher(String filepath) {
		for (FileWatcher watcher : fileWatchers) {
			if (watcher.getFilename().equals(filepath)) {
				filesToWatch.remove(filepath);
				fileWatchers.remove(watcher);
				watcher.stopThread();
			}
		}
	}

	private static void watchFilesFromWatchList() {
		for (String path : filesToWatch) {
			System.out.println("From watch list: " + path);
			addWatcher(path, backupClient);
		}
	}

	static public void serializeWatchList() {
		// serializuj(filesToWatch)
		
	}

}
