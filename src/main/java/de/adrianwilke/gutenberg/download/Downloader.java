package de.adrianwilke.gutenberg.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import de.adrianwilke.gutenberg.exceptions.DownloadException;

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
	 * @throws DownloadException
	 */
	public File download(String downloadUrl) {
		return download(downloadUrl, false);
	}

	/**
	 * @throws DownloadException
	 */
	public File download(String downloadUrl, boolean overwrite) {
		try {
			return download(downloadUrl, new File(new URL(downloadUrl).getPath()).getPath(), overwrite);
		} catch (MalformedURLException e) {
			// new URL()
			throw new DownloadException(e);
		}
	}

	/**
	 * Will not download, if file path already exists.
	 * 
	 * @throws DownloadException
	 */
	public File download(String downloadUrl, String localFilePath) {
		return download(downloadUrl, localFilePath, false);
	}

	/**
	 * @throws DownloadException
	 */
	public File download(String downloadUrl, String localFilePath, boolean overwrite) {
		long time = System.currentTimeMillis();
		File downloadFile = new File(baseDownloadDirectory, localFilePath);
		if (!overwrite && downloadFile.exists()) {
			return downloadFile;
		}
		downloadFile.getParentFile().mkdirs();
		FileOutputStream stream = null;
		try {
			URL url = new URL(downloadUrl);
			ReadableByteChannel channel = Channels.newChannel(url.openStream());
			stream = new FileOutputStream(downloadFile);
			stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
		} catch (FileNotFoundException e) {
			// new FileOutputStream()
			throw new DownloadException(e);
		} catch (MalformedURLException e) {
			// new URL()
			throw new DownloadException(e);
		} catch (IOException e) {
			// URL.openStream()
			// FileChannel.transferFrom()
			throw new DownloadException(e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				// FileOutputStream.close()
				throw new DownloadException(e);
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
