import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Config {
	private static File configFile;
	private static Properties prop;

	public static void init() {
		configFile = new File("config.properties");
		try {
			FileReader reader = new FileReader(configFile);
			prop = new Properties();
			prop.load(reader);
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Config file not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("err");
		}

	}

	public static String getProperty(String key) {
		try {
			return prop.getProperty(key);
		} catch (NullPointerException e) {
			return "config file error";
		}

	}
	// public static synchronized void
	// saveFileMetadataList(ArrayList<FileMetadata> metadataList){
	// try {
	// FileOutputStream fileOut=new
	// FileOutputStream(getProperty("metadataFile"));
	// ObjectOutputStream outStream=new ObjectOutputStream(fileOut);
	// outStream.writeObject(metadataList);
	// outStream.flush();
	// fileOut.close();
	// System.out.println("Metadata saved to file");
	// } catch (FileNotFoundException e) {
	// System.exit(0);
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public static synchronized void serializeArrayList(ArrayList<?> arrayToSerialize, String fileName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(getProperty(fileName));
			ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
			outStream.writeObject(arrayToSerialize);
			outStream.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			System.exit(0);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static synchronized ArrayList<FileMetadata> loadMetadataListFromFile()
//			throws IOException, ClassNotFoundException {
//		FileInputStream fileIn = new FileInputStream(getProperty("metadataFile"));
//		ObjectInputStream in = new ObjectInputStream(fileIn);
//		ArrayList<FileMetadata> metadata = (ArrayList<FileMetadata>) in.readObject();
//		return metadata;
//
//	}

	public static synchronized ArrayList<String> deserializeArrayList(String fileName)
			throws IOException, ClassNotFoundException, EOFException {
		FileInputStream fileIn = new FileInputStream(getProperty(fileName));
		ObjectInputStream in = new ObjectInputStream(fileIn);
		ArrayList<String> arrayList = (ArrayList<String>) in.readObject();
		System.out.println("Deserialized list to watch");
		return arrayList;
	}

}
