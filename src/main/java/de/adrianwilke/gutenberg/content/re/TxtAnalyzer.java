package de.adrianwilke.gutenberg.content.re;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import de.adrianwilke.gutenberg.generators.HtmlGenerator;
import de.adrianwilke.gutenberg.io.TextFileAccessor;

/**
 * Human comparison of texts.
 * 
 * @author Adrian Wilke
 */
public class TxtAnalyzer {

	protected static boolean EXECUTE = false;
	protected static List<String> FILE_CHARSETS;
	protected static List<String> FILE_PATHS;
	protected static File DIRECTORY_GENERATION;

	public static void main(String[] args) {
		mainConfigure(args);

		List<Txt> texts = new LinkedList<Txt>();
		for (int i = 0; i < FILE_PATHS.size(); i++) {
			texts.add(new FullTxt(FILE_PATHS.get(i), FILE_CHARSETS.get(i)));
		}

		List<Txt> textsCut = new LinkedList<Txt>();
		TxtCutter cutter = new TxtCutter();
		for (int i = 0; i < FILE_PATHS.size(); i++) {
			textsCut.add(cutter.cut(texts.get(i)));
		}

		// -------------------------------------------------------------------------------------------------------------
		// Context of sections
		if (EXECUTE) {
			Txt txt = textsCut.get(0);
			int range = 2;
			int section = 4;

			System.out.println("Text:");
			System.out.println(txt);
			System.out.println();

			System.out.println("Overview:");
			for (Entry<Integer, List<Txt>> sectionEntry : txt.getSections().entrySet()) {
				System.out
						.println("Section " + sectionEntry.getKey() + ": " + sectionEntry.getValue().size() + " parts");
			}
			System.out.println();

			System.out.println("Context of section " + section + ":");
			System.out.println();
			new TxtAnalyzer().printContextOfTextPart(txt.getSections().get(section), range);
			System.out.println();
		}

		// -------------------------------------------------------------------------------------------------------------
		// Parts in sections
		if (EXECUTE) {
			Txt textA = textsCut.get(0);
			Txt textB = textsCut.get(1);
			new TxtAnalyzer().printComparison(textA, textB);
		}

		// -------------------------------------------------------------------------------------------------------------
		// Search for chapters
		// Generate HTML
		if (EXECUTE) {
			boolean preferLongDistances = true;
			Txt textA = textsCut.get(0);
			Txt textB = textsCut.get(1);

			// Search for chapters in text A
			TxtChapterSearch chapterSearchA = new TxtChapterSearch();
			if (!chapterSearchA.search(textA, preferLongDistances)) {
				System.err.println("No chapters found in text A:");
				System.err.println(textA);
				System.exit(1);
			}
			System.out.println(textA);
			System.out.println("Found chapters using distances of " + chapterSearchA.getDistanceOfFind());
			System.out.println("Distances: " + chapterSearchA.getUsedDistances());
			System.out.println("Chapters start with index " + chapterSearchA.getTextIndexOfFind());
			System.out.println();

			// Search for chapters in text B
			TxtChapterSearch chapterSearchB = new TxtChapterSearch();
			if (!chapterSearchB.search(textB, preferLongDistances)) {
				System.err.println("No chapters found in text B:");
				System.err.println(textB);
			}
			System.out.println(textB);
			System.out.println("Found chapters using distances of " + chapterSearchB.getDistanceOfFind());
			System.out.println("Distances: " + chapterSearchB.getUsedDistances());
			System.out.println("Chapters start with index " + chapterSearchB.getTextIndexOfFind());
			System.out.println();

			// Try to find good number of lines to search for next chapters
			System.out.println("Searching for good diffs");
			System.out.println();

			List<Txt> chapterPartsA = textA.getSections().get(chapterSearchA.getDistanceOfFind());
			List<Txt> chapterPartsB = textB.getSections().get(chapterSearchB.getDistanceOfFind());

			int chapterPartsIndexA = chapterSearchA.getTextIndexOfFind();
			int chapterPartsIndexB = chapterSearchB.getTextIndexOfFind();

			System.out.println("First chapter sizes and diff");
			System.out.println(chapterPartsA.get(chapterPartsIndexA).getLineIndexes().size());
			System.out.println(chapterPartsB.get(chapterPartsIndexB).getLineIndexes().size());
			int diffA = Math.abs(chapterPartsA.get(chapterPartsIndexA).getLineIndexes().size()
					- chapterPartsB.get(chapterPartsIndexB).getLineIndexes().size());
			System.out.println("Diff: " + diffA);
			System.out.println();

			System.out.println("Second chapter sizes and diff");
			System.out.println(chapterPartsA.get(chapterPartsIndexA + 1).getLineIndexes().size());
			System.out.println(chapterPartsB.get(chapterPartsIndexB + 1).getLineIndexes().size());
			int diffB = Math.abs(chapterPartsA.get(chapterPartsIndexA + 1).getLineIndexes().size()
					- chapterPartsB.get(chapterPartsIndexB + 1).getLineIndexes().size());
			System.out.println("Diff: " + diffB);
			System.out.println();

			System.out.println("Diff for guesses");
			int dif = Math.max(diffA, diffB) + (Math.min(diffA, diffB) / 2);
			System.out.println(dif);
			System.out.println();

			// New guess
			System.out.println(chapterPartsA.get(chapterPartsIndexA + 3).getLineIndexes().size());
			System.out.println(chapterPartsB.get(chapterPartsIndexB + 2).getLineIndexes().size());

			// Generate html
			HtmlGenerator html = new HtmlGenerator(textA, textB);
			html.generateHeader()
					.generateCells(textA.getSections().get(chapterSearchA.getDistanceOfFind()),
							textB.getSections().get(chapterSearchB.getDistanceOfFind()),
							chapterSearchA.getTextIndexOfFind(), chapterSearchB.getTextIndexOfFind())
					.generateFooter();

			String filePath = new File(DIRECTORY_GENERATION, "/test.htm").getPath();
			TextFileAccessor.writeStringToFile(html.toString(), filePath);
		}
	}

	private static void mainConfigure(String[] args) {
		if (args.length == 0) {
			System.err.println("Please check arguments.");
			System.exit(1);
		} else if (args.length % 2 != 1) {
			System.err.println("Please check arguments.");
			System.exit(1);
		} else {
			DIRECTORY_GENERATION = new File(args[0]);
			System.out.println("Generation directory:");
			System.out.println(DIRECTORY_GENERATION);
			FILE_PATHS = new LinkedList<String>();
			FILE_CHARSETS = new LinkedList<String>();
			System.out.println("Files in arguments:");
			for (int argIndex = 1; argIndex < args.length; argIndex += 2) {
				System.out.print(((argIndex / 2) + 1));
				FILE_PATHS.add(args[argIndex]);
				System.out.print(" " + args[argIndex]);
				FILE_CHARSETS.add(args[(argIndex + 1)]);
				System.out.println(" " + args[(argIndex + 1)]);
			}
			System.out.println();
		}
	}

	/**
	 * Prints comparison of parts in sections
	 */
	protected void printComparison(Txt textA, Txt textB) {

		System.out.println("File: " + textA);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Txt>> section : textA.getSections().entrySet()) {
			System.out.println("Number of parts in section " + section.getKey() + ": " + section.getValue().size());
		}

		System.out.println();

		System.out.println("File: " + textB);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Txt>> section : textB.getSections().entrySet()) {
			System.out.println("Number of parts in section " + section.getKey() + ": " + section.getValue().size());
		}
	}

	/**
	 * Prints context of first line in text-parts
	 */
	protected void printContextOfTextPart(List<Txt> list, int range) {
		for (int i = 0; i < list.size(); i++) {
			Txt textPart = list.get(i);
			System.out.println("[" + i + "]");
			System.out.println(textPart.getContext(textPart.getLineIndexes().first() + 1, range));
		}
	}

}