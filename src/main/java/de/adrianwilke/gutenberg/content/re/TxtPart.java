package de.adrianwilke.gutenberg.content.re;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a text-part.
 * 
 * @author Adrian Wilke
 */
public class TxtPart extends Txt {

	/**
	 * Creates new text, which uses contents of its parent.
	 */
	TxtPart(Txt parent, String name, int indexBegin, int indexEnd) {
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

	@Override
	public String getLineToLowerCase(int index) {
		return getRoot().getLineToLowerCase(index);
	}

	/**
	 * Returns true
	 */
	@Override
	protected boolean hasParent() {
		return true;
	}
}
