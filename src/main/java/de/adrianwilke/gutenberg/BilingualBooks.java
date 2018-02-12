package de.adrianwilke.gutenberg;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.entities.Author;
import de.adrianwilke.gutenberg.entities.DcType;
import de.adrianwilke.gutenberg.entities.Ebook;
import de.adrianwilke.gutenberg.entities.Language;

public class BilingualBooks {

	public static void main(String[] args) {

		String tdbDirectory = null;
		if (args.length == 1) {
			tdbDirectory = args[0];
		} else {
			System.err.println("Please set Jena TDB directory");
			System.exit(1);
		}

		File directoryTdb = new File(tdbDirectory);
		if (!directoryTdb.isDirectory()
				|| !directoryTdb.canRead()
				|| !(Arrays.asList(directoryTdb.list())).contains("GOSP.dat")) {
			System.err.println("Error: Can not read Jena TDB directory: " + tdbDirectory);
			System.exit(1);
		}

		Gutenberg.getInstance(tdbDirectory);
		System.out.println("TDB directory: " + tdbDirectory);

		// TODO
		// new BilingualBooks().tmp();
		new BilingualBooks().compareBilingualBoks();
	}

	private Map<String, Author> authorCache = new HashMap<String, Author>();
	private Map<String, Ebook> textBookCache = new HashMap<String, Ebook>();

	// TODO Get textbooks in special language, e.g. en
	private List<Ebook> getTextBooks(String authorUri) {
		List<Ebook> textBooks = new LinkedList<Ebook>();

		Author author;
		if (authorCache.containsKey(authorUri)) {
			author = authorCache.get(authorUri);
		} else {
			author = new Author(authorUri);
			authorCache.put(authorUri, author);
		}

		for (String textBookUri : author.getTextEbookUris()) {

			Ebook textBook;
			if (textBookCache.containsKey(textBookUri)) {
				textBook = textBookCache.get(textBookUri);
			} else {
				textBook = new Ebook(textBookUri);
				textBookCache.put(textBookUri, textBook);
			}

			textBooks.add(textBook);
		}

		return textBooks;
	}

	private List<Author> getAuthors(String textBookUri) {
		List<Author> authors = new LinkedList<Author>();

		Ebook textBook;
		if (textBookCache.containsKey(textBookUri)) {
			textBook = textBookCache.get(textBookUri);
		} else {
			textBook = new Ebook(textBookUri);
			textBookCache.put(textBookUri, textBook);
		}

		for (String authorUri : textBook.getCreatorUris()) {

			Author author;
			if (authorCache.containsKey(authorUri)) {
				author = authorCache.get(authorUri);
			} else {
				author = new Author(authorUri);
				authorCache.put(authorUri, author);
			}

			authors.add(author);
		}

		return authors;
	}

	// TODO
	void compareBilingualBoks() {

		// Get all german text-books
		List<RDFNode> germanEbooks = Ebook.getEbooks(new Language(Language.LANG_DE), new DcType(DcType.TEXT));
		System.out.println(germanEbooks.size());
		germanEbooks = germanEbooks.subList(0, 10);

		// TODO: Subset to test
		germanEbooks = germanEbooks.subList(0, 1);

		for (RDFNode germanEbook : germanEbooks) {

			// Get all translation candidates
			List<Ebook> authorTextbooks = new LinkedList<Ebook>();
			List<Author> authors = getAuthors(germanEbook.toString());
			for (Author author : authors) {
				authorTextbooks.addAll(getTextBooks(author.getUri()));
			}

			// TODO: Check, if names are similar
			// Compare $germanEbook-allTitles with $authorTextbooks-allTitles
			System.out.println(authorTextbooks);
		}

	}

	// TODO
	void tmp() {
		// Ebook book = new Ebook("http://www.gutenberg.org/ebooks/19778"); // ALICE
		Ebook book = new Ebook("http://www.gutenberg.org/ebooks/19551"); // Multiple creators

		// book.printContext();

		// List<RDFNode> nodes = new SelectBldr().setDistinct(true)
		// .addWhere(book.getEnclosedUri(), Uris.enclose(Uris.DCTERMS_LANGUAGE), "?l")
		// .addWhere("?l", Uris.enclose(Uris.RDF_VALUE),
		// "?typedlanguage").addVar("typedlanguage")
		// .execute("typedlanguage");
		// for (RDFNode rdfNode : nodes) {
		// System.out.println(rdfNode);
		// }

		System.out.println(book.getCreators());

		System.out.println(book.getAllTitles());

		System.out.println(book.getCreators().get(0).getTextEbookUris());
	}
}