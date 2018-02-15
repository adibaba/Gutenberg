package de.adrianwilke.gutenberg.exceptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import de.adrianwilke.gutenberg.download.Downloader;

/**
 * Container for exceptions thrown on download.
 * 
 * See: {@link Downloader}
 * 
 * @author Adrian Wilke
 */
public class DownloadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DownloadException(FileNotFoundException e) {
		super(e);
	}

	public DownloadException(MalformedURLException e) {
		super(e);
	}

	public DownloadException(IOException e) {
		super(e);
	}
}