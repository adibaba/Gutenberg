package de.adrianwilke.gutenberg.exceptions;

/**
 * Thrown on access attempts on a non-existent parent of a text.  
 * 
 * @author Adrian Wilke
 */
public class TextRootRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TextRootRuntimeException() {
		super();
	}
}