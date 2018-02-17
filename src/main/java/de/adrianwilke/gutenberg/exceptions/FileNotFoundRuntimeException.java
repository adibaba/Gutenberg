package de.adrianwilke.gutenberg.exceptions;

import java.io.FileNotFoundException;

/**
 * "Signals that an attempt to open the file denoted by a specified pathname has
 * failed."
 * 
 * @author Adrian Wilke
 */
public class FileNotFoundRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileNotFoundRuntimeException(FileNotFoundException e) {
		super(e);
	}

}