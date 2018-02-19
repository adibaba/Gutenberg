package de.adrianwilke.gutenberg.content_analyzer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.adrianwilke.gutenberg.filesystem.TextFileAccessor;

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

	public static void main(String[] args) {
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

			text1.getPartsRaw();
			System.out.println(text1);
		}

		// Check text lines and text parts
		if (EXECUTE == false) {

			text1.getPartsRaw();
			text2.getPartsRaw();
			text3.getPartsRaw();

			System.out.println(text1);
			System.out.println(text2);
			System.out.println(text3);

			Text currentTextFile = text1;

			// Check start and end line of content
			if (EXECUTE == false) {
				System.out.println(currentTextFile.getContext(currentTextFile.getContentStartLineNumber(), 20));
				System.out.println(currentTextFile.getContext(currentTextFile.getContentEndLineNumber(), 20));
			}

			// Check content parts
			if (EXECUTE == false) {
				List<TextPart> currentContentParts = currentTextFile.getPartsCutted();
				System.out.print(currentContentParts.get(0).getStartIndex() + 1);
				System.out.print(",");
				System.out.println(currentContentParts.get(currentContentParts.size() - 1).getEndIndex() + 1);
			}
		}

		// Get text-parts and text-sections
		if (EXECUTE == false) {
			System.out.println(text1);

			for (Entry<Integer, List<TextPart>> section : text1.getSections().entrySet()) {
				System.out.println(section.getKey() + ": " + section.getValue());
			}
		}

		// Print context of sections
		if (EXECUTE == false) {

			// Get data
			Text currentFile = text1;
			Map<Integer, List<TextPart>> sections = currentFile.getSections();

			// Overview
			for (Entry<Integer, List<TextPart>> section : currentFile.getSections().entrySet()) {
				System.out.println(section.getKey() + ": " + section.getValue().size());
			}
			System.out.println();

			// Print context
			analyzer.printContextOfTextPart(currentFile, sections.get(4), 5);
		}

		if (EXECUTE == false) {
			analyzer.compare(text1, text2);
		}

		if (EXECUTE == true) {

			boolean preferLongDistances = true;

			Text text = text1;
			ChapterSearch chapterSearch1 = new ChapterSearch();
			if (chapterSearch1.search(text, preferLongDistances)) {
				System.out.println(text);
				System.out.println("Found chapters using distances of " + chapterSearch1.getDistanceOfFind());
				System.out.println("Distances: " + chapterSearch1.getUsedDistances());
				System.out.println("Chapters start with index " + chapterSearch1.getIndexOfFind());
				System.out.println();
			}

			text = text2;
			ChapterSearch chapterSearch2 = new ChapterSearch();
			if (chapterSearch2.search(text, preferLongDistances)) {
				System.out.println(text);
				System.out.println("Found chapters using distances of " + chapterSearch2.getDistanceOfFind());
				System.out.println("Distances: " + chapterSearch2.getUsedDistances());
				System.out.println("Chapters start with index " + chapterSearch2.getIndexOfFind());
				System.out.println();
			}

			List<TextPart> textParts1 = text1.getSections().get(chapterSearch1.getDistanceOfFind());
			List<TextPart> textParts2 = text2.getSections().get(chapterSearch2.getDistanceOfFind());

			int ctpIndex1 = chapterSearch1.getIndexOfFind();
			int ctpIndex2 = chapterSearch2.getIndexOfFind();

			System.out.println("First chapter sizes and diff");
			System.out.println(textParts1.get(ctpIndex1).getSize());
			System.out.println(textParts2.get(ctpIndex2).getSize());
			int difA = Math.abs(textParts1.get(ctpIndex1).getSize() - textParts2.get(ctpIndex2).getSize());
			System.out.println(difA);
			System.out.println();

			System.out.println("Second chapter sizes and diff");
			System.out.println(textParts1.get(ctpIndex1 + 1).getSize());
			System.out.println(textParts2.get(ctpIndex2 + 1).getSize());
			int difB = Math.abs(textParts1.get(ctpIndex1 + 1).getSize() - textParts2.get(ctpIndex2 + 1).getSize());
			System.out.println(difB);
			System.out.println();

			System.out.println("Diff for guesses");
			int dif = Math.max(difA, difB) + (Math.min(difA, difB) / 2);
			System.out.println(dif);
			System.out.println();

			// new guess
			System.out.println(textParts1.get(ctpIndex1 + 3).getSize());
			System.out.println(textParts2.get(ctpIndex2 + 2).getSize());

		}
	}

	private static void mainConfigure(String[] args) {
		if (args.length == 3) {
			FILE_PATH = args[0];
			FILE_PATH_2 = args[1];
			FILE_PATH_3 = args[2];
		} else {
			System.err.println("Please set file path.");
			System.exit(1);
		}
	}

	private void compare(Text textFile1, Text textFile2) {

		// Remove non-content
		System.out.println("File: " + textFile1);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<TextPart>> entry : textFile1.getSections().entrySet()) {
			System.out.println("Parts distance " + entry.getKey() + ": " + entry.getValue().size());
		}

		System.out.println();

		// Remove non-content
		System.out.println("File: " + textFile2);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<TextPart>> entry : textFile2.getSections().entrySet()) {
			System.out.println("Parts distance " + entry.getKey() + ": " + entry.getValue().size());
		}
	}

	private void printContextOfTextPart(Text textFile, List<TextPart> textParts, int range) {
		for (int i = 0; i < textParts.size(); i++) {
			TextPart textPart = textParts.get(i);
			System.out.println("[" + i + "]");
			System.out.println(textFile.getContext(textPart.getStartLineNumber(), range));
		}
	}

}