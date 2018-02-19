package de.adrianwilke.gutenberg.content_analyzer;

import java.util.LinkedList;
import java.util.List;

import de.adrianwilke.gutenberg.utils.Comparators;

/**
 * Searches in sections (a.k.a. text parts with different distances (a.k.a
 * numbers of empty lines)) for chapter headings.
 * 
 * @author Adrian Wilke
 */
public class ChapterSearch {

	public static boolean EXECUTE = true;

	public static void main(String[] args) {
		if (EXECUTE == false) {
			new ChapterSearch().printChapterHeadings();
		}
	}

	private int distanceOfFind = -1;
	private int indexOfFind = -1;
	private Text textFile;
	private LinkedList<Integer> usedDistances;

	private List<List<String>> getChapterHeadingVariations() {
		List<List<String>> chapterHeadingsList = new LinkedList<List<String>>();

		List<String> languagePrefixes;
		languagePrefixes = new LinkedList<String>();
		languagePrefixes.add("chapter");
		languagePrefixes.add("kapitel");

		List<String> chapterHeadings;

		chapterHeadings = new LinkedList<String>();
		chapterHeadings.add("1");
		chapterHeadings.add("2");
		chapterHeadings.add("3");
		chapterHeadingsList.add(chapterHeadings);

		for (String language : languagePrefixes) {
			List<String> generated = new LinkedList<String>();
			for (String chapterHeading : chapterHeadings) {
				generated.add(language + " " + chapterHeading);
			}
			chapterHeadingsList.add(generated);
		}

		chapterHeadings = new LinkedList<String>();
		chapterHeadings.add("i");
		chapterHeadings.add("ii");
		chapterHeadings.add("iii");
		chapterHeadingsList.add(chapterHeadings);

		for (String language : languagePrefixes) {
			List<String> generated = new LinkedList<String>();
			for (String chapterHeading : chapterHeadings) {
				generated.add(language + " " + chapterHeading);
			}
			chapterHeadingsList.add(generated);
		}

		chapterHeadings = new LinkedList<String>();
		chapterHeadings.add("erstes");
		chapterHeadings.add("zweites");
		chapterHeadings.add("drittes");
		chapterHeadingsList.add(chapterHeadings);

		return chapterHeadingsList;
	}

	private void printChapterHeadings() {
		for (List<String> chapterHeadingVariation : getChapterHeadingVariations()) {
			for (String chapterHeading : chapterHeadingVariation) {
				System.out.println(chapterHeading);
			}
			System.out.println();
		}
	}

	/**
	 * Gets used distance of find.
	 * 
	 * @return -1, if no chapters were found
	 */
	public int getDistanceOfFind() {
		return distanceOfFind;
	}

	/**
	 * Gets the text-part index of find.
	 * 
	 * @return -1, if no chapters were found
	 */
	public int getIndexOfFind() {
		return indexOfFind;
	}

	/**
	 * Gets the used distances, which were used as search inputs.
	 */
	public LinkedList<Integer> getUsedDistances() {
		return usedDistances;
	}

	/**
	 * Searches for chapters. Preferring long distances can result in smaller
	 * runtime.
	 * 
	 * @return if chapters were found
	 */
	public boolean search(Text textFile, boolean preferLongDistances) {

		// (Re-)set variables
		distanceOfFind = -1;
		indexOfFind = -1;
		usedDistances = null;
		this.textFile = textFile;

		// Get available distances
		LinkedList<Integer> distances = new LinkedList<Integer>(textFile.getSections().keySet());

		// Smallest distance is often one empty line. Not interesting for chapters.
		distances.remove(0);

		// Set preferred distances
		if (preferLongDistances) {
			distances.sort(new Comparators<Integer>().getToLongInverse());
		}

		// Use all remaining distances for search
		for (int i = 0; i < distances.size(); i++) {

			// Search for chapters
			int index = searchTextParts(textFile.getSections().get(distances.get(i)));

			// Found chapters! :)
			if (index > 0) {

				distanceOfFind = distances.get(i);
				indexOfFind = index;
				usedDistances = distances;
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unused")
	private int searchTextParts(List<TextPart> textParts) {
		List<List<String>> chapterVariations = getChapterHeadingVariations();

		// For each text-part
		for (int i = 0; i < textParts.size(); i++) {
			TextPart textPart = textParts.get(i);

			// For each heading variant
			checkHeadingVariations: for (List<String> headingVariation : chapterVariations) {

				// Only continue, if start line of text-part matches first heading
				if (textFile.getLines().get(textPart.getStartIndex()).toLowerCase()
						.startsWith(headingVariation.get(0))) {

					// For additional headings, check additional parts
					for (int j = 1; j < chapterVariations.size(); j++) {
						TextPart nextTextPart = textParts.get(i + j);
						if (!textFile.getLines().get(nextTextPart.getStartIndex()).toLowerCase()
								.startsWith(headingVariation.get(j))) {
							continue checkHeadingVariations;
						}
						return i;
					}
				}
			}
		}
		return -1;
	}
}