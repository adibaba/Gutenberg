package de.adrianwilke.gutenberg.bilingual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.comparators.ExactComparator;
import de.adrianwilke.gutenberg.comparators.LastPointComparator;
import de.adrianwilke.gutenberg.comparators.ShortenerComparator;
import de.adrianwilke.gutenberg.comparators.TitleComparator;
import de.adrianwilke.gutenberg.entities.Author;
import de.adrianwilke.gutenberg.entities.DcType;
import de.adrianwilke.gutenberg.entities.Ebook;
import de.adrianwilke.gutenberg.entities.Language;

public class BilingualBooks {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

		String tdbDirectory = null;
		String downloadDirectory = null;

		if (args.length == 2) {
			tdbDirectory = args[0];
			downloadDirectory = args[1];
		} else {
			System.err.println("Please set Jena TDB directory and a download directoy");
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
		System.out.println("TDB directory:      " + tdbDirectory);
		Gutenberg.getInstance().setDownloadDirectory(downloadDirectory);
		System.out.println("Download directory: " + Gutenberg.getInstance().getDownloadDirectory());


		String bilingualMatchesFile="/home/adi/Downloads/rdf-files-gutenberg/serialized/bilingual-matches.dat";
		BilingualMatch.readAll((new File(bilingualMatchesFile)));
		System.out.println(BilingualMatch.toStringAll());
		
//		new BilingualBooks().compareBilingualBoks();
	}

	private Map<String, Author> authorCache = new HashMap<String, Author>();
	private Map<String, Ebook> textBookCache = new HashMap<String, Ebook>();

	private List<Ebook> getAllTextBooksOfAuthor(String authorUri) {

		// Use cache
		Author author;
		if (authorCache.containsKey(authorUri)) {
			author = authorCache.get(authorUri);
		} else {
			author = new Author(authorUri);
			authorCache.put(authorUri, author);
		}

		List<Ebook> textBooks = new LinkedList<Ebook>();
		for (String textBookUri : author.getTextEbookUris()) {

			// Use cache
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

		// Use cache
		Ebook textBook;
		if (textBookCache.containsKey(textBookUri)) {
			textBook = textBookCache.get(textBookUri);
		} else {
			textBook = new Ebook(textBookUri);
			textBookCache.put(textBookUri, textBook);
		}

		for (String authorUri : textBook.getCreatorUris()) {

			// Use cache
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

	void compareBilingualBoks() throws FileNotFoundException, IOException {
		TitleComparator.addTitleComparator(new LastPointComparator());
		TitleComparator.addTitleComparator(new ShortenerComparator());
		TitleComparator.addTitleComparator(new ExactComparator());

		String onlyUseUri = "";
		// onlyUseUri = "http://www.gutenberg.org/ebooks/19778"; // Alice
		// onlyUseUri = "http://www.gutenberg.org/ebooks/31963"; // Bilingual

		// TODO Faust

		// Get all german text-books
		List<String> originEbookUris = Ebook.getEbookUris(new DcType(DcType.TEXT), new Language(Language.LANG_DE));

		if (!onlyUseUri.isEmpty()) {
			originEbookUris = originEbookUris.subList(0, 1);
		}

		// Check all german ebooks
		for (String originEbookUri : originEbookUris) {

			if (!onlyUseUri.isEmpty()) {
				originEbookUri = onlyUseUri;
			}

			Ebook originEbook = new Ebook(originEbookUri);

			// Get all translation candidates
			// (ebook -> all authors -> all ebooks)
			Set<Ebook> allEbooksOfAllOriginAuthors = new HashSet<Ebook>();
			for (Author author : getAuthors(originEbookUri)) {
				allEbooksOfAllOriginAuthors.addAll(getAllTextBooksOfAuthor(author.getUri()));
			}

			// Compare all titles of translation candidates to all titles of origin
			for (Ebook candidateEbook : allEbooksOfAllOriginAuthors) {

				if (originEbookUri.equals(candidateEbook.getUri())) {
					continue;
				}

				Set<String> candidateLanguages = new HashSet<String>();
				for (Language candidateLanguage : candidateEbook.getLanguages()) {
					candidateLanguages.add(candidateLanguage.getRdfLiteral().toString());

				}
				Set<String> originLanguages = new HashSet<String>();
				for (Language originLanguage : originEbook.getLanguages()) {
					originLanguages.add(originLanguage.getRdfLiteral().toString());

				}
				candidateLanguages.removeAll(originLanguages);
				if (candidateLanguages.isEmpty()) {
					continue;
				}

				// Use cache
				if (textBookCache.containsKey(candidateEbook.getUri())) {
					candidateEbook = textBookCache.get(candidateEbook.getUri());
				} else {
					textBookCache.put(candidateEbook.getUri(), candidateEbook);
				}

				
				for (String originTitle : originEbook.getAllTitles()) {
					for (String title : candidateEbook.getAllTitles()) {
						Map<String, String> matches = TitleComparator.compareAll(title, originTitle);

						if (!matches.isEmpty()) {

							BilingualMatch.add(new BilingualMatch(originEbook.getId(), candidateEbook.getId(),
									originTitle, title, matches));
						}

						// Download
						if (!matches.isEmpty()) {
							// Downloader downloader = new
							// Downloader(Gutenberg.getInstance().getDownloadDirectory());
							// DcFormat textFileFormat = new DcFormat(DcFormat.PREFIX_FILES,
							// DcFormat.TYPE_CASE_INSENSITIVE_TXT);
							//
							// for (String textFileUrl : originEbook.getFormatUrls(textFileFormat)) {
							// downloader.download(textFileUrl,
							// originEbook.getFilesystemId() + "." + textFileFormat.getSuffix());
							// // System.out.println(textFileUrl);
							// }
							//
							// for (String textFileUrl : candidateEbook.getFormatUrls(textFileFormat)) {
							// downloader.download(textFileUrl,
							// candidateEbook.getFilesystemId() + "." + textFileFormat.getSuffix());
							// // System.out.println(textFileUrl);
							// }

						}

					}
				}
				// System.out.println(candidateEbook.getFilesystemId() + "." + new
				// DcFormat(DcFormat.PREFIX_FILES,
				// DcFormat.TYPE_CASE_INSENSITIVE_TXT).getSuffix());

//				System.out.println(BilingualMatch.toStringAllMultilines());
				
			}

		}

		String bilingualMatchesFile="/home/adi/Downloads/rdf-files-gutenberg/serialized/bilingual-matches.dat";
		BilingualMatch.writeAll(new File(bilingualMatchesFile));
	}

}