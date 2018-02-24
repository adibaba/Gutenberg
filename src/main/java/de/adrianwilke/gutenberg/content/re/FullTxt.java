package de.adrianwilke.gutenberg.content.re;

import java.io.File;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.adrianwilke.gutenberg.io.TextFileAccessor;

/**
 * Represents a full text.
 * 
 * @author Adrian Wilke
 */
public class FullTxt extends Txt {

	final protected String charsetName;
	final protected String filePath;

	protected SortedSet<Integer> lineIndexes;
	protected String[] lines;

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
	 * Returns false
	 */
	@Override
	protected boolean hasParent() {
		return false;
	}
}