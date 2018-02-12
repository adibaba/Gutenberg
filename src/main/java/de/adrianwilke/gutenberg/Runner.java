package de.adrianwilke.gutenberg;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import de.adrianwilke.gutenberg.entities.Author;
import de.adrianwilke.gutenberg.entities.DcType;
import de.adrianwilke.gutenberg.entities.Ebook;
import de.adrianwilke.gutenberg.entities.Language;
import de.adrianwilke.gutenberg.entities.Node;

/**
 * Execution endpoints.
 * 
 * To import Gutenberg RDF/XML files, download archive at
 * http://www.gutenberg.org/wiki/Gutenberg:Feeds (e.g.
 * http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.bz2).
 * 
 * Configure directory for extracted RDF/XML files
 * {@link Runner#DIRECTORY_RDF_FILES} and directory to store Jena TDB
 * {@link Runner#DIRECTORY_TDB}.
 * 
 * To execute example code, set EXEC-constants to 1 (e.g. to import RDF/XML
 * files use {@link Runner#EXEC_IMPORT_RDF_FILES}).
 * 
 * @author Adrian Wilke
 */
public class Runner {

	public static final int ALICE_FILE_ID = 19778;
	public static final String ALICE_URI = "http://www.gutenberg.org/ebooks/" + ALICE_FILE_ID;

	// TODO: Please set directories here
	public static final String DIRECTORY_RDF_FILES = "/tmp/rdf-files-gutenberg/epub";
	public static final String DIRECTORY_TDB = "/tmp/rdf-files-gutenberg/tdb";

	public static int EXEC_IMPORT_RDF_FILES = 1;
	public static int EXEC_IMPORTER_PRINT_RDF_FILE = 0;
	public static int EXEC_PRINT_AUTHOR_INFORMATION = 0;
	public static int EXEC_PRINT_DC_TYPE_INFORMATION = 0;
	public static int EXEC_PRINT_EBOOK_INFORMATION = 0;
	public static int EXEC_PRINT_ITEMS_IN_LANGUAGE = 0;
	public static int EXEC_PRINT_LANGUAGE_INFORMATION = 0;
	public static int EXEC_PRINT_NODE_CONTEXT = 0;
	public static int EXEC_PRINT_SIMILAR_EBOOKS = 0;

	public static void main(String[] args) throws Exception {

		// Initialize TDB
		Gutenberg.getInstance(DIRECTORY_TDB);

		Runner runner = new Runner();

		if (0 != EXEC_IMPORT_RDF_FILES) {
			String[] arguments = new String[2];
			arguments[0] = DIRECTORY_RDF_FILES;
			arguments[1] = DIRECTORY_TDB;

			// if ("To import".equals(null)) {
			Importer.main(args);
			// } else {
			// throw new Exception("Be sure to import.");
			// }
		}

		if (0 != EXEC_IMPORTER_PRINT_RDF_FILE) {
			runner.printImporterRdfFile(DIRECTORY_RDF_FILES, ALICE_FILE_ID);
		}

		if (0 != EXEC_PRINT_AUTHOR_INFORMATION) {
			runner.printAuthorInformation(new Ebook(ALICE_URI));
		}

		if (0 != EXEC_PRINT_DC_TYPE_INFORMATION) {
			runner.printDcTypeInformation();
		}

		if (0 != EXEC_PRINT_EBOOK_INFORMATION) {
			runner.printEbookInformation();
		}

		if (0 != EXEC_PRINT_ITEMS_IN_LANGUAGE) {
			runner.printItemsInLanguage(new Language(Language.LANG_DE));
		}

		if (0 != EXEC_PRINT_LANGUAGE_INFORMATION) {
			runner.printLanguageInformation();
		}

		if (0 != EXEC_PRINT_NODE_CONTEXT) {
			runner.printNodeContext(new Node(ALICE_URI));
		}

		if (0 != EXEC_PRINT_SIMILAR_EBOOKS) {
			runner.printSimilarEbooks(new Ebook(ALICE_URI));
		}
	}

	public void printAuthorInformation(Ebook ebook) {
		Author author = ebook.getCreator();
		System.out.println("String: " + author);
		System.out.println("Name: " + author.getName());
		System.out.println("Alias: " + author.getAlias());
		System.out.println("Birthdate: " + author.getBirthdate());
		System.out.println("Deathdate: " + author.getDeathdate());
		System.out.println("Webpage: " + author.getWebpage());
		System.out.println("URI: " + author.getUri());
		System.out.println();
	}

	private void printDcTypeInformation() {

		// Get all DC types
		for (String string : DcType.getDcTypes()) {
			System.out.println(string);
		}
		System.out.println();

		// Get individual DC Types
		System.out.println("TEXT: " + new DcType(DcType.TEXT).getResourcesByDcType().size());
		System.out.println("SOUND: " + new DcType(DcType.SOUND).getResourcesByDcType().size());
		System.out.println("DATASET: " + new DcType(DcType.DATASET).getResourcesByDcType().size());
		System.out.println("IMAGE: " + new DcType(DcType.IMAGE).getResourcesByDcType().size());
		System.out.println("COLLECTION: " + new DcType(DcType.COLLECTION).getResourcesByDcType().size());
		System.out.println("STILL_IMAGE: " + new DcType(DcType.STILL_IMAGE).getResourcesByDcType().size());
		System.out.println("MOVING_IMAGE: " + new DcType(DcType.MOVING_IMAGE).getResourcesByDcType().size());
		System.out.println();

		System.out.println(DcType.MOVING_IMAGE);
		for (Resource resource : new DcType(DcType.MOVING_IMAGE).getResourcesByDcType()) {
			System.out.println(resource);
		}
		System.out.println();
	}

	public void printEbookInformation() {
		System.out.println();

		// Sizes of collections
		Language language = new Language(Language.LANG_DE);
		DcType dcTypeText = new DcType(DcType.TEXT);
		DcType dcTypeSound = new DcType(DcType.SOUND);

		System.out.println("Total number of Ebooks: " + Ebook.getEbooks().size());
		System.out.println("Number of Ebooks in language " + language + ": " + Ebook.getEbooks(language).size());

		System.out.println("Number of Ebooks in language "
				+ language
				+ " and type "
				+ dcTypeText
				+ ": "
				+ Ebook.getEbooks(language, dcTypeText).size());

		System.out.println("Number of Ebooks in language "
				+ language
				+ " and type "
				+ dcTypeSound
				+ ": "
				+ Ebook.getEbooks(language, dcTypeSound).size());
		System.out.println();

		System.out.println("Ebooks in language " + language + " and type " + dcTypeSound + ": ");
		for (RDFNode rdfNode : Ebook.getEbooks(language, dcTypeSound)) {
			System.out.println(rdfNode);
		}

		System.out.println();
	}

	public void printImporterRdfFile(String gutenbergDirectory, int fileId) {
		Importer importer = new Importer();
		String filePath = importer.getRdfFile(DIRECTORY_RDF_FILES, Integer.toString(ALICE_FILE_ID));
		Model model = importer.getModel(filePath);
		importer.printTurtle(model);
	}

	public void printItemsInLanguage(Language lang) {

		System.out.println("RDF nodes with language " + lang);
		List<RDFNode> rdfNodes = lang.getResourcesByLanguage();
		for (int j = 0; j < 5; j++) {
			System.out.println(rdfNodes.get(j).toString());
		}
		System.out.println(rdfNodes.size());
		System.out.println();

		System.out.println("Ebooks of type text with language " + lang);
		DcType dcTypeText = new DcType(DcType.TEXT);
		List<RDFNode> ebooks = Ebook.getEbooks(lang, dcTypeText);
		for (int j = 0; j < 5; j++) {
			System.out.println(new Ebook(ebooks.get(j).toString()));
		}
		System.out.println(ebooks.size());
		System.out.println();
	}

	public void printLanguageInformation() {
		System.out.println("All languages");
		for (String language : Language.getLanguages()) {
			System.out.println(language);
		}
		System.out.println();

		int numberOfResources = 0;

		Language langDe = new Language(Language.LANG_DE);
		int size = langDe.getResourcesByLanguage().size();
		System.out.println(langDe + " " + size);
		numberOfResources += size;

		Language langEn = new Language(Language.LANG_EN);
		size = langEn.getResourcesByLanguage().size();
		System.out.println(langEn + " " + size);
		numberOfResources += size;
		System.out.println();

		for (String typedLiteralString : Language.getLanguages()) {
			Language language = Language.create(typedLiteralString);
			if (!language.toString().equals("de") && !language.toString().equals("en")) {
				size = language.getResourcesByLanguage().size();
				System.out.println(language + " " + size);
				numberOfResources += size;

			}
		}
		System.out.println("Number of resources: " + numberOfResources);
		System.out.println();
	}

	public void printNodeContext(Node node) {
		node.printContext();
		node.printExactContext();
	}

	public void printSimilarEbooks(Ebook ebook) {
		System.out.println("Title: " + ebook.getTitle());
		System.out.println("Alternative: " + ebook.getAlternative());
		System.out.println();
		ebook.getCreator().printTextEbooks();
		System.out.println();
	}
}