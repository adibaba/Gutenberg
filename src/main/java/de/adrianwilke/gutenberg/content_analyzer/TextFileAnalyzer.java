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

		TextFile textFile1 = new TextFile(FILE_PATH, TextFileAccessor.ISO_8859_1);
		TextFile textFile2 = new TextFile(FILE_PATH_2, TextFileAccessor.ISO_8859_1);
		TextFile textFile3 = new TextFile(FILE_PATH_3, TextFileAccessor.UTF_8);

		// Get boundaries
		if (EXECUTE == false) {
			System.out.println(textFile1);

			// Note: 19778-8 [42 (30), 3729 (3735)]
			System.out.println(textFile1);

			textFile1.getPartsRaw();
			System.out.println(textFile1);
		}

		// Check text lines and text parts
		if (EXECUTE == false) {

			textFile1.getPartsRaw();
			textFile2.getPartsRaw();
			textFile3.getPartsRaw();

			System.out.println(textFile1);
			System.out.println(textFile2);
			System.out.println(textFile3);

			TextFile currentTextFile = textFile1;

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
			System.out.println(textFile1);

			for (Entry<Integer, List<TextPart>> section : textFile1.getSections().entrySet()) {
				System.out.println(section.getKey() + ": " + section.getValue());
			}
		}

		// Print context of sections
		if (EXECUTE == false) {

			// Get data
			TextFile currentFile = textFile1;
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
			analyzer.compare(textFile1, textFile2);
		}

		if (EXECUTE == true) {
			boolean preferLongDistances = true;

			new ChapterSearch().search(textFile1, preferLongDistances);

			new ChapterSearch().search(textFile2, preferLongDistances);

			new ChapterSearch().search(textFile3, preferLongDistances);
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

	private void compare(TextFile textFile1, TextFile textFile2) {

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

	private void printContextOfTextPart(TextFile textFile, List<TextPart> textParts, int range) {
		for (int i = 0; i < textParts.size(); i++) {
			TextPart textPart = textParts.get(i);
			System.out.println("[" + i + "]");
			System.out.println(textFile.getContext(textPart.getStartLineNumber(), range));
		}
	}

}