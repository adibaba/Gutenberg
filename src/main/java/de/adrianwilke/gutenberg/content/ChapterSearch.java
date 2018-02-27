package de.adrianwilke.gutenberg.content;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.adrianwilke.gutenberg.utils.Comparators;

/**
 * Searches in sections (a.k.a. text-parts with different distances (a.k.a
 * numbers of empty lines)) for chapter headings.
 * 
 * For each section, it is tested, if the first line of text-parts matches one
 * of several heading variants. In case of a match, the two following text-parts
 * are tested for a match of following headings.
 * 
 * TODO: Search for common words in three headings. Search for more headings.
 * 
 * TODO: Eventually, search only for numbers in first run. Compare, if there are
 * multiple founds.
 * 
 * @author Adrian Wilke
 */
public class ChapterSearch {

	private SortedSet<Text> chapters;
	private int distanceOfFind;
	private int headingIndexOfFind;
	private int indexOfFind;
	private Text text;
	private LinkedList<Integer> usedDistances;

	public ChapterSearch(Text text) {
		this.text = text;

		distanceOfFind = -1;
		indexOfFind = -1;
		usedDistances = null;
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
		chapterHeadings.add("4");
		chapterHeadings.add("5");
		chapterHeadings.add("6");
		chapterHeadings.add("7");
		chapterHeadings.add("8");
		chapterHeadings.add("9");
		chapterHeadings.add("10");
		chapterHeadings.add("11");
		chapterHeadings.add("12");
		chapterHeadings.add("13");
		chapterHeadings.add("14");
		chapterHeadings.add("15");
		chapterHeadings.add("16");
		chapterHeadings.add("17");
		chapterHeadings.add("18");
		chapterHeadings.add("19");
		chapterHeadings.add("20");
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
		chapterHeadings.add("iv");
		chapterHeadings.add("v");
		chapterHeadings.add("vi");
		chapterHeadings.add("vii");
		chapterHeadings.add("viii");
		chapterHeadings.add("ix");
		chapterHeadings.add("x");
		chapterHeadings.add("xi");
		chapterHeadings.add("xii");
		chapterHeadings.add("xiii");
		chapterHeadings.add("xiv");
		chapterHeadings.add("xv");
		chapterHeadings.add("xvi");
		chapterHeadings.add("xvii");
		chapterHeadings.add("xviii");
		chapterHeadings.add("xix");
		chapterHeadings.add("xx");
		chapterHeadingsList.add(chapterHeadings);

		for (String language : languagePrefixes) {
			List<String> generated = new LinkedList<String>();
			for (String chapterHeading : chapterHeadings) {
				generated.add(language + " " + chapterHeading);
			}
			chapterHeadingsList.add(generated);
		}

		// 19778
		chapterHeadings = new LinkedList<String>();
		chapterHeadings.add("erstes");
		chapterHeadings.add("zweites");
		chapterHeadings.add("drittes");
		chapterHeadings.add("viertes");
		chapterHeadings.add("fuenftes");
		chapterHeadings.add("sechstes");
		chapterHeadings.add("siebentes"); // jawohl
		chapterHeadings.add("achtes");
		chapterHeadings.add("neuntes");
		chapterHeadings.add("zehntes");
		chapterHeadings.add("elftes");
		chapterHeadings.add("zwoelftes");
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

	/**
	 * Searches for chapters. Preferring long distances can result in smaller
	 * runtime.
	 * 
	 * Returns a map: Key: heading number, Value: Value: text.
	 * 
	 * Returns null, if nothing found.
	 * 
	 * @param text
	 *            A text to search for chapters
	 * @param preferLongDistances
	 *            Default: true
	 */
	public boolean search(boolean preferLongDistances) {

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
			SortedSet<Text> chapters = searchInSection(text.getSections().get(distances.get(distanceIndex)));

			// Found chapters! :)
			if (chapters != null) {
				distanceOfFind = distances.get(distanceIndex);
				this.chapters = chapters;
				return true;
			}
		}
		return false;
	}

	/**
	 * Uses found section to search for heading after heading 2.
	 */
	public SortedSet<Text> searchAdditionalHeadings() {
		List<Text> sectionOfFind = text.getSections().get(distanceOfFind);
		List<String> headings = getChapterHeadingVariations().get(headingIndexOfFind);
		int startIndex = indexOfFind + 1;
		for (int headingIndex = 2; headingIndex < headings.size(); headingIndex++) {
			for (int i = startIndex; i < sectionOfFind.size(); i++) {
				Text textPart = sectionOfFind.get(i);
				if (textPart.getLineSimplified(textPart.getFirstIndex()).startsWith(headings.get(headingIndex))) {
					chapters.add(new TextPart(text, "chapter" + (headingIndex + 1), textPart.getFirstIndex(),
							textPart.getLastIndex()));
				}
			}
		}

		return chapters;
	}

	/**
	 * Searches in one section for possible variations for first heading.
	 * 
	 * In case of a match, the two following text-parts are tested for a match of
	 * following headings.
	 * 
	 * Returns a map: Key: heading number, Value: Value: text.
	 * 
	 * Returns null, if nothing found.
	 */
	@SuppressWarnings("unused")
	SortedSet<Text> searchInSection(List<Text> textsOfSection) {
		List<List<String>> headingVariations = getChapterHeadingVariations();

		// For each text-part of given section ...
		for (int textPartIndex = 0; textPartIndex < textsOfSection.size(); textPartIndex++) {
			Text text = textsOfSection.get(textPartIndex);

			// ... and for each heading variation
			checkHeadingVariations: for (int h = 0; h < headingVariations.size(); h++) {
				List<String> headings = headingVariations.get(h);

				// Search for first heading
				if (text.getLineSimplified(text.getFirstIndex()).startsWith(headings.get(0))) {

					// Search for headings 2 and 3
					SortedSet<Text> chapters = searchTextPartsNext(textsOfSection, textPartIndex, headings);
					if (chapters == null) {
						// Additional heading not found
						continue checkHeadingVariations;
					} else {
						// Additional heading found
						chapters.add(new TextPart(this.text, "chapter1", text.getFirstIndex(), text.getLastIndex()));
					}

					headingIndexOfFind = h;
					indexOfFind = textPartIndex;
					return chapters;
				}
			}
		}

		// Headings not found
		return null;
	}

	/**
	 * Searches for headings 2 and 3
	 * 
	 * Returns a map: Key: heading number, Value: text.
	 * 
	 * Returns null, if nothing found.
	 */
	protected SortedSet<Text> searchTextPartsNext(List<Text> texts, int textPartIndex, List<String> headings) {
		SortedSet<Text> chapters = new TreeSet<Text>();
		Text text = texts.get(textPartIndex);

		// For additional heading 2 and heading 3, check following text-parts
		for (int headingIndex = 1; headingIndex <= 2; headingIndex++) {

			// Text-index of first find (stays) plus 1 or 2
			int nextTextPartIndex = textPartIndex + headingIndex;

			// Prevent NPE
			if (nextTextPartIndex + 1 > texts.size()) {
				return null;
			}

			// If heading not found in text-part, skip and check next variation
			Text nextText = texts.get(nextTextPartIndex);

			if (text.getLineSimplified(nextText.getFirstIndex()).startsWith(headings.get(headingIndex))) {
				TextPart newTextPart = new TextPart(this.text, "chapter" + (headingIndex + 1), nextText.getFirstIndex(),
						nextText.getLastIndex());
				chapters.add(newTextPart);

			} else {

				// TODO: Prevent NPE
				// if (nextTextPartIndex + 1 > texts.size()) {
				// return null;
				// }
				
				// Hack to also check the next text-part
				nextText = texts.get(nextTextPartIndex + 1);
				if (text.getLineSimplified(nextText.getFirstIndex()).startsWith(headings.get(headingIndex))) {
					TextPart newTextPart = new TextPart(this.text, "chapter" + (headingIndex + 1),
							nextText.getFirstIndex(), nextText.getLastIndex());
					chapters.add(newTextPart);
					System.out.println("Chapter search hack used for " + newTextPart.toString());
				} else {

					// Additional heading not found
					return null;
				}
			}
		}

		// All additional headings found
		return chapters;
	}
}