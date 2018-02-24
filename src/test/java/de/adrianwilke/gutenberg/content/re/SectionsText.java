package de.adrianwilke.gutenberg.content.re;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests the generation of sections.
 * 
 * @author Adrian Wilke
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SectionsText {

	private static String CHARSET = "UTF-8";
	private static Integer[] DISTANCE_SIZES = new Integer[] { 5, 4, 3 };
	private static Integer[] DISTANCES = new Integer[] { 1, 2, 4 };
	private static int LINES = 14;
	private static boolean PRINT = false;
	private static String RESOURCE = "text/lorem-ipsum.txt";
	private static SortedMap<Integer, SortedSet<Txt>> sections;
	private static Txt text;

	@BeforeClass
	public static void loadText() {
		text = new FullTxt(Resources.getResource(RESOURCE).getPath(), CHARSET);
		sections = text.getParts();
	}

	/**
	 * Test, if correct distances found
	 */
	@Test
	public void test1Distances() {
		Integer[] actualDistances = sections.keySet().toArray(new Integer[0]);

		if (PRINT) {
			System.out.println("Distances expected: " + Arrays.toString(DISTANCES));
			System.out.println("Distances actual:   " + Arrays.toString(actualDistances));
		}

		assertArrayEquals(DISTANCES, actualDistances);
	}

	/**
	 * Test, if distances have correct number of parts
	 */
	@Test
	public void testDistanceSizes() {
		for (int i = 0; i < DISTANCES.length; i++) {
			int distance = DISTANCES[i];
			int expectedSize = DISTANCE_SIZES[i];
			int actualSize = sections.get(distance).size();

			if (PRINT) {
				System.out.println("Size expected (" + distance + "): " + expectedSize);
				System.out.println("Size actual   (" + distance + "): " + actualSize);
			}

			assertEquals(expectedSize, actualSize);
		}
	}

	/**
	 * Test, if all sections contain correct number of empty lines
	 */
	@Test
	public void testEmptyLines() {

		// Get indexes of non-empty lines of original text
		SortedSet<Integer> nonEmptyOriginalLines = new TreeSet<Integer>();
		for (Integer index : text.getLineIndexes()) {
			if (!text.getLine(index).trim().isEmpty()) {
				nonEmptyOriginalLines.add(index);
			}
		}
		Integer[] expectedNonEmptyLines = nonEmptyOriginalLines.toArray(new Integer[0]);

		if (PRINT) {
			System.out.println("Lines expected:   " + Arrays.toString(expectedNonEmptyLines));
		}

		assumeTrue(expectedNonEmptyLines.length == LINES);

		// Test
		SortedSet<Integer> nonEmptyLines = new TreeSet<Integer>();
		for (Integer distance : DISTANCES) {
			nonEmptyLines.clear();
			SortedSet<Txt> parts = sections.get(distance);
			for (Txt part : parts) {
				for (Integer index : part.getLineIndexes()) {
					if (!part.getLine(index).trim().isEmpty()) {
						nonEmptyLines.add(index);
					}
				}
			}
			Integer[] actualNonEmptyLines = nonEmptyLines.toArray(new Integer[0]);

			if (PRINT) {
				System.out.println("Lines actual (" + distance + "): " + Arrays.toString(actualNonEmptyLines));
			}

			assertArrayEquals(expectedNonEmptyLines, actualNonEmptyLines);
		}
	}
}