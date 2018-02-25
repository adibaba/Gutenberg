package de.adrianwilke.gutenberg.content.re;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests {@link TxtChapterSearch}.
 * 
 * @author Adrian Wilke
 */
public class ChapterSearchTest {

	private static String CHARSET = "UTF-8";
	private static boolean PRINT = false;
	private static String RESOURCE_WITH_HEADINGS = "text/headings.txt";
	private static String RESOURCE_WITHOUT_HEADINGS = "text/lorem-ipsum.txt";
	private static Txt textWithHeadings;
	private static Txt textWithoutHeadings;

	@BeforeClass
	public static void loadText() {
		textWithHeadings = new FullTxt(Resources.getResource(RESOURCE_WITH_HEADINGS).getPath(), CHARSET);
		textWithoutHeadings = new FullTxt(Resources.getResource(RESOURCE_WITHOUT_HEADINGS).getPath(), CHARSET);
	}

	/**
	 * Searches for chapters
	 */
	@Test
	public void testChapterSearch() {
		TxtChapterSearch chapterSearch;
		boolean found;

		// No headings in text

		chapterSearch = new TxtChapterSearch();
		found = chapterSearch.search(textWithoutHeadings, true);
		if (PRINT) {
			System.out.println("Distances in haystack: " + textWithoutHeadings.getSections().keySet());
			System.out.println("Used distances:        " + chapterSearch.getUsedDistances());
			System.out.println("Find distance:         " + chapterSearch.getDistanceOfFind());
			System.out.println("Find index:            " + chapterSearch.getTextIndexOfFind());
			System.out.println();
		}
		assert (!found);
		assert (!chapterSearch.getUsedDistances().contains(1));

		// Headings in text

		chapterSearch = new TxtChapterSearch();
		found = chapterSearch.search(textWithHeadings, true);
		if (PRINT) {
			System.out.println("Distances in haystack: " + textWithHeadings.getSections().keySet());
			System.out.println("Used distances:        " + chapterSearch.getUsedDistances());
			System.out.println("Find distance:         " + chapterSearch.getDistanceOfFind());
			System.out.println("Find index:            " + chapterSearch.getTextIndexOfFind());
			System.out.println("Context:");
			List<Txt> sectionOfFind = textWithHeadings.getSections().get(chapterSearch.getDistanceOfFind());
			Txt textOfFind = sectionOfFind.get(chapterSearch.getTextIndexOfFind());
			Integer firstLineIndex = textOfFind.getLineIndexes().first();
			System.out.println(textWithHeadings.getContext(firstLineIndex + 1, 2));
			System.out.println();
		}
		assert (found);
	}
}