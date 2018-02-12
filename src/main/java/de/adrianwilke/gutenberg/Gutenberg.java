package de.adrianwilke.gutenberg;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import de.adrianwilke.gutenberg.exceptions.SingletonException;

/**
 * Singleton holding TDB model.
 * 
 * @author Adrian Wilke
 */
public class Gutenberg {

	private static Gutenberg instance;

	public static synchronized Gutenberg getInstance() throws SingletonException {
		if (Gutenberg.instance == null) {
			throw new SingletonException("Call initialize first.");
		}
		return Gutenberg.instance;
	}

	public static synchronized Gutenberg getInstance(String directory) throws SingletonException {
		if (Gutenberg.instance == null) {
			Gutenberg.instance = new Gutenberg(directory);
		} else {
			throw new SingletonException("Can only be initialized once.");
		}
		return Gutenberg.instance;
	}

	private final String directory;

	private final Model model;

	private Gutenberg(String directory) {
		this.directory = directory;
		model = TDBFactory.createDataset(directory).getDefaultModel();
	}

	public String getDirectory() {
		return directory;
	}

	public Model getModel() {
		return model;
	}
}