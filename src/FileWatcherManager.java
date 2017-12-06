import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileWatcherManager {
	private static ArrayList<FileWatcher> fileWatchers;
	private static ArrayList<String> filesToWatch;
	private static BackupClient backupClient;

	public static void init(BackupClient bcpclient) throws ClassNotFoundException, IOException {
		fileWatchers = new ArrayList<FileWatcher>();
		filesToWatch = new ArrayList<String>();
		backupClient = bcpclient;
		try {
			watchFilesFromWatchList(Config.deserializeArrayList("watchedFilesList"));

		} catch (EOFException e) {
			e.printStackTrace();
		}

	}

	static public void addWatcher(String uploadPath) {

			if (!filesToWatch.contains(uploadPath)) {
				System.out.println("Adding to watchlist: " + uploadPath);
				FileWatcher filewatcher = new FileWatcher(new File(uploadPath), (FileChangedListener) backupClient);
				filesToWatch.add(uploadPath);
				fileWatchers.add(filewatcher);
				Thread t = new Thread((Runnable) filewatcher);
				t.start();
				serializeWatchList();
			}
	}

	static public void removeWatcher(String filepath) {
		
		FileWatcher toBeDeleted = new FileWatcher(null, backupClient);
		
		for (FileWatcher watcher : fileWatchers) {
			if (watcher.getPath().equals(filepath)) {
				System.out.println("Stopped following: " + filepath);
				toBeDeleted = watcher;
				
			}
		}
		filesToWatch.remove(filepath);
		fileWatchers.remove(toBeDeleted);
		toBeDeleted.stopThread();
		serializeWatchList();
	}

	private static void watchFilesFromWatchList(ArrayList<String> deserialized) {
		for (String path : deserialized) {
			System.out.println("From watch list: " + path);
			addWatcher(path);
		}
	}

	static public void serializeWatchList() {
		System.out.println("Saving.... ");
		Config.serializeArrayList(filesToWatch, "watchedFilesList");
	}

}
