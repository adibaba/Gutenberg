package de.adrianwilke.gutenberg.content_analyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.adrianwilke.gutenberg.data.TextFileAccessor;

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

		// Get boundaries
		if (EXECUTE == false) {
			TextFile textFile = new TextFile(FILE_PATH, TextFileAccessor.ISO_8859_1);
			System.out.println(textFile);

			// Note: 19778-8 [42 (30), 3729 (3735)]
			analyzer.getBoundaries(textFile);
			System.out.println(textFile);

			textFile.getParts();
			System.out.println(textFile);
		}

		// Check text lines and text parts
		if (EXECUTE == false) {
			TextFile textFile1 = new TextFile(FILE_PATH, TextFileAccessor.ISO_8859_1);
			TextFile textFile2 = new TextFile(FILE_PATH_2, TextFileAccessor.ISO_8859_1);
			TextFile textFile3 = new TextFile(FILE_PATH_3, TextFileAccessor.UTF_8);

			analyzer.getBoundaries(textFile1);
			analyzer.getBoundaries(textFile2);
			analyzer.getBoundaries(textFile3);

			textFile1.getParts();
			textFile2.getParts();
			textFile3.getParts();

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
				List<TextPart> currentContentParts = currentTextFile.getPartsOfContent();
				System.out.print(currentContentParts.get(0).getStartIndex() + 1);
				System.out.print(",");
				System.out.println(currentContentParts.get(currentContentParts.size() - 1).getEndIndex() + 1);
			}
		}

		// Get text-parts and text-sections
		if (EXECUTE == true) {
			TextFile textFile = new TextFile(FILE_PATH, TextFileAccessor.ISO_8859_1);
			analyzer.getBoundaries(textFile);
			System.out.println(textFile);

			System.out.println("1: " + textFile.getPartsOfContent());

			Map<Integer, List<TextPart>> textSections = TextPart.textPartsToSections(textFile.getPartsOfContent());
			for (Integer distance : textSections.keySet()) {
				System.out.println(distance + ": " + textSections.get(distance));
			}
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

	public void getBoundaries(TextFile textFile) {

		int startLineIndex = -1;
		int endLineIndex = getBoundaryEndIndex(textFile.getLines(), 0);

		if (endLineIndex != -1) {
			startLineIndex = getBoundaryStartIndex(textFile.getLines(), endLineIndex);
		} else {
			startLineIndex = getBoundaryStartIndex(textFile.getLines(), textFile.getLines().size() - 1);
		}

		if (startLineIndex < 0) {
			System.err.println(TextFileAnalyzer.class.getName() + "No start line found.");
		} else {
			textFile.setContentStartIndex(startLineIndex);
		}

		if (endLineIndex < 0) {
			System.err.println(TextFileAnalyzer.class.getName() + "No end line found.");
		} else {
			textFile.setContentEndIndex(endLineIndex);
		}
	}

	private int getBoundaryEndIndex(List<String> lines, int startLine) {
		int lineIndex = -1;

		// Begin at first line, to match first line containing a candidate
		lineloop: for (int i = startLine; i < lines.size(); i++) {
			String line = lines.get(i);

			for (String candidate : getBoundaryEndCandidates()) {
				if (line.startsWith(candidate)) {
					lineIndex = i - 1;
					break lineloop;
				}
			}
		}

		// Remove empty lines
		for (int i = lineIndex - 1; i >= 0; i--) {
			String line = lines.get(i);
			if (!line.isEmpty()) {
				lineIndex = i;
				break;
			}
		}

		return lineIndex;
	}

	private List<String> getBoundaryEndCandidates() {
		List<String> list = new LinkedList<String>();
		list.add("End of Project Gutenberg"); // 19778-8, line 3735 / 11-0, line 3376 (different following apostrophes)
		list.add("*** END OF THIS PROJECT GUTENBERG EBOOK"); // 19778-8, line 3737
		list.add("End of the Project Gutenberg EBook of"); // 19033-8, line 1341
		return list;
	}

	private int getBoundaryStartIndex(List<String> lines, int startLine) {
		int lineIndex = -1;

		// Begin at last line, to match last line containing a candidate
		lineloop: for (int i = startLine; i >= 0; i--) {
			for (String candidate : getBoundaryStartCandidates()) {
				if (lines.get(i).startsWith(candidate)) {
					lineIndex = i + 1;
					break lineloop;
				}
			}
		}

		// Remove empty lines
		for (int i = lineIndex + 1; i < lines.size(); i++) {
			if (!lines.get(i).isEmpty()) {
				lineIndex = i;
				break;
			}
		}

		return lineIndex;
	}

	private List<String> getBoundaryStartCandidates() {
		List<String> list = new LinkedList<String>();
		list.add("*** START OF THIS PROJECT GUTENBERG EBOOK"); // 19778-8, line 25
		list.add("Team at http://www.pgdp.net"); // 19778-8, line 32
		list.add("Distributed Proofreading Team at http://www.pgdp.net"); // 19033-8, line 27
		return list;
	}

}