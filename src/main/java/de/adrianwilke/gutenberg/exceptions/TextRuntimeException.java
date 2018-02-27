package de.adrianwilke.gutenberg.exceptions;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Thrown on exceptions in {@link Text}.
 *  * 
 * @author Adrian Wilke
 */
public class TextRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    public TextRuntimeException(String message) {
        super(message);
    }
}
