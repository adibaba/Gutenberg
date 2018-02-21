package de.adrianwilke.gutenberg.exceptions;

import java.io.IOException;

import de.adrianwilke.gutenberg.io.TextFileAccessor;

/**
 * Container for exceptions thrown on file access.
 * 
 * See: {@link TextFileAccessor}
 * 
 * @author Adrian Wilke
 */
public class FileAccessRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileAccessRuntimeException(IOException e) {
		super(e);
	}
}