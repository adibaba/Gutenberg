package de.adrianwilke.gutenberg.content;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.adrianwilke.gutenberg.generators.HtmlGenerator;
import de.adrianwilke.gutenberg.io.TextFileAccessor;

/**
 * Analyzes content of text files.
 * 
 * @author Adrian Wilke
 */
public class TextFileAnalyzer {

	public static boolean EXECUTE = true;
	public static String FILE_PATH;
	public static String FILE_PATH_2;
	public static String FILE_PATH_3;

	public static void main(String[] args) throws FileNotFoundException {
		mainConfigure(args);
		TextFileAnalyzer analyzer = new TextFileAnalyzer();

		Text text1 = new Text(FILE_PATH, TextFileAccessor.ISO_8859_1);
		Text text2 = new Text(FILE_PATH_2, TextFileAccessor.ISO_8859_1);
		Text text3 = new Text(FILE_PATH_3, TextFileAccessor.UTF_8);

		// Get boundaries
		if (EXECUTE == false) {
			System.out.println(text1);

			// Note: 19778-8 [42 (30), 3729 (3735)]
			System.out.println(text1);

			text1.getParts();
			System.out.println(text1);
			System.out.println();
		}

		// Check text lines and text parts
		if (EXECUTE == false) {

			text1.getParts();
			text2.getParts();
			text3.getParts();

			System.out.println(text1);
			System.out.println(text2);
			System.out.println(text3);

			Text currentTextFile = text1;

			// Check start and end line of content
			if (EXECUTE == true) {
				System.out.println(currentTextFile.getContext(currentTextFile.getContentStartLineNumber(), 20));
				System.out.println(currentTextFile.getContext(currentTextFile.getContentEndLineNumber(), 20));
			}

			// Check content parts
			if (EXECUTE == true) {
				List<Part> currentContentParts = currentTextFile.getPartsCut();
				System.out.print(currentContentParts.get(0).getStartIndex() + 1);
				System.out.print(",");
				System.out.println(currentContentParts.get(currentContentParts.size() - 1).getEndIndex() + 1);
			}
		}

		// Get text-parts and text-sections
		if (EXECUTE == false) {
			System.out.println(text1);

			for (Entry<Integer, List<Part>> section : text1.getSections().entrySet()) {
				System.out.println(section.getKey() + ": " + section.getValue());
			}
		}

		// Print context of sections
		if (EXECUTE == false) {

			// Get data
			Text currentFile = text1;
			Map<Integer, List<Part>> sections = currentFile.getSections();

			// Overview
			for (Entry<Integer, List<Part>> section : currentFile.getSections().entrySet()) {
				System.out.println(section.getKey() + ": " + section.getValue().size());
			}
			System.out.println();

			// Print context
			analyzer.printContextOfTextPart(currentFile, sections.get(4), 5);
		}

		if (EXECUTE== false ) {
			analyzer.compare(text1, text2);
		}

		if (EXECUTE ) {

			boolean preferLongDistances = true;

			Text textA = text1;
			ChapterSearch chapterSearchA = new ChapterSearch();
			if (chapterSearchA.search(textA, preferLongDistances)) {
				System.out.println(textA);
				System.out.println("Found chapters using distances of " + chapterSearchA.getDistanceOfFind());
				System.out.println("Distances: " + chapterSearchA.getUsedDistances());
				System.out.println("Chapters start with index " + chapterSearchA.getIndexOfFind());
				System.out.println();
			}

			Text textB = text2;
			ChapterSearch chapterSearchB = new ChapterSearch();
			if (chapterSearchB.search(textB, preferLongDistances)) {
				System.out.println(textB);
				System.out.println("Found chapters using distances of " + chapterSearchB.getDistanceOfFind());
				System.out.println("Distances: " + chapterSearchB.getUsedDistances());
				System.out.println("Chapters start with index " + chapterSearchB.getIndexOfFind());
				System.out.println();
			}

			List<Part> textPartsA = textA.getSections().get(chapterSearchA.getDistanceOfFind());
			List<Part> textPartsB = textB.getSections().get(chapterSearchB.getDistanceOfFind());

			int ctpIndexA = chapterSearchA.getIndexOfFind();
			int ctpIndexB = chapterSearchB.getIndexOfFind();

			System.out.println("First chapter sizes and diff");
			System.out.println(textPartsA.get(ctpIndexA).getNumberOfLines());
			System.out.println(textPartsB.get(ctpIndexB).getNumberOfLines());
			int difA = Math
					.abs(textPartsA.get(ctpIndexA).getNumberOfLines() - textPartsB.get(ctpIndexB).getNumberOfLines());
			System.out.println(difA);
			System.out.println();

			System.out.println("Second chapter sizes and diff");
			System.out.println(textPartsA.get(ctpIndexA + 1).getNumberOfLines());
			System.out.println(textPartsB.get(ctpIndexB + 1).getNumberOfLines());
			int difB = Math.abs(textPartsA.get(ctpIndexA + 1).getNumberOfLines()
					- textPartsB.get(ctpIndexB + 1).getNumberOfLines());
			System.out.println(difB);
			System.out.println();

			System.out.println("Diff for guesses");
			int dif = Math.max(difA, difB) + (Math.min(difA, difB) / 2);
			System.out.println(dif);
			System.out.println();

			// new guess
			System.out.println(textPartsA.get(ctpIndexA + 3).getNumberOfLines());
			System.out.println(textPartsB.get(ctpIndexB + 2).getNumberOfLines());

			// generate html
			HtmlGenerator html = new HtmlGenerator(textA, textB);
			html.generateHeader()
					.generateCells(textA.getSections().get(chapterSearchA.getDistanceOfFind()),
							textB.getSections().get(chapterSearchB.getDistanceOfFind()),
							chapterSearchA.getIndexOfFind(), chapterSearchB.getIndexOfFind())
					.generateFooter();

			TextFileAccessor.writeStringToFile(html.toString(), args[0] + "/test.htm");
		}

		if (EXECUTE == false) {
			List<Part> parts1 = text1.getParts();
			for (int i = 0; i < 3; i++) {
				if (parts1.size() < i) {
					break;
				}
				Part part = parts1.get(i);
				for (int j = part.getStartIndex(); j <= part.getEndIndex(); j++) {
					System.out.println(text1.getLines().get(j));
					System.out.println(text1.wordCount(j));
				}
				System.out.println(text1.wordCount(part));

			}
		}
	}

	private static void mainConfigure(String[] args) {
		if (args.length == 4) {
			FILE_PATH = args[1];
			FILE_PATH_2 = args[2];
			FILE_PATH_3 = args[3];
		} else {
			System.err.println("Please set file path.");
			System.exit(1);
		}
	}

	private void compare(Text textFile1, Text textFile2) {

		// Remove non-content
		System.out.println("File: " + textFile1);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Part>> entry : textFile1.getSections().entrySet()) {
			System.out.println("Parts distance " + entry.getKey() + ": " + entry.getValue().size());
		}

		System.out.println();

		// Remove non-content
		System.out.println("File: " + textFile2);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Part>> entry : textFile2.getSections().entrySet()) {
			System.out.println("Parts distance " + entry.getKey() + ": " + entry.getValue().size());
		}
	}

	private void printContextOfTextPart(Text textFile, List<Part> textParts, int range) {
		for (int i = 0; i < textParts.size(); i++) {
			Part textPart = textParts.get(i);
			System.out.println("[" + i + "]");
			System.out.println(textFile.getContext(textPart.getStartLineNumber(), range));
		}
	}

}