package de.adrianwilke.gutenberg;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

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
 * Set directory for extracted RDF/XML files {@link Examples#DIRECTORY_RDF} and
 * directory to store Jena TDB {@link Examples#DIRECTORY_TDB}. You can also use
 * command line arguments, see {@link Examples#mainConfigure(String[])}.
 * 
 * To execute example code, set EXEC-constants to 1 (e.g. to import RDF/XML
 * files use {@link Examples#EXEC_IMPORT_RDF_FILES}).
 * 
 * @author Adrian Wilke
 */
public class Examples {

	public static final int ALICE_FILE_ID = 19778;
	public static final String ALICE_URI = "http://www.gutenberg.org/ebooks/" + ALICE_FILE_ID;

	public static String DIRECTORY_RDF = "/tmp/gutenberg/epub";
	public static String DIRECTORY_TDB = "/tmp/gutenberg/tdb";

	public static int EXEC_IMPORT_RDF_FILES = 0;
	public static int EXEC_IMPORTER_PRINT_RDF_FILE = 0;
	public static int EXEC_PRINT_AUTHOR_INFORMATION = 1;
	public static int EXEC_PRINT_DC_TYPE_INFORMATION = 0;
	public static int EXEC_PRINT_EBOOK_INFORMATION = 0;
	public static int EXEC_PRINT_ITEMS_IN_LANGUAGE = 0;
	public static int EXEC_PRINT_LANGUAGE_INFORMATION = 0;
	public static int EXEC_PRINT_NODE_CONTEXT = 0;
	public static int EXEC_PRINT_SIMILAR_EBOOKS = 0;

	public static void main(String[] args) throws Exception {

		// Configure
		mainConfigure(args);

		// Initialize TDB
		Gutenberg.getInstance(DIRECTORY_TDB);

		// Execute
		if (0 != EXEC_IMPORT_RDF_FILES) {
			String[] arguments = new String[2];
			arguments[0] = DIRECTORY_RDF;
			arguments[1] = DIRECTORY_TDB;

			if ("To import".equals(null)) {
				Importer.main(args);
			} else {
				throw new Exception("Be sure to import.");
			}
		}
		if (0 != EXEC_IMPORTER_PRINT_RDF_FILE) {
			new Examples().printImporterRdfFile(DIRECTORY_RDF, ALICE_FILE_ID);
		}
		if (0 != EXEC_PRINT_AUTHOR_INFORMATION) {
			new Examples().printAuthorInformation(new Ebook(ALICE_URI));
		}
		if (0 != EXEC_PRINT_DC_TYPE_INFORMATION) {
			new Examples().printDcTypeInformation();
		}
		if (0 != EXEC_PRINT_EBOOK_INFORMATION) {
			new Examples().printEbookInformation();
		}
		if (0 != EXEC_PRINT_ITEMS_IN_LANGUAGE) {
			new Examples().printItemsInLanguage(new Language(Language.LANG_DE));
		}
		if (0 != EXEC_PRINT_LANGUAGE_INFORMATION) {
			new Examples().printLanguageInformation();
		}
		if (0 != EXEC_PRINT_NODE_CONTEXT) {
			new Examples().printNodeContext(new Node(ALICE_URI));
		}
		if (0 != EXEC_PRINT_SIMILAR_EBOOKS) {
			new Examples().printSimilarEbooks(new Ebook(ALICE_URI));
		}
	}

	public static void mainConfigure(String[] args) throws Exception {

		// Set directories
		if (args.length == 2) {
			DIRECTORY_RDF = args[0];
			DIRECTORY_TDB = args[1];
		} else if (args.length == 1) {
			DIRECTORY_RDF = args[0] + File.separator + "epub";
			DIRECTORY_TDB = args[0] + File.separator + "tdb";
		}

		// Print info
		System.out.println("DIRECTORY_RDF: " + DIRECTORY_RDF);
		System.out.println("DIRECTORY_TDB: " + DIRECTORY_TDB);

		// TODO: Check TDB directory for non-import methods
		if (0 == EXEC_IMPORT_RDF_FILES && 0 == EXEC_IMPORTER_PRINT_RDF_FILE) {
			File directoryTdb = new File(DIRECTORY_TDB);
			if (!directoryTdb.isDirectory()
					|| !directoryTdb.canRead()
					|| !(Arrays.asList(directoryTdb.list())).contains("GOSP.dat")) {
				System.err.println("Error: Can not read Jena TDB directory: " + DIRECTORY_RDF);
				System.exit(1);
			}
		}
	}

	public void printAuthorInformation(Ebook ebook) {
		Author firstAuthor = ebook.getCreators().get(0);
		System.out.println("Authors: " + ebook.getCreators());
		System.out.println("Author 1, to string: " + firstAuthor);
		System.out.println("Author 1, Name: " + firstAuthor.getName());
		System.out.println("Author 1, URI: " + firstAuthor.getUri());
		System.out.println("Author 1, text books: ");
		for (Ebook textBook : firstAuthor.getTextEbooks()) {
			System.out.println(textBook);
		}
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
		for (RDFNode node : new DcType(DcType.MOVING_IMAGE).getResourcesByDcType()) {
			System.out.println(node);
		}
		System.out.println();
	}

	public void printEbookInformation() {
		System.out.println();

		// Sizes of collections
		Language language = new Language(Language.LANG_DE);
		DcType dcTypeText = new DcType(DcType.TEXT);
		DcType dcTypeSound = new DcType(DcType.SOUND);

		System.out.println("Total number of Ebooks: " + Ebook.getEbookRdfNodes().size());
		System.out.println("Number of Ebooks in language " + language + ": " + Ebook.getEbookRdfNodes(language).size());

		System.out.println("Number of Ebooks in language "
				+ language
				+ " and type "
				+ dcTypeText
				+ ": "
				+ Ebook.getEbookRdfNodes(language, dcTypeText).size());

		System.out.println("Number of Ebooks in language "
				+ language
				+ " and type "
				+ dcTypeSound
				+ ": "
				+ Ebook.getEbookRdfNodes(language, dcTypeSound).size());
		System.out.println();

		System.out.println("Ebooks in language " + language + " and type " + dcTypeSound + ": ");
		for (RDFNode rdfNode : Ebook.getEbookRdfNodes(language, dcTypeSound)) {
			System.out.println(rdfNode);
		}

		System.out.println();
	}

	public void printImporterRdfFile(String gutenbergDirectory, int fileId) {
		Importer importer = new Importer();
		String filePath = importer.getRdfFile(DIRECTORY_RDF, Integer.toString(ALICE_FILE_ID));
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
		List<RDFNode> ebooks = Ebook.getEbookRdfNodes(lang, dcTypeText);
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

		Language lang = new Language(Language.LANG_DE);
		int size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		lang = new Language(Language.LANG_EN);
		size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		lang = new Language(Language.LANG_ES);
		size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		lang = new Language(Language.LANG_FI);
		size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		lang = new Language(Language.LANG_FR);
		size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		lang = new Language(Language.LANG_IT);
		size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		lang = new Language(Language.LANG_NL);
		size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		lang = new Language(Language.LANG_PT);
		size = lang.getResourcesByLanguage().size();
		System.out.println(lang + " " + size);
		numberOfResources += size;

		System.out.println();
		for (String typedLiteralString : Language.getLanguages()) {
			Language language = Language.create(typedLiteralString);
			if (!language.toString().equals("de")
					&& !language.toString().equals("en")
					&& !language.toString().equals("es")
					&& !language.toString().equals("fi")
					&& !language.toString().equals("fr")
					&& !language.toString().equals("it")
					&& !language.toString().equals("nl")
					&& !language.toString().equals("pt")) {
				size = language.getResourcesByLanguage().size();
				System.out.println(language + " " + size);
				numberOfResources += size;

			}
		}
		System.out.println("Number of resources: " + numberOfResources);
		System.out.println();
	}

	public void printNodeContext(Node node) {
		node.printContextExample();
		node.printContext();
	}

	public void printSimilarEbooks(Ebook ebook) {
		// TODO: Multiple creators
		System.out.println("Title: " + ebook.getTitles());
		System.out.println("Alternative: " + ebook.getAlternatives());
		System.out.println();
		// TODO: Multiple creators
		ebook.getCreators().get(0).printTextEbooks();
		System.out.println();
	}
}