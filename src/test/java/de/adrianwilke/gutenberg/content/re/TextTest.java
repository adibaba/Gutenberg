package de.adrianwilke.gutenberg.content.re;

import org.junit.BeforeClass;
import org.junit.Test;

import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests of texts.
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
	 * Test the print of contexts.
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
	 * Test the print texts
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
