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
import java.util.TreeSet;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.comparators.TitleComparator;
import de.adrianwilke.gutenberg.data.Downloader;
import de.adrianwilke.gutenberg.entities.Author;
import de.adrianwilke.gutenberg.entities.Cache;
import de.adrianwilke.gutenberg.entities.DcFormat;
import de.adrianwilke.gutenberg.entities.DcType;
import de.adrianwilke.gutenberg.entities.Ebook;
import de.adrianwilke.gutenberg.entities.Language;
import de.adrianwilke.gutenberg.utils.Comparators;

/**
 * Searches for bilingual books.
 * 
 * @author Adrian Wilke
 */
public class BilingualBooks {

	public static boolean EXECUTE = true;
	public static final String FILE_MATCHES_DE = "bilingual-matches-de.dat";

	public static void main(String[] args) throws FileNotFoundException, IOException {

		mainConfigure(args);
		BilingualBooks bb = new BilingualBooks();

		List<String> germanTextBooksUris = Ebook.getEbookUris(new DcType(DcType.TEXT), new Language(Language.LANG_DE));
		Language languageEn = new Language(Language.LANG_EN);
		DcFormat formatTxt = new DcFormat(DcFormat.PREFIX_FILES, DcFormat.TYPE_CASE_INSENSITIVE_TXT);
		DcFormat formatTxtUtf8 = new DcFormat(DcFormat.PREFIX_FILES, DcFormat.TYPE_CASE_INSENSITIVE_TXT_UTF_8);
		DcFormat formatAllFiles = new DcFormat(DcFormat.PREFIX_FILES, null);
		DcFormat formatHtmlZip = new DcFormat(DcFormat.PREFIX_FILES, DcFormat.TYPE_H_ZIP);

		// Generate matches from TDB
		if (EXECUTE == false) {
			long time = System.currentTimeMillis();
			bb.getBilingual(germanTextBooksUris, languageEn);
			System.out.println((System.currentTimeMillis() - time) / 1000f);
		}

		// Serialize matches and write them to file
		if (EXECUTE == false) {
			BilingualMatch.writeAllSerialized(FILE_MATCHES_DE);
		}

		// Read serialized matches from file
		if (EXECUTE == false) {
			BilingualMatch.readAllSerialized(FILE_MATCHES_DE);
		}

		// Print matches
		if (EXECUTE == false) {
			bb.printBilingualMatches();
		}

		// Download matches
		if (EXECUTE == false) {
			bb.downloadMultimatch(formatTxt, "bilingual-de-en");
			bb.downloadMultimatch(formatTxtUtf8, "bilingual-de-en");
			bb.downloadMultimatch(formatTxtUtf8, "bilingual-de-en");
			bb.downloadMultimatch(formatHtmlZip, "bilingual-de-en");
		}

		// Print and download native bilingual books
		if (EXECUTE == false) {
			Set<Ebook> nativeBilingualBooks = bb.getNativeBilingualBooks(germanTextBooksUris, languageEn);
			for (Ebook ebook : nativeBilingualBooks) {
				System.out.println(ebook);
				bb.downloadEbook(ebook, formatAllFiles, "native-bilingual-de-en");
			}
		}

	}

	private static void mainConfigure(String[] args) {

		String tdbDirectory = null;
		String downloadDirectory = null;
		String serializationDirectory = null;

		if (args.length == 3) {
			tdbDirectory = args[0];
			downloadDirectory = args[1];
			serializationDirectory = args[2];
		} else {
			System.err.println("Please set Jena TDB directory, download directoy, and serialization directory.");
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
		System.out.println("TDB directory:           " + tdbDirectory);
		Gutenberg.getInstance().setDownloadDirectory(downloadDirectory);
		System.out.println("Download directory:      " + Gutenberg.getInstance().getDownloadDirectory());
		Gutenberg.getInstance().setSerializationDirectory(serializationDirectory);
		System.out.println("Serialization directory: " + Gutenberg.getInstance().getSerializationDirectory());
	}

	@SuppressWarnings("unchecked")
	private Cache<Author> authorCache = (Cache<Author>) Cache.getCache(Author.class);

	private Downloader downloader = new Downloader(Gutenberg.getInstance().getDownloadDirectory());

	@SuppressWarnings("unchecked")
	private Cache<Ebook> ebookCache = (Cache<Ebook>) Cache.getCache(Ebook.class);

	private void downloadMultimatch(DcFormat downloadFormat, String downloadPathPrefix) {
		if (downloadFormat.getPrefix() != null) {

			// Get IDs of all e-books in matches
			Set<Integer> ids = new HashSet<Integer>();
			for (BilingualMatch bilingualMatch : BilingualMatch.getAll()) {
				ids.add(bilingualMatch.getOriginId());
				ids.add(bilingualMatch.getCandidateId());
			}
			System.out.println("IDs to check for download: " + ids.size());

			for (int id : ids) {
				downloadEbook(new Ebook(id), downloadFormat, downloadPathPrefix);
			}
		} else {
			System.err.println("Please specify prefix for download.");
		}
	}

	private void downloadEbook(Ebook ebook, DcFormat downloadFormat, String downloadPathPrefix) {
		if (downloadFormat.getPrefix() != null) {

			// Create download path
			for (String downloadUrl : ebook.getFormatUrls(downloadFormat)) {
				if (downloadUrl.contains("/")) {
					String downloadPath = downloadPathPrefix + "/";
					downloadPath += ebook.getPrefixForFileSystemStorage() + "/";
					downloadPath += downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);

					// Download
					downloader.download(downloadUrl, downloadPath);

				} else {
					System.err.println("URL without slash: " + downloadUrl);
					continue;
				}
			}

		} else {
			System.err.println("Please specify prefix for download.");
		}

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

	/**
	 * Results are added to {@link BilingualMatch}
	 */
	public void getBilingual(List<String> ebookUris, Language secondLanguage)
			throws FileNotFoundException, IOException {

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

				// Check second language
				if (!candidateEbook.getLanguages().contains(secondLanguage)) {
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

							// Add match
							BilingualMatch.add(new BilingualMatch(originEbook.getId(), candidateEbook.getId(),
									originTitle, title, matches));
						}
					}
				}
			}
		}
	}

	public Set<Ebook> getNativeBilingualBooks(List<String> ebookUris, Language secondLanguage) {
		Set<Ebook> nativeBilingualBooks = new TreeSet<>(new Comparators<Ebook>().getToStringDefault());
		for (String ebookUri : ebookUris) {
			Ebook ebook = new Ebook(ebookUri);
			if (ebook.getLanguages().contains(secondLanguage)) {
				nativeBilingualBooks.add(ebook);
			}
		}
		return nativeBilingualBooks;
	}

	private void printBilingualMatches() {
		System.out.println(BilingualMatch.toStringAll());
		System.out.println(BilingualMatch.getAll().size());
		System.out.println();
	}
}