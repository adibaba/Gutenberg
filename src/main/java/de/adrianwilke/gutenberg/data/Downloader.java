package de.adrianwilke.gutenberg.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import de.adrianwilke.gutenberg.exceptions.DownloadRuntimeException;
import de.adrianwilke.gutenberg.exceptions.FileNotFoundRuntimeException;

/**
 * Downloads URL data.
 * 
 * @author Adrian Wilke
 */
public class Downloader {
	
	public static final boolean PRINT_TIME = false;
	
	private File baseDownloadDirectory;

	public Downloader(String baseDownloadDirectory) {
		this.baseDownloadDirectory = new File(baseDownloadDirectory);
	}

	/**
	 * Will not download, if file path already exists.
	 * 
	 * @throws DownloadRuntimeException
	 * @throws FileNotFoundRuntimeException
	 */
	public File download(String downloadUrl) {
		return download(downloadUrl, false);
	}

	/**
	 * @throws DownloadRuntimeException
	 * @throws FileNotFoundRuntimeException
	 */
	public File download(String downloadUrl, boolean overwrite) {
		try {
			return download(downloadUrl, new File(new URL(downloadUrl).getPath()).getPath(), overwrite);
		} catch (MalformedURLException e) {
			// new URL()
			throw new DownloadRuntimeException(e);
		}
	}

	/**
	 * Will not download, if file path already exists.
	 * 
	 * @throws DownloadRuntimeException
	 * @throws FileNotFoundRuntimeException
	 */
	public File download(String downloadUrl, String localFilePath) {
		return download(downloadUrl, localFilePath, false);
	}

	/**
	 * @throws DownloadRuntimeException
	 * @throws FileNotFoundRuntimeException
	 */
	public File download(String downloadUrl, String localFilePath, boolean overwrite) {
		long time = System.currentTimeMillis();
		
		// Set file to download
		File downloadFile = new File(baseDownloadDirectory, localFilePath);
		
		// Use existing file
		if (!overwrite && downloadFile.exists()) {
			return downloadFile;
		}
		
		// Create directories, if not existent
		downloadFile.getParentFile().mkdirs();
		
		// Download
		FileOutputStream stream = null;
		try {
			URL url = new URL(downloadUrl);
			ReadableByteChannel channel = Channels.newChannel(url.openStream());
			stream = new FileOutputStream(downloadFile);
			stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
		} catch (FileNotFoundException e) {
			// new FileOutputStream()
			throw new FileNotFoundRuntimeException(e);
		} catch (MalformedURLException e) {
			// new URL()
			throw new DownloadRuntimeException(e);
		} catch (IOException e) {
			// URL.openStream()
			// FileChannel.transferFrom()
			throw new DownloadRuntimeException(e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				// FileOutputStream.close()
				throw new DownloadRuntimeException(e);
			}
		}
		
		if (PRINT_TIME) {
			System.out.println((System.currentTimeMillis() - time) / 1000 + " seconds");
		}
		
		return downloadFile;
	}

	public File getBaseDownloadDirectory() {
		return baseDownloadDirectory;
	}
}