package de.adrianwilke.gutenberg.content;

import org.junit.BeforeClass;
import org.junit.Test;

import de.adrianwilke.gutenberg.content.FullText;
import de.adrianwilke.gutenberg.content.Text;
import de.adrianwilke.gutenberg.content.TextPart;
import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests {@link Text}, {@link FullText}, {@link TextPart}.
 * 
 * @author Adrian Wilke
 */
public class TextTest {

	private static String CHARSET = "UTF-8";
	private static boolean PRINT = false;
	private static String RESOURCE = "text/lorem-ipsum.txt";
	private static Text text;

	@BeforeClass
	public static void loadText() {
		text = new FullText(Resources.getResource(RESOURCE).getPath(), CHARSET);
	}

	/**
	 * Tests {@link Text#getLineSimplified(int)}
	 */
	@Test
	public void testCleaning() {
		if (PRINT) {
			for (Integer index : text.getLineIndexes()) {
				System.out.println(text.getLine(index));
				System.out.println(text.getLineSimplified(index));
			}
		}
	}

	/**
	 * Tests {@link Text#getCorpus()}
	 */
	@Test
	public void testCorpus() {
		if (PRINT) {
			System.out.println(text.getCorpus());
		}
		assert (text.getCorpus().size() == 63);
	}

	/**
	 * Tests the print of contexts.
	 */
	@Test
	public void testGetContext() {
		if (PRINT) {
			int lineNumer = 3;
			int range = 5;
			System.out.println("Line number: " + lineNumer + ", range: " + range);
			System.out.println(text.getContext(lineNumer, range));

			lineNumer = 25;
			System.out.println("Line number: " + lineNumer + ", range: " + range);
			System.out.println(text.getContext(lineNumer, range));

			lineNumer = 21;
			range = 0;
			System.out.println("Line number: " + lineNumer + ", range: " + range);
			System.out.println(text.getContext(lineNumer, range));

			range = 1;
			System.out.println("Line number: " + lineNumer + ", range: " + range);
			System.out.println(text.getContext(lineNumer, range));

			System.out.println();
		}

	}

	/**
	 * Tests {@link Text#getNumberOfPunctuationMarks()}
	 */
	@Test
	public void testPunctuationMarks() {
		if (PRINT) {
			System.out.println(text.getNumberOfPunctuationMarks());
		}
		assert (text.getNumberOfPunctuationMarks() == 8);
	}

	/**
	 * Tests to remove a line.
	 */
	@Test
	public void testRemove() {
		int numberOfIndexes = text.getLineIndexes().size();
		assert (text.remove(10));
		assert (numberOfIndexes == text.getLineIndexes().size() + 1);

		if (PRINT) {
			// Source should be existent
			System.out.println(text.getContext(10, 1));
		}

		// Reload text for other tests
		loadText();
		assert (numberOfIndexes == text.getLineIndexes().size());
	}

	/**
	 * Tests the print texts
	 */
	@Test
	public void testToString() {
		if (PRINT) {
			System.out.println(text);
			System.out.println();
			System.out.println("Getting indexes.");
			text.getLineIndexes();

			System.out.println(text);
			System.out.println();

			System.out.println("Getting sections.");
			text.getSections();
			System.out.println(text);
			System.out.println(text.sections.get(1).get(1));

			System.out.println();
		}
	}
}