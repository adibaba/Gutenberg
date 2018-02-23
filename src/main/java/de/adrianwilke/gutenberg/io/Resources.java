package de.adrianwilke.gutenberg.io;

import java.io.File;

/**
 * Handles resources.
 * 
 * @author Adrian Wilke
 */
public class Resources {

	public static File getResource(String resourceName) {
		ClassLoader classLoader = Resources.class.getClassLoader();
		return new File(classLoader.getResource(resourceName).getFile());
	}

}