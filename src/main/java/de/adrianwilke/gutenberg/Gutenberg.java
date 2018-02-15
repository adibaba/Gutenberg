package de.adrianwilke.gutenberg;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import de.adrianwilke.gutenberg.exceptions.SingletonException;

/**
 * Singleton holding TDB model.
 * 
 * Can also hold directory for downloads.
 * 
 * @author Adrian Wilke
 */
public class Gutenberg {

	private static Gutenberg instance;
	
	public static synchronized Gutenberg getInstance() throws SingletonException {
		if (Gutenberg.instance == null) {
			throw new SingletonException("Call initialization constructor first.");
		}
		return Gutenberg.instance;
	}

	public static synchronized Gutenberg getInstance(String tdbDirectory) throws SingletonException {
		if (Gutenberg.instance == null) {
			Gutenberg.instance = new Gutenberg(tdbDirectory);
		} else {
			throw new SingletonException("Can only be initialized once.");
		}
		return Gutenberg.instance;
	}

	private final String tdbDirectory;

	private String downloadDirectory;

	private final Model model;

	private Gutenberg(String tdbDirectory) {
		this.tdbDirectory = tdbDirectory;
		model = TDBFactory.createDataset(tdbDirectory).getDefaultModel();
	}

	public String getDirectory() {
		return tdbDirectory;
	}

	public String getDownloadDirectory() {
		return downloadDirectory;
	}

	public Model getModel() {
		return model;
	}

	public void setDownloadDirectory(String downloadDirectory) {
		this.downloadDirectory = downloadDirectory;
	}
}