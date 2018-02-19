package de.adrianwilke.gutenberg.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.adrianwilke.gutenberg.exceptions.FileNotFoundRuntimeException;
import de.adrianwilke.gutenberg.exceptions.FileSerializerRuntimeException;

/**
 * Handles serialization and file operations.
 * 
 * @author Adrian Wilke
 */
public class FileSerializer {

	private File baseSerializationDirectory;

	public FileSerializer(String baseSerializationDirectory) {
		this.baseSerializationDirectory = new File(baseSerializationDirectory);
	}

	public File getBaseSerializationDirectory() {
		return baseSerializationDirectory;
	}

	/**
	 * @throws FileNotFoundRuntimeException
	 * @throws FileSerializerRuntimeException
	 */
	public Object read(String filePath) {

		File file = new File(baseSerializationDirectory, filePath);

		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Object object = null;
		try {
			fileInputStream = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(fileInputStream);
			object = objectInputStream.readObject();
		} catch (FileNotFoundException e) {
			// new FileOutputStream()
			throw new FileNotFoundRuntimeException(e);
		} catch (IOException e) {
			// new ObjectInputStream()
			throw new FileSerializerRuntimeException(e);
		} catch (ClassNotFoundException e) {
			// ObjectInputStream.readObject()
			throw new FileSerializerRuntimeException(e);
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				// FileInputStream.close()
				// ObjectInputStream.close()
				throw new FileSerializerRuntimeException(e);
			}
		}
		return object;
	}

	/**
	 * @throws FileNotFoundRuntimeException
	 * @throws FileSerializerRuntimeException
	 */
	public void write(String filePath, Object object) {

		File file = new File(baseSerializationDirectory, filePath);

		ObjectOutputStream objectOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(object);
		} catch (FileNotFoundException e) {
			// new FileOutputStream()
			throw new FileNotFoundRuntimeException(e);
		} catch (IOException e) {
			// new ObjectOutputStream(fileOutputStream)
			// ObjectOutputStream
			throw new FileSerializerRuntimeException(e);
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
				if (objectOutputStream != null) {
					objectOutputStream.close();
				}
			} catch (IOException e) {
				// FileOutputStream.close()
				// ObjectOutputStream.close()
				throw new FileSerializerRuntimeException(e);
			}
		}
	}
}