package de.adrianwilke.gutenberg.content.re;

import java.util.LinkedList;
import java.util.List;

/**
 * Searches for non-content strings at top and bottom of text.
 * 
 * @author Adrian Wilke
 */
public class TxtCutter {

	private boolean indexBeginFound = false;
	private boolean indexEndFound = false;

	private int getBoundaryIndexBegin(Txt text, int endLineIndex) {
		int returnLineIndex = -1;

		// Begin at last line, to match last line containing a candidate
		lineloop: for (int i = endLineIndex; i >= 0; i--) {
			for (String candidate : getBoundaryLineCandidatesForStart()) {
				if (text.getLine(i).startsWith(candidate)) {
					// Do not include search string
					returnLineIndex = i;
					indexBeginFound = true;
					break lineloop;
				}
			}
		}

		// Do not include empty lines
		for (int i = returnLineIndex + 1; i < text.getLineIndexes().last(); i++) {
			if (!text.getLine(i).isEmpty()) {
				returnLineIndex = i;
				break;
			}
		}

		return returnLineIndex;
	}

	private int getBoundaryIndexEnd(Txt text, int startLineIndex) {
		int returnLineIndex = -1;

		// Begin at first line, to match first line containing a candidate
		lineloop: for (int i = startLineIndex; i < text.getLineIndexes().last(); i++) {
			String line = text.getLine(i);

			for (String candidate : getBoundaryLineCandidatesForEnd()) {
				if (line.startsWith(candidate)) {
					// Do not include search string
					returnLineIndex = i;
					indexEndFound = true;
					break lineloop;
				}
			}
		}

		// Do not include empty lines
		for (int i = returnLineIndex - 1; i >= 0; i--) {
			String line = text.getLine(i);
			if (!line.isEmpty()) {
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
	 * Returns if top boundary was found.
	 */
	public boolean isIndexBeginFound() {
		return indexBeginFound;
	}

	/**
	 * Returns if bottom boundary was found.
	 */
	public boolean isIndexEndFound() {
		return indexEndFound;
	}

	/**
	 * Returns new text with respective top and bottom indexes.
	 */
	public TxtPart cut(Txt text) {

		// Begin with bottom of file. Approach of beginning with top of file produced
		// wrong results at work with real data.
		int endLineIndex = getBoundaryIndexEnd(text, 0);

		// If end line index found, cut file before searching start line.
		int startLineIndex;
		if (endLineIndex != -1) {
			startLineIndex = getBoundaryIndexBegin(text, endLineIndex);
		} else {
			startLineIndex = getBoundaryIndexBegin(text, text.getLineIndexes().last());
		}

		String name;
		if (isIndexBeginFound() && isIndexEndFound()) {
			name = "cut";
		} else if (!isIndexBeginFound() && !isIndexEndFound()) {
			name = "uncut";
		} else if (!isIndexBeginFound()) {
			name = "begin-cut";
		} else {
			name = "end-cut";
		}

		return new TxtPart(text, name, startLineIndex, endLineIndex);
	}
}
