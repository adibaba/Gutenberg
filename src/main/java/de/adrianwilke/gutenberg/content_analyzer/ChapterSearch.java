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

	TextFile textFile;

	public void search(TextFile textFile, boolean preferLongDistances) {

		this.textFile = textFile;

		// Begin with biggest distance, do not use smallest distance
		LinkedList<Integer> distances = new LinkedList<Integer>(textFile.getSections().keySet());
		distances.remove(0);

		if (preferLongDistances) {
			distances.sort(new Comparators<Integer>().getToLongInverse());
		}

		for (int i = 0; i < distances.size(); i++) {
			int index = searchTextParts(textFile.getSections().get(distances.get(i)));
			if (index > 0) {
				System.out.println(textFile);
				System.out.println("Found chapters using distances of " + distances.get(i));
				System.out.println("Distances: " + distances);
				System.out.println("Chapters start with index " + index);
				System.out.println();
				break;
			}
		}
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

	public static void main(String[] args) {
		if (EXECUTE == false) {
			new ChapterSearch().printChapterHeadings();
		}
	}
}