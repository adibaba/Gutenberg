package de.adrianwilke.gutenberg.content;

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

import de.adrianwilke.gutenberg.content.FullTxt;
import de.adrianwilke.gutenberg.content.Text;
import de.adrianwilke.gutenberg.io.Resources;

/**
 * Tests the generation of sections {@link Text#getSections()}.
 * 
 * @author Adrian Wilke
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SectionsTest {

	private static String CHARSET = "UTF-8";
	private static Integer[] DISTANCE_SIZES_HEADINGS_AND_ILLUSTRATION = new Integer[] { 9, 5, 4 };
	private static Integer[] DISTANCE_SIZES_HEADINGS_EXCLUDED = new Integer[] { 5, 4, 3 };
	private static Integer[] DISTANCE_SIZES_HEADINGS_INCLUDED = new Integer[] { 9, 5, 4 };
	private static Integer[] DISTANCES_HEADINGS_AND_ILLUSTRATION = new Integer[] { 1, 2, 3 };
	private static Integer[] DISTANCES_HEADINGS_EXCLUDED = new Integer[] { 1, 2, 4 };
	private static Integer[] DISTANCES_HEADINGS_INCLUDED = new Integer[] { 1, 2, 3 };
	private static int LINES = 14;
	private static boolean PRINT = false;
	private static String RESOURCE_HEADINGS_AND_ILLUSTRATION = "text/illustrations2.txt";
	private static String RESOURCE_HEADINGS_EXCLUDED = "text/lorem-ipsum.txt";
	private static String RESOURCE_HEADINGS_INCLUDED = "text/headings.txt";
	private static SortedMap<Integer, List<Text>> sectionsHeadingsAndIllustration;
	private static SortedMap<Integer, List<Text>> sectionsHeadingsExcluded;
	private static SortedMap<Integer, List<Text>> sectionsHeadingsIncluded;
	private static Text textHeadingsAndIllustration;
	private static Text textHeadingsExcluded;
	private static Text textHeadingsIncluded;

	@BeforeClass
	public static void loadText() {
		textHeadingsIncluded = new FullTxt(Resources.getResource(RESOURCE_HEADINGS_INCLUDED).getPath(), CHARSET);
		textHeadingsExcluded = new FullTxt(Resources.getResource(RESOURCE_HEADINGS_EXCLUDED).getPath(), CHARSET);
		sectionsHeadingsIncluded = textHeadingsIncluded.getSections();
		sectionsHeadingsExcluded = textHeadingsExcluded.getSections();

		// Additional case with illustration behind heading
		textHeadingsAndIllustration = new FullTxt(Resources.getResource(RESOURCE_HEADINGS_AND_ILLUSTRATION).getPath(),
				CHARSET);
		Cleaner cleaner = new Cleaner();
		textHeadingsAndIllustration = cleaner.clean(textHeadingsAndIllustration);
		sectionsHeadingsAndIllustration = textHeadingsAndIllustration.getSections();
	}

	/**
	 * Tests, if correct distances found.
	 */
	@Test
	public void test1Distances() {
		Integer[] actualDistances = sectionsHeadingsExcluded.keySet().toArray(new Integer[0]);

		if (PRINT) {
			System.out.println("Distances expected: " + Arrays.toString(DISTANCES_HEADINGS_EXCLUDED));
			System.out.println("Distances actual:   " + Arrays.toString(actualDistances));
		}

		assertArrayEquals(DISTANCES_HEADINGS_EXCLUDED, actualDistances);

		// Repeat with headings variation

		actualDistances = sectionsHeadingsIncluded.keySet().toArray(new Integer[0]);

		if (PRINT) {
			System.out.println("Distances expected: " + Arrays.toString(DISTANCES_HEADINGS_INCLUDED));
			System.out.println("Distances actual:   " + Arrays.toString(actualDistances));
		}

		assertArrayEquals(DISTANCES_HEADINGS_INCLUDED, actualDistances);

		// Repeat with headings and illustration

		actualDistances = sectionsHeadingsAndIllustration.keySet().toArray(new Integer[0]);

		if (PRINT) {
			System.out.println("Distances expected: " + Arrays.toString(DISTANCES_HEADINGS_AND_ILLUSTRATION));
			System.out.println("Distances actual:   " + Arrays.toString(actualDistances));
		}

		assertArrayEquals(DISTANCES_HEADINGS_AND_ILLUSTRATION, actualDistances);
	}

	/**
	 * Tests, if distances have correct number of parts.
	 */
	@Test
	public void test2DistanceSizes() {
		for (int i = 0; i < DISTANCES_HEADINGS_EXCLUDED.length; i++) {
			int distance = DISTANCES_HEADINGS_EXCLUDED[i];
			int expectedSize = DISTANCE_SIZES_HEADINGS_EXCLUDED[i];
			int actualSize = sectionsHeadingsExcluded.get(distance).size();

			if (PRINT) {
				System.out.println("--------------------------------------------------------------------------------");
				System.out.println(RESOURCE_HEADINGS_EXCLUDED);
				System.out.println("Size expected (" + distance + "): " + expectedSize);
				System.out.println("Size actual   (" + distance + "): " + actualSize);
			}

			assertEquals(expectedSize, actualSize);
		}

		// Repeat with headings variation

		for (int i = 0; i < DISTANCES_HEADINGS_INCLUDED.length; i++) {
			int distance = DISTANCES_HEADINGS_INCLUDED[i];
			int expectedSize = DISTANCE_SIZES_HEADINGS_INCLUDED[i];
			int actualSize = sectionsHeadingsIncluded.get(distance).size();

			if (PRINT) {
				System.out.println("--------------------------------------------------------------------------------");
				System.out.println(RESOURCE_HEADINGS_INCLUDED);
				System.out.println("Size expected (" + distance + "): " + expectedSize);
				System.out.println("Size actual   (" + distance + "): " + actualSize);
			}

			assertEquals(expectedSize, actualSize);
		}

		// Repeat with headings and illustration

		for (int i = 0; i < DISTANCES_HEADINGS_AND_ILLUSTRATION.length; i++) {
			int distance = DISTANCES_HEADINGS_AND_ILLUSTRATION[i];
			int expectedSize = DISTANCE_SIZES_HEADINGS_AND_ILLUSTRATION[i];
			int actualSize = sectionsHeadingsAndIllustration.get(distance).size();

			if (PRINT) {
				System.out.println("--------------------------------------------------------------------------------");
				System.out.println(RESOURCE_HEADINGS_AND_ILLUSTRATION);
				System.out.println("Size expected (" + distance + "): " + expectedSize);
				System.out.println("Size actual   (" + distance + "): " + actualSize);
			}

			assertEquals(expectedSize, actualSize);
		}
	}

	/**
	 * Tests, if all sections contain correct number of empty lines.
	 */
	@Test
	public void test3EmptyLines() {

		// Get indexes of non-empty lines of original text
		SortedSet<Integer> nonEmptyOriginalLines = new TreeSet<Integer>();
		for (Integer index : textHeadingsExcluded.getLineIndexes()) {
			if (!textHeadingsExcluded.getLine(index).trim().isEmpty()) {
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
		for (Integer distance : DISTANCES_HEADINGS_EXCLUDED) {
			nonEmptyLines.clear();
			List<Text> parts = sectionsHeadingsExcluded.get(distance);
			for (Text part : parts) {
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
	 * Tests sections of section.
	 */
	@Test
	public void test4SubSections() {

		// A distance of 4 should exist
		assertTrue(sectionsHeadingsExcluded.containsKey(4));

		// There should be 3 parts divided by a distance of 4
		assertTrue(sectionsHeadingsExcluded.get(4).size() == 3);

		// Last-part of distance of 4
		List<Text> section = sectionsHeadingsExcluded.get(4);
		Text lastPart = section.get(section.size() - 1);

		if (PRINT) {
			System.out.println();
			System.out.println("Section 4, last part:");
			System.out.println(lastPart);
			System.out.println(lastPart.toStringAllLines(true));
		}

		// Get sub-sections of last-part
		SortedMap<Integer, List<Text>> subSections = lastPart.getSections();

		// Last-part has only one break
		assertTrue(subSections.size() == 1);

		// Last-part break consists of two lines
		assertTrue(subSections.containsKey(2));

		// Last-part consists of two parts
		assertTrue(subSections.get(2).size() == 2);

		if (PRINT) {
			System.out.println("Section 4, last part, sub-sections:");
			for (Text subSectionText : subSections.get(2)) {
				System.out.println(subSectionText);
				System.out.println(subSectionText.toStringAllLines(true));
			}
		}

	}
}