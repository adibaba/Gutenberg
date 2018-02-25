package de.adrianwilke.gutenberg.content.re;

import java.util.LinkedList;
import java.util.List;

import de.adrianwilke.gutenberg.utils.Comparators;

/**
 * Searches in sections (a.k.a. text-parts with different distances (a.k.a
 * numbers of empty lines)) for chapter headings.
 * 
 * For each section, it is tested, if the first line of text-parts matches one
 * of several heading variants. In case of a match, the two following text-parts
 * are tested for a match of following headings.
 * 
 * @author Adrian Wilke
 */
public class TxtChapterSearch {

	private int distanceOfFind;
	private int indexOfFind;
	private LinkedList<Integer> usedDistances;

	public TxtChapterSearch() {
		reset();
	}

	protected List<List<String>> getChapterHeadingVariations() {
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
	public int getTextIndexOfFind() {
		return indexOfFind;
	}

	/**
	 * Gets the used distances, which were used as search inputs.
	 */
	public LinkedList<Integer> getUsedDistances() {
		return usedDistances;
	}

	protected void reset() {
		distanceOfFind = -1;
		indexOfFind = -1;
		usedDistances = null;
	}

	/**
	 * Searches for chapters. Preferring long distances can result in smaller
	 * runtime.
	 * 
	 * @param text
	 *            A text to search for chapters
	 * @param preferLongDistances
	 *            Default: true
	 * 
	 * @return if chapters were found
	 */
	public boolean search(Txt text, boolean preferLongDistances) {

		// (Re-)set variables
		reset();

		// Get available distances
		LinkedList<Integer> distances = new LinkedList<Integer>(text.getSections().keySet());

		// Smallest distance is often one empty line. Not interesting for chapters.
		if (distances.size() > 1 && distances.get(0).equals(1)) {
			distances.remove(0);
		}

		// Set preferred distances
		if (preferLongDistances) {
			distances.sort(new Comparators<Integer>().getToLongInverse());
		}
		usedDistances = distances;

		// Use all remaining distances for search
		for (int distanceIndex = 0; distanceIndex < distances.size(); distanceIndex++) {

			// Search for chapters
			int index = searchTextParts(text.getSections().get(distances.get(distanceIndex)));

			// Found chapters! :)
			if (index > 0) {
				distanceOfFind = distances.get(distanceIndex);
				indexOfFind = index;
				return true;
			}
		}
		return false;
	}

	protected int searchTextParts(List<Txt> sectionTexts) {
		List<List<String>> headingVariations = getChapterHeadingVariations();

		// For each text-part of given section
		for (int textIndex = 0; textIndex < sectionTexts.size(); textIndex++) {
			Txt sectionText = sectionTexts.get(textIndex);

			// For each heading variation
			checkHeadingVariations: for (List<String> headingVariation : headingVariations) {

				// Only continue, if start line of text-part matches first heading
				if (sectionText.getLineToLowerCase(sectionText.getLineIndexes().first())
						.startsWith(headingVariation.get(0))) {

					// For additional headings, check additional parts
					for (int headingIndex = 1; headingIndex < headingVariation.size(); headingIndex++) {

						// Stay in scope of available text-parts
						if (sectionTexts.size() < textIndex + headingIndex + 1) {
							continue checkHeadingVariations;
						}

						Txt nextTextPart = sectionTexts.get(textIndex + headingIndex);
						if (!sectionText.getLineToLowerCase(nextTextPart.getLineIndexes().first())
								.startsWith(headingVariation.get(headingIndex))) {
							continue checkHeadingVariations;
						}
					}
					return textIndex;
				}
			}
		}
		return -1;
	}
}