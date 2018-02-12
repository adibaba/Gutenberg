package de.adrianwilke.gutenberg.exceptions;

import de.adrianwilke.gutenberg.Gutenberg;

/**
 * Exception thrown on wrong singleton instantiation.
 * 
 * See: {@link Gutenberg#getInstance()} and
 * {@link Gutenberg#getInstance(String)}
 * 
 * @author Adrian Wilke
 */
public class SingletonException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	public SingletonException(String message) {
		super(message);
	}
}
