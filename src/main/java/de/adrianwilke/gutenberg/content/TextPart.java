package de.adrianwilke.gutenberg.content;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a text-part.
 * 
 * @author Adrian Wilke
 */
public class TextPart extends Text {

	/**
	 * Creates new text, which uses contents of its parent.
	 */
	TextPart(Text parent, String name, int indexBegin, int indexEnd) {
		super(parent);

		setName(name, true);

		lineIndexes = new TreeSet<Integer>();
		SortedSet<Integer> lineIndexesParent = parent.getLineIndexes();
		for (int i = indexBegin; i <= indexEnd; i++) {
			if (lineIndexesParent.contains(i)) {
				lineIndexes.add(i);
			}
		}
	}

	/**
	 * Gets line with related index.
	 */
	@Override
	public String getLine(int index) {
		return getRoot().getLine(index);
	}

	/**
	 * Gets indexes of text.
	 */
	@Override
	public SortedSet<Integer> getLineIndexes() {
		return lineIndexes;
	}

	/**
	 * Gets simplified line with related index. Lines are in lower case and only
	 * consist of letters, numbers and spaces.
	 */
	@Override
	public String getLineSimplified(int index) {
		return getRoot().getLineSimplified(index);
	}

	/**
	 * Gets number of punctuation marks in line.
	 */
	@Override
	public int getNumberOfPunctuationMarks(int lineIndex) {
		return getRoot().getNumberOfPunctuationMarks(lineIndex);
	}

	/**
	 * Returns true
	 */
	@Override
	protected boolean hasParent() {
		return true;
	}
}
