import java.io.File;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

interface FileChangedListener {
	public void fileChanged(File file);
}

public class FileWatcher implements Runnable, Serializable {
	private FileChangedListener listener;
	private final File file;
	private AtomicBoolean stop = new AtomicBoolean(false);

	public FileWatcher(File file, FileChangedListener listener) {
		this.file = file;
		this.listener = listener;
	}

	public boolean isStopped() {
		return stop.get();
	}

	public void stopThread() {
		stop.set(true);
	}

	public void doOnChange() {
		listener.fileChanged(file);
		System.out.println(file.getName() + " changed!");
	}

	public String getFilename() {
		return file.getName();
	}
	
	public String getPath() {
		return file.getAbsolutePath();
	}

	@Override
	public void run() {
		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
			Path path = file.toPath().getParent();
			path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
			while (!isStopped()) {
				WatchKey key;
				try {
					key = watcher.poll(25, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					return;
				}
				if (key == null) {
					Thread.yield();
					continue;
				}
				Thread.sleep(50);
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();

					if (kind == StandardWatchEventKinds.OVERFLOW) {
						Thread.yield();
						continue;
					} else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
							&& filename.toString().equals(file.getName())) {
						doOnChange();
					} else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
							&& filename.toString().equals(file.getName())) {
						stopThread();
					}
					boolean valid = key.reset();
					if (!valid) {
						break;
					}
				}
				Thread.yield();
			}
		} catch (Throwable e) {
			// Log or rethrow the error
		}
	}
}