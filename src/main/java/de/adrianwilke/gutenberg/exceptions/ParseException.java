package de.adrianwilke.gutenberg.exceptions;

/**
 * Container for exceptions thrown on parsing.
 * 
 * @author Adrian Wilke
 */
public class ParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParseException(org.apache.jena.sparql.lang.sparql_11.ParseException e) {
		super(e);
	}
}