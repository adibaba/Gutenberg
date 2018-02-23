package de.adrianwilke.gutenberg.content.re;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import de.adrianwilke.gutenberg.exceptions.TextRootRuntimeException;
import de.adrianwilke.gutenberg.io.Resources;

/**
 * Represents an abstract text.
 * 
 * @author Adrian Wilke
 */
public abstract class Txt {

	private static final int DEFAULT_LINE_NUMBER_DIGITS = 4;
	public static boolean EXECUTE = true;

	public static void main(String[] args) {
		if (EXECUTE == false) {
			Txt txt = new FullTxt(Resources.getResource("text/lorem-ipsum.txt").getPath(), "UTF-8");
			System.out.println(txt.toStringWithLineNumberPrefix());

			for (Txt part : txt.getParts()) {
				System.out.println(part.toStringWithLineNumberPrefix());
			}
		}
	}

	private final Txt parent;
	protected List<Txt> parts;

	/**
	 * Creates new text.
	 * 
	 * @param parent
	 *            null, if is root
	 */
	protected Txt(Txt parent) {
		this.parent = parent;
	}

	/**
	 * Puts all contained lines to the string builder. TODO: get root and put lines
	 */
	public StringBuilder addToStringBuilder(StringBuilder stringBuilder, boolean putLinePrefix) {
		for (Integer lineIndex : getLineIndexes()) {
			if (putLinePrefix) {
				stringBuilder.append(lineIndex + 1);
				for (int i = String.valueOf(lineIndex + 1).length(); i < DEFAULT_LINE_NUMBER_DIGITS; i++) {
					stringBuilder.append(" ");
				}
				stringBuilder.append(" ");
			}
			stringBuilder.append(getLine(lineIndex));
			stringBuilder.append(System.lineSeparator());
		}
		return stringBuilder;
	}

	/**
	 * Gets start index of text range. The range is not necessarily continuous.
	 */
	public abstract int getIndexRangeBegin();

	/**
	 * Gets end index of text range. The range is not necessarily continuous.
	 */
	public abstract int getIndexRangeEnd();

	/**
	 * Gets line with related index.
	 */
	public abstract String getLine(int index);

	/**
	 * Gets indexes of text.
	 */
	public abstract SortedSet<Integer> getLineIndexes();

	/**
	 * Gets a string containing the range of line numbers. The range is not
	 * necessarily continuous.
	 */
	public String getLineNumberRange() {
		return "[" + getLineNumberRangeBegin() + "," + getLineNumberRangeEnd() + "]";
	}

	/**
	 * Gets start line number of text range. The range is not necessarily
	 * continuous.
	 */
	public int getLineNumberRangeBegin() {
		return getIndexRangeBegin() + 1;
	}

	/**
	 * Gets end line number of text range. The range is not necessarily continuous.
	 */
	public int getLineNumberRangeEnd() {
		return getIndexRangeEnd() + 1;
	}

	/**
	 * Gets parent element.
	 * 
	 * @throws TextRootRuntimeException
	 *             if is root and has no parent element
	 */
	protected Txt getParent() {
		if (parent == null) {
			throw new TextRootRuntimeException();
		} else {
			return parent;
		}
	}

	/**
	 * Returns text parts divides by empty lines.
	 */
	public List<Txt> getParts() {
		if (parts == null) {
			parts = new LinkedList<Txt>();

			int numberOfEmptyLines = 0;
			int startIndex = -1;
			for (int i = getIndexRangeBegin(); i < getIndexRangeEnd(); i++) {
				if (getLine(i).trim().isEmpty()) {
					numberOfEmptyLines++;
					if (startIndex == -1) {
						// Start index not set -> empty lines at start of text
						continue;
					} else if (numberOfEmptyLines == 1) {
						// First empty line after text -> New part found
						parts.add(new TxtPart(this, startIndex, i - 1));
					}
					startIndex = -1;
				} else {
					if (startIndex == -1) {
						startIndex = i;
					}
					numberOfEmptyLines = 0;
				}
			}
		}
		return parts;
	}

	/**
	 * Gets the text root element.
	 */
	protected Txt getRoot() {
		Txt text = this;
		while (text.hasParent()) {
			text = text.getParent();
		}
		return text;
	}

	/**
	 * Returns, if is not root
	 */
	protected abstract boolean hasParent();

	/**
	 * Returns a string containing all lines.
	 */
	@Override
	public String toString() {
		return addToStringBuilder(new StringBuilder(), false).toString();
	}

	/**
	 * Returns a string containing all lines with line number prefixes.
	 */
	public String toStringWithLineNumberPrefix() {
		return addToStringBuilder(new StringBuilder(), true).toString();
	}
}