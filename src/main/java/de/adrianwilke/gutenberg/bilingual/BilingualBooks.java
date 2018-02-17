package de.adrianwilke.gutenberg.bilingual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.comparators.TitleComparator;
import de.adrianwilke.gutenberg.entities.Author;
import de.adrianwilke.gutenberg.entities.Cache;
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

		// String bilingualMatchesDirectory =
		// "/home/adi/Downloads/rdf-files-gutenberg/serialized";
		// String bilingualMatchesFile = "bilingual-matches.dat";
		// BilingualMatch.readAll(bilingualMatchesDirectory, bilingualMatchesFile);
		// System.out.println(BilingualMatch.toStringAll());
		// System.out.println(BilingualMatch.getAll().size());
		// System.out.println();

		long time = System.currentTimeMillis();
		new BilingualBooks().getBilingual(Ebook.getEbookUris(new DcType(DcType.TEXT), new Language(Language.LANG_DE)));
		System.out.println((System.currentTimeMillis() - time) / 1000f);
	}

	@SuppressWarnings("unchecked")
	private Cache<Author> authorCache = (Cache<Author>) Cache.getCache(Author.class);

	@SuppressWarnings("unchecked")
	private Cache<Ebook> ebookCache = (Cache<Ebook>) Cache.getCache(Ebook.class);

	void getBilingual(List<String> ebookUris) throws FileNotFoundException, IOException {

		// Check all input ebooks
		for (String originEbookUri : ebookUris) {
			Ebook originEbook = new Ebook(originEbookUri);

			// Get all translation candidates
			// (origin ebook -> all authors -> all ebooks)
			// Authos and ebooks are added to caches.
			Set<Ebook> allEbookCandidates = new HashSet<Ebook>();
			for (Author author : getAuthors(originEbookUri)) {
				allEbookCandidates.addAll(getAllTextBooksOfAuthor(author.getUri()));
			}

			// Compare all titles of translation candidates to all titles of origin
			for (Ebook candidateEbook : allEbookCandidates) {

				// Do not return origin ebook
				if (originEbookUri.equals(candidateEbook.getUri())) {
					continue;
				}

				// Check languages: There must be at least one language in candidate, which is
				// not in origin
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

				// Compare all titles of origin with all titles of candidates
				for (String originTitle : originEbook.getAllTitles()) {
					for (String title : candidateEbook.getAllTitles()) {
						
						Map<String, String> matches = TitleComparator.compareAll(title, originTitle);

						if (!matches.isEmpty()) {

							BilingualMatch.add(new BilingualMatch(originEbook.getId(), candidateEbook.getId(),
									originTitle, title, matches));
						}

						// Download
//						if (!matches.isEmpty()) {
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

//						}

					}
				}
				// System.out.println(candidateEbook.getFilesystemId() + "." + new
				// DcFormat(DcFormat.PREFIX_FILES,
				// DcFormat.TYPE_CASE_INSENSITIVE_TXT).getSuffix());
			}

		}

		// String bilingualMatchesDirectory =
		// "/home/adi/Downloads/rdf-files-gutenberg/serialized";
		// String bilingualMatchesFile = "bilingual-matches.dat";
		// BilingualMatch.writeAll(bilingualMatchesDirectory, bilingualMatchesFile);

		System.out.println(BilingualMatch.toStringAll());
		System.out.println(BilingualMatch.getAll().size());
	}

	/**
	 * For each given author, all textbooks are returned.
	 * 
	 * Caches for authors and e-books are utilized, if data was already requested.
	 */
	private List<Ebook> getAllTextBooksOfAuthor(String authorUri) {
		List<Ebook> textBooks = new LinkedList<Ebook>();
		for (String textBookUri : authorCache.get(authorUri).getTextEbookUris()) {
			textBooks.add(ebookCache.get(textBookUri));
		}
		return textBooks;
	}

	/**
	 * For each given textbook, all authors are returned.
	 * 
	 * Caches for authors and e-books are utilized, if data was already requested.
	 */
	private List<Author> getAuthors(String textBookUri) {
		List<Author> authors = new LinkedList<Author>();
		for (String authorUri : ebookCache.get(textBookUri).getCreatorUris()) {
			authors.add(authorCache.get(authorUri));
		}
		return authors;
	}

}