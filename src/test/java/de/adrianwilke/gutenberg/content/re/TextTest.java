package de.adrianwilke.gutenberg.content.re;

import org.junit.BeforeClass;
import org.junit.Test;

import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests {@link Txt}, {@link FullTxt}, {@link TxtPart}.
 * 
 * @author Adrian Wilke
 */
public class TextTest {

	private static String CHARSET = "UTF-8";
	private static boolean PRINT = false;
	private static String RESOURCE = "text/lorem-ipsum.txt";
	private static Txt text;

	@BeforeClass
	public static void loadText() {
		text = new FullTxt(Resources.getResource(RESOURCE).getPath(), CHARSET);
	}

	/**
	 * Tests {@link Txt#getLineSimplified(int)}
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