package de.adrianwilke.gutenberg.content;

import java.io.File;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.adrianwilke.gutenberg.io.TextFileAccessor;
import de.adrianwilke.gutenberg.utils.RegEx;

/**
 * Represents a full text.
 * 
 * @author Adrian Wilke
 */
public class FullTxt extends Text {
	static public final int DEFAULT_LENGTH_SIZE = 4;

	final protected String charsetName;
	final protected String filePath;

	protected String[] lines;
	protected String[] linesSimplified;

	/**
	 * Creates new text from file source.
	 * 
	 * @see java.nio.charset.StandardCharsets
	 */
	public FullTxt(String filePath, String charsetName) {
		super(null);

		this.filePath = filePath;
		this.charsetName = charsetName;

		String name = new File(filePath).getName();
		if (name.contains(".")) {
			setName(name.substring(0, name.indexOf(".")), false);
		} else {
			setName(name, false);
		}
	}

	/**
	 * Gets line with related index.
	 */
	public String getLine(int index) {
		return getLines()[index];
	}

	/**
	 * Gets indexes of text.
	 */
	@Override
	public SortedSet<Integer> getLineIndexes() {
		if (lineIndexes == null) {
			SortedSet<Integer> indexes = new TreeSet<Integer>();
			for (int i = 0; i < getLines().length; i++) {
				indexes.add(i);
			}
			lineIndexes = indexes;
		}
		return lineIndexes;
	}

	/**
	 * Gets raw lines.
	 */
	protected String[] getLines() {
		if (lines == null) {
			List<String> linesList = TextFileAccessor.readFileToString(filePath, charsetName);
			lines = new String[linesList.size()];
			for (int i = 0; i < linesList.size(); i++) {
				lines[i] = linesList.get(i);
			}
		}
		return lines;
	}

	/**
	 * Gets simplified line with related index. Lines are in lower case and only
	 * consist of letters, numbers and spaces.
	 */
	@Override
	public String getLineSimplified(int index) {
		if (linesSimplified == null) {
			linesSimplified = new String[getLineIndexes().size()];
		}
		if (linesSimplified[index] == null) {
			linesSimplified[index] = new RegEx(getLine(index).toLowerCase()).replaceUmlauts().getAscii()
					.replaceAllExeptAlphaNumberBySpace().toString();
		}
		return linesSimplified[index];
	}

	/**
	 * Returns false
	 */
	@Override
	protected boolean hasParent() {
		return false;
	}

	/**
	 * Returns string representation of text
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(super.toString());
		stringBuilder.append(" ");

		File file = new File(filePath);
		if (file.exists()) {
			long fileLenth = file.length();
			long size = fileLenth;
			if (fileLenth >= 1024) {
				size = fileLenth / 1024;
			}
			for (int i = 0; i < DEFAULT_LENGTH_SIZE - String.valueOf(size).length(); i++) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(size);
			if (fileLenth >= 1024) {
				stringBuilder.append(" KiB");
			} else {
				stringBuilder.append(" bytes");
			}

		}
		return stringBuilder.append(" ").toString();
	}
}