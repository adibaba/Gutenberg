package de.adrianwilke.gutenberg.exceptions;

import org.apache.jena.sparql.lang.sparql_11.ParseException;

/**
 * This exception is thrown when parse errors are encountered.
 * 
 * @author Adrian Wilke
 */
public class ParseRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParseRuntimeException(ParseException e) {
		super(e);
	}
}