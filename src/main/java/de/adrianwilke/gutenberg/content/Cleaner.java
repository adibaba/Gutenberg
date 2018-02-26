package de.adrianwilke.gutenberg.content;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Removes indexes.
 * 
 * @author Adrian Wilke
 */
public class Cleaner {

	/**
	 * Returns cleaned text with removed indexes. Empty lines above (or below)
	 * removed lines are also removed.
	 * 
	 * @return
	 */
	public TextPart clean(Text text) {

		List<String> badLines = new LinkedList<String>();
		badLines.add("[Illustration]");
		badLines.add("       *       *       *       *       *");

		List<Integer> removeList = new LinkedList<Integer>();
		Iterator<Integer> it = text.getLineIndexes().iterator();
		while (it.hasNext()) {
			Integer lineIndex = it.next();
			if (badLines.contains(text.getLine(lineIndex))) {
				removeList.add(lineIndex);
			}
		}

		TextPart cleanText = new TextPart(text, "clean", text.getLineIndexes().first(), text.getLineIndexes().last());
		for (int i = removeList.size() - 1; i >= 0; i--) {
			int indexToRemove = removeList.get(i);

			int emptyLinesAbove = 0;
			for (int j = indexToRemove - 1; j >= cleanText.getIndexFirst(); j--) {
				if (cleanText.getLine(j).isEmpty()) {
					emptyLinesAbove++;
				} else {
					break;
				}
			}

			int emptyLinesBelow = 0;
			for (int j = indexToRemove + 1; j <= cleanText.getIndexLast(); j++) {
				if (cleanText.getLine(j).isEmpty()) {
					emptyLinesBelow++;
				} else {
					break;
				}
			}

			// Smaller distances comprise larger distances.
			// To be conservative, remove larger distances.
			if (emptyLinesAbove > emptyLinesBelow) {
				for (int j = indexToRemove; j >= indexToRemove - emptyLinesAbove; j--) {
					cleanText.remove(j);
				}
			} else {
				for (int j = indexToRemove; j <= indexToRemove + emptyLinesBelow; j++) {
					cleanText.remove(j);
				}
			}

			cleanText.remove(removeList.get(i));
		}

		return cleanText;
	}
}