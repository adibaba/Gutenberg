package de.adrianwilke.gutenberg.content.re;

import org.junit.BeforeClass;
import org.junit.Test;

import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests {@link TxtCutter}.
 * 
 * @author Adrian Wilke
 */
public class CutterTest {

	private static String CHARSET = "UTF-8";
	private static boolean PRINT = false;
	private static String RESOURCE_WITH_BOUNDARIES = "text/boundaries.txt";
	private static String RESOURCE_WITHOUT_BOUNDARIES = "text/lorem-ipsum.txt";

	private static Txt textWithBoundaries;
	private static Txt textWithoutBoundaries;

	@BeforeClass
	public static void loadText() {
		textWithBoundaries = new FullTxt(Resources.getResource(RESOURCE_WITH_BOUNDARIES).getPath(), CHARSET);
		textWithoutBoundaries = new FullTxt(Resources.getResource(RESOURCE_WITHOUT_BOUNDARIES).getPath(), CHARSET);
	}

	/**
	 * Tests cutting texts.
	 */
	@Test
	public void testCutter() {

		TxtCutter cutter = new TxtCutter();
		TxtPart generatedText = cutter.cut(textWithoutBoundaries);
		assert (!cutter.isIndexBeginFound());
		assert (!cutter.isIndexEndFound());
		assert (generatedText.getParent().equals(textWithoutBoundaries));
		assert (!generatedText.equals(textWithoutBoundaries));
		assert (!generatedText.getName().equals(textWithoutBoundaries.getName()));
		assert (generatedText.getName().endsWith("/uncut"));

		cutter = new TxtCutter();
		generatedText = cutter.cut(textWithBoundaries);
		assert (cutter.isIndexBeginFound());
		assert (cutter.isIndexEndFound());
		assert (generatedText.getLineIndexes().first() > textWithBoundaries.getLineIndexes().first());
		assert (generatedText.getLineIndexes().last() < textWithBoundaries.getLineIndexes().last());
		assert (!generatedText.getLine(generatedText.getLineIndexes().first()).isEmpty());
		assert (!generatedText.getLine(generatedText.getLineIndexes().last()).isEmpty());
		assert (generatedText.getParent().equals(textWithBoundaries));
		assert (generatedText.getName().endsWith("/cut"));

		if (PRINT) {
			System.out.println(textWithBoundaries);
			System.out.println(generatedText);
			System.out.println(generatedText.getContext(generatedText.lineIndexes.first() + 1, 3));
			System.out.println(generatedText.getContext(generatedText.lineIndexes.last() + 1, 3));
		}
	}
}