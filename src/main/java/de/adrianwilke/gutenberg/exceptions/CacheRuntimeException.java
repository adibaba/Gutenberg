package de.adrianwilke.gutenberg.exceptions;

import de.adrianwilke.gutenberg.entities.Cache;

/**
 * Container for exceptions thrown on cache access.
 * 
 * See: {@link Cache}
 * 
 * @author Adrian Wilke
 */
public class CacheRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CacheRuntimeException(Exception e) {
		super(e);
	}
}