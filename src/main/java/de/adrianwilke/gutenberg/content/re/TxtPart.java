package de.adrianwilke.gutenberg.content.re;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a text part.
 * 
 * @author Adrian Wilke
 */
public class TxtPart extends Txt {

	protected SortedSet<Integer> lineIndexes;

	/**
	 * Creates new text, which uses contents of its parent.
	 * 
	 * @param parent
	 *            parent text
	 * @param indexBegin
	 *            inclusive index
	 * @param indexEnd
	 *            inclusive index
	 */
	TxtPart(Txt parent, int indexBegin, int indexEnd) {
		super(parent);

		lineIndexes = new TreeSet<Integer>();
		SortedSet<Integer> lineIndexesParent = parent.getLineIndexes();
		for (int i = indexBegin; i <= indexEnd; i++) {
			if (lineIndexesParent.contains(i)) {
				lineIndexes.add(i);
			}
		}
	}

	/**
	 * Gets start index of text range. The range is not necessarily continuous.
	 */
	@Override
	public int getIndexRangeBegin() {
		return lineIndexes.first();
	}

	/**
	 * Gets end index of text range. The range is not necessarily continuous.
	 */
	@Override
	public int getIndexRangeEnd() {
		return lineIndexes.last();
	}

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
	 * Returns true
	 */
	@Override
	protected boolean hasParent() {
		return true;
	}
}
