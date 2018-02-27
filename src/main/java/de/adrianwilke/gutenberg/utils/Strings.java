package de.adrianwilke.gutenberg.utils;

/**
 * String utils.
 * 
 * @author Adrian Wilke
 */
public class Strings {

	public static Integer urlToId(String url) {
		return Integer.valueOf(url.toString().substring(1 + url.toString().lastIndexOf("/")));
	}
}