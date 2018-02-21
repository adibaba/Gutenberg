package de.adrianwilke.gutenberg.exceptions;

import java.io.IOException;
import java.net.MalformedURLException;

import de.adrianwilke.gutenberg.io.Downloader;

/**
 * Container for exceptions thrown on download.
 * 
 * See: {@link Downloader}
 * 
 * @author Adrian Wilke
 */
public class DownloadRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DownloadRuntimeException(MalformedURLException e) {
		super(e);
	}

	public DownloadRuntimeException(IOException e) {
		super(e);
	}
}