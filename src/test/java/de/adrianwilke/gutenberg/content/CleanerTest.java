package de.adrianwilke.gutenberg.content;

import org.junit.BeforeClass;
import org.junit.Test;

import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests {@link Cutter}.
 * 
 * @author Adrian Wilke
 */
public class CleanerTest {

	private static boolean PRINT = false;
	private static String CHARSET = "UTF-8";
	private static String RESOURCE_WITH_ILLUSTRATIONS = "text/illustrations.txt";

	private static Text textWithIllustrations;

	@BeforeClass
	public static void loadText() {
		textWithIllustrations = new FullTxt(Resources.getResource(RESOURCE_WITH_ILLUSTRATIONS).getPath(), CHARSET);
	}

	/**
	 * Tests cutting texts.
	 */
	@Test
	public void testCutter() {

		Cleaner cleaner = new Cleaner();
		TextPart generatedText = cleaner.clean(textWithIllustrations);
		
		if (PRINT) {
			System.out.println(generatedText.getContext(15, 5));
		}
		
		assert (textWithIllustrations.getLineIndexes().size() > generatedText.getLineIndexes().size());
		assert (textWithIllustrations.getLineIndexes().size() == 3 + generatedText.getLineIndexes().size());
	}
}