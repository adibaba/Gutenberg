package de.adrianwilke.gutenberg.content_analyzer;

import java.util.LinkedList;
import java.util.List;

/**
 * Searches for non-content strings at top and bottom of text. If found, the
 * boundaries are written to the respective text-part.
 * 
 * @author Adrian Wilke
 */
public class Cutter {

	private int getBoundaryIndexOfEnd(List<String> lines, int startLineIndex) {
		int returnLineIndex = -1;

		// Begin at first line, to match first line containing a candidate
		lineloop: for (int i = startLineIndex; i < lines.size(); i++) {
			String line = lines.get(i);

			for (String candidate : getBoundaryLineCandidatesForEnd()) {
				if (line.startsWith(candidate)) {
					// Do not include search string
					returnLineIndex = i - 1;
					break lineloop;
				}
			}
		}

		// Do not include empty lines
		for (int i = returnLineIndex - 1; i >= 0; i--) {
			String line = lines.get(i);
			if (!line.isEmpty()) {
				returnLineIndex = i;
				break;
			}
		}

		return returnLineIndex;
	}

	private int getBoundaryIndexOfStart(List<String> lines, int endLineIndex) {
		int returnLineIndex = -1;

		// Begin at last line, to match last line containing a candidate
		lineloop: for (int i = endLineIndex; i >= 0; i--) {
			for (String candidate : getBoundaryLineCandidatesForStart()) {
				if (lines.get(i).startsWith(candidate)) {
					// Do not include search string
					returnLineIndex = i + 1;
					break lineloop;
				}
			}
		}

		// Do not include empty lines
		for (int i = returnLineIndex + 1; i < lines.size(); i++) {
			if (!lines.get(i).isEmpty()) {
				returnLineIndex = i;
				break;
			}
		}

		return returnLineIndex;
	}

	private List<String> getBoundaryLineCandidatesForEnd() {
		List<String> list = new LinkedList<String>();
		list.add("End of Project Gutenberg"); // 19778-8, line 3735 / 11-0, line 3376 (different following apostrophes)
		list.add("*** END OF THIS PROJECT GUTENBERG EBOOK"); // 19778-8, line 3737
		list.add("End of the Project Gutenberg EBook of"); // 19033-8, line 1341
		return list;
	}

	private List<String> getBoundaryLineCandidatesForStart() {
		List<String> list = new LinkedList<String>();
		list.add("*** START OF THIS PROJECT GUTENBERG EBOOK"); // 19778-8, line 25
		list.add("Team at http://www.pgdp.net"); // 19778-8, line 32
		list.add("Distributed Proofreading Team at http://www.pgdp.net"); // 19033-8, line 27
		return list;
	}

	/**
	 * If top and/or bottom boundaries are found, the respective fields in the text
	 * file are set.
	 * 
	 * Returns if top boundary and bottom boundary were found.
	 */
	public boolean setBoundaries(Text textFile) {

		// Begin with bottom of file. Approach of beginning with top of file produced
		// wrong results at work with real Gutenberg data.
		int endLineIndex = getBoundaryIndexOfEnd(textFile.getLines(), 0);

		// If end line index found, cut file before searching start line.
		int startLineIndex;
		if (endLineIndex != -1) {
			startLineIndex = getBoundaryIndexOfStart(textFile.getLines(), endLineIndex);
		} else {
			startLineIndex = getBoundaryIndexOfStart(textFile.getLines(), textFile.getLines().size() - 1);
		}

		// Set indexes in text file
		boolean bothFound = true;
		if (startLineIndex < 0) {
			bothFound = false;
		} else {
			textFile.setContentStartIndex(startLineIndex);
		}
		if (endLineIndex < 0) {
			bothFound = false;
		} else {
			textFile.setContentEndIndex(endLineIndex);
		}

		return bothFound;
	}
}
