package de.adrianwilke.gutenberg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.tdb.TDBFactory;

/**
 * Imports Gutenberg RDF/XML files into Jena TDB.
 * 
 * Test took 30 Minutes and generated TDB with size of 1.5 GB.
 * 
 * @author Adrian Wilke
 */
public class Importer {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println("Please provide two arguments:");
			System.err.println("- Directory to store database");
			System.err.println("- Directory of Gutenberg RDF/XML files");
			System.err.println("  (Get them at http://www.gutenberg.org/wiki/Gutenberg:Feeds)");
			System.exit(1);
		}

		File gutenbergDirectory = new File(args[0]);
		if (!gutenbergDirectory.isDirectory() || !gutenbergDirectory.canRead()) {
			System.err.println("Can not read Gutenberg directory: " + gutenbergDirectory);
			System.exit(1);
		}

		File databaseDirectory = new File(args[1]);
		if (!databaseDirectory.exists()) {
			if (!databaseDirectory.mkdirs()) {
				System.err.println("Can not create database directory: " + databaseDirectory);
				System.exit(1);
			}
		}
		if (!databaseDirectory.isDirectory() || !databaseDirectory.canRead()) {
			System.err.println("Can not read database directory: " + databaseDirectory);
			System.exit(1);
		}

		new Importer().importDirectory(gutenbergDirectory.getPath(), databaseDirectory.getPath());
	}

	protected Dataset connect(String tdbDirectory) {
		return TDBFactory.createDataset(tdbDirectory);
	}

	protected Model getModel(String file) {
		Model model = ModelFactory.createDefaultModel();
		model.read(file);
		return model;
	}

	protected List<String> getRdfDirectories(String directory) {

		File rootDirectory = new File(directory);
		List<String> subDirectories = new LinkedList<String>();

		for (String subDirectory : rootDirectory.list()) {
			String rdfFile = getRdfFile(rootDirectory.getPath(), subDirectory);
			if (new File(rdfFile).canRead()) {
				subDirectories.add(subDirectory);
			} else {
				handleError("Could not find file: " + rdfFile);
			}
		}

		return subDirectories;
	}

	protected String getRdfFile(String rootDirectory, String fileId) {
		return rootDirectory + File.separator + fileId + File.separator + "pg" + fileId + ".rdf";
	}

	protected void handleError(String string) {
		System.err.println(string);
	}

	public void importDirectory(String gutenbergDirectory, String databaseDirectory) {
		long time = System.currentTimeMillis();

		Dataset dataset = connect(databaseDirectory);

		logln("Importing: " + gutenbergDirectory);
		List<String> rdfDirectories = getRdfDirectories(gutenbergDirectory);

		int lastPercent = 0;
		for (int i = 0; i < rdfDirectories.size(); i++) {
			String rdfFile = getRdfFile(gutenbergDirectory, rdfDirectories.get(i));
			Model fileModel = getModel(rdfFile);
			importModel(dataset, fileModel);

			int percent = i * 100 / rdfDirectories.size();
			if (percent > lastPercent + 1) {
				lastPercent += 1;
				log(lastPercent + "% ");
				if (lastPercent % 10 == 0) {
					logln("");
				}
			}
		}
		log("99%");
		logln("100%");
		logln("Minutes: " + (System.currentTimeMillis() - time) / (1000f * 60f));
		logln("TDB: " + databaseDirectory);
	}

	protected void importModel(Dataset dataset, Model model) {
		dataset.begin(ReadWrite.READ);
		Model datasetModel = dataset.getDefaultModel();
		dataset.end();

		dataset.begin(ReadWrite.WRITE);
		try {
			datasetModel.add(model);
			datasetModel.commit();
		} finally {
			dataset.end();
		}
	}

	protected void log(String string) {
		System.out.print(string);
	}

	protected void logln(String string) {
		System.out.println(string);
	}

	protected void printRdfXml(Model model) {
		model.write(System.out, RDFLanguages.strLangRDFXML);
	}

	protected void printTurtle(Model model) {
		model.write(System.out, RDFLanguages.strLangTurtle);
	}
}