package de.adrianwilke.gutenberg.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.adrianwilke.gutenberg.content.comparator.CorpusComparator;
import de.adrianwilke.gutenberg.content.comparator.LengthComparatot;
import de.adrianwilke.gutenberg.content.comparator.PunctuationComparator;
import de.adrianwilke.gutenberg.content.comparator.TextComparator;
import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests {@link TextComparator}.
 * 
 * @author Adrian Wilke
 */
public class TextComparatorTest {

	private static String CHARSET = "UTF-8";
	private static boolean PRINT = false;
	private static String RESOURCE_HEADINGS = "text/headings.txt";
	private static String RESOURCE_HEADINGS2 = "text/headings2.txt";

	private static Text text;
	private static Text text2;

	@BeforeClass
	public static void loadText() {
		text = new FullText(Resources.getResource(RESOURCE_HEADINGS).getPath(), CHARSET);
		text2 = new FullText(Resources.getResource(RESOURCE_HEADINGS2).getPath(), CHARSET);
	}

	@Test
	public void test() {
		TextComparator corpusComparator = new CorpusComparator();
		TextComparator punctuationComparator = new PunctuationComparator();
		TextComparator lengthComparatot = new LengthComparatot();

		if (PRINT) {
			System.out.println(corpusComparator.compare(text, text2));
			System.out.println(corpusComparator.compare(text2, text));
			System.out.println(punctuationComparator.compare(text, text2));
			System.out.println(punctuationComparator.compare(text2, text));
			System.out.println(lengthComparatot.compare(text, text2));
			System.out.println(lengthComparatot.compare(text2, text));
		}

		assertEquals(corpusComparator.compare(text, text2), corpusComparator.compare(text2, text), 0);
		assertEquals(punctuationComparator.compare(text, text2), punctuationComparator.compare(text2, text), 0);
		assertEquals(lengthComparatot.compare(text, text2), lengthComparatot.compare(text2, text), 0);

		assertTrue(corpusComparator.compare(text, text2) < 1);
		assertTrue(punctuationComparator.compare(text, text2) < 1);
		assertTrue(lengthComparatot.compare(text, text2) < 1);

		assertTrue(corpusComparator.compare(text, text) == 1);
		assertTrue(punctuationComparator.compare(text, text) == 1);
		assertTrue(lengthComparatot.compare(text, text) == 1);

		assertTrue(corpusComparator.compare(text, text, text) == 1);
		assertTrue(punctuationComparator.compare(text, text, text) < 1);
		assertTrue(lengthComparatot.compare(text, text, text) < 1);

		assertTrue(corpusComparator.compare(text, text2, text2, text) == 1);
		assertTrue(punctuationComparator.compare(text, text2, text2, text) == 1);
		assertTrue(lengthComparatot.compare(text, text2, text2, text) == 1);
	}
}