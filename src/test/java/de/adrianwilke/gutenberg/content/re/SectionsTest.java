package de.adrianwilke.gutenberg.content.re;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.List;
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
public class SectionsTest {

	private static String CHARSET = "UTF-8";
	private static Integer[] DISTANCE_SIZES = new Integer[] { 5, 4, 3 };
	private static Integer[] DISTANCES = new Integer[] { 1, 2, 4 };
	private static int LINES = 14;
	private static boolean PRINT = false;
	private static String RESOURCE = "text/lorem-ipsum.txt";
	private static SortedMap<Integer, List<Txt>> sections;
	private static Txt text;

	@BeforeClass
	public static void loadText() {
		text = new FullTxt(Resources.getResource(RESOURCE).getPath(), CHARSET);
		sections = text.getSections();
	}

	/**
	 * Test, if correct distances found.
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
	 * Test, if distances have correct number of parts.
	 */
	@Test
	public void test2DistanceSizes() {
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
	 * Test, if all sections contain correct number of empty lines.
	 */
	@Test
	public void test3EmptyLines() {

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
			List<Txt> parts = sections.get(distance);
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

	/**
	 * Test sections of section.
	 */
	@Test
	public void test4SubSections() {

		// A distance of 4 should exist
		assertTrue(sections.containsKey(4));

		// There should be 3 parts divided by a distance of 4
		assertTrue(sections.get(4).size() == 3);

		// Last-part of distance of 4
		List<Txt> section = sections.get(4);
		Txt lastPart = section.get(section.size() - 1);

		if (PRINT) {
			System.out.println();
			System.out.println("Section 4, last part:");
			System.out.println(lastPart);
			System.out.println(lastPart.getLinesString(true));
		}

		// Get sub-sections of last-part
		SortedMap<Integer, List<Txt>> subSections = lastPart.getSections();

		// Last-part has only one break
		assertTrue(subSections.size() == 1);

		// Last-part break consists of two lines
		assertTrue(subSections.containsKey(2));

		// Last-part consists of two parts
		assertTrue(subSections.get(2).size() == 2);

		if (PRINT) {
			System.out.println("Section 4, last part, sub-sections:");
			for (Txt subSectionText : subSections.get(2)) {
				System.out.println(subSectionText);
				System.out.println(subSectionText.getLinesString(true));
			}
		}

	}
}