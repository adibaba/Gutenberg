package de.adrianwilke.gutenberg.content.re;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import de.adrianwilke.gutenberg.exceptions.TextRootRuntimeException;
import de.adrianwilke.gutenberg.io.Resources;

/**
 * Represents an abstract text.
 * 
 * @author Adrian Wilke
 */
public abstract class Txt implements Comparable<Txt> {

	static public final int DEFAULT_LINE_NUMBER_DIGITS = 4;
	static public boolean EXECUTE = true;

	public static void main(String[] args) {
		if (EXECUTE == true) {
			Txt text = new FullTxt(Resources.getResource("text/lorem-ipsum.txt").getPath(), "UTF-8");

			System.out.println(text);
			System.out.println(text.getLinesString(true));

			SortedMap<Integer, SortedSet<Txt>> textDistanceParts = text.getParts();

			System.out.println("DISTANCES: " + textDistanceParts.keySet());

			for (Entry<Integer, SortedSet<Txt>> distancesEntry : textDistanceParts.entrySet()) {
				System.out.println("DISTANCE: " + distancesEntry.getKey());
				SortedSet<Txt> distanceSet = distancesEntry.getValue();
				for (Txt distPart : distanceSet) {
					System.out.println("PART: " + distPart);
					System.out.println(distPart.getLinesString(true));
				}
			}
		}
	}

	public SortedMap<Integer, SortedSet<Txt>> distanceParts; // TODO protected
	protected String name;
	protected final Txt parent;

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
	 * Puts all contained lines to the string builder.
	 */
	public StringBuilder addLines(StringBuilder stringBuilder, boolean putLinePrefix) {
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
	 * {@link Comparable} implemented as comparison of first line indexes.
	 */
	public int compareTo(Txt t) {
		return Integer.compare(getLineIndexes().first(), t.getLineIndexes().first());
	};

	/**
	 * Gets line with related index.
	 */
	public abstract String getLine(int index);

	/**
	 * Gets indexes of text.
	 */
	public abstract SortedSet<Integer> getLineIndexes();

	/**
	 * Returns a string containing all lines with line number prefixes.
	 */
	public String getLinesString(boolean addLineNumberPrefix) {
		return addLines(new StringBuilder(), addLineNumberPrefix).toString();
	}

	/**
	 * Gets name of text
	 */
	public String getName() {
		return name;
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
	 * Returns text-parts divides by empty lines.
	 * 
	 * TODO
	 * 
	 * @return
	 */
	public SortedMap<Integer, SortedSet<Txt>> getParts() {
		if (distanceParts == null) {
			distanceParts = new TreeMap<Integer, SortedSet<Txt>>();

			// First iteration: Get distances (a.k.a. sequences of empty lines), which occur
			// in text
			SortedSet<Integer> distances = new TreeSet<Integer>();
			int emptyLinesCounter = 0;

			// Iterate over all relevant lines/indexes
			for (int i = getLineIndexes().first(); i <= getLineIndexes().last(); i++) {
				if (getLine(i).trim().isEmpty()) {
					// Current line is empty -> Check if there are more
					emptyLinesCounter++;

				} else {
					// Current line contains text -> Add distance before text
					if (emptyLinesCounter > 0) {
						distances.add(emptyLinesCounter);
					}
					emptyLinesCounter = 0;
				}
			}

			// Second iteration: Split text dependent of number of empty lines (a.k.a.
			// distances)
			Map<Integer, Integer> distancesToStartIndex = new HashMap<Integer, Integer>();
			for (Integer distance : distances) {
				distanceParts.put(distance, new TreeSet<Txt>());
				distancesToStartIndex.put(distance, -1);
			}
			emptyLinesCounter = 0;
			int lastTextLineIndex = -1;

			// Iterate over all relevant lines/indexes
			for (int lineIndex = getLineIndexes().first(); lineIndex <= getLineIndexes().last(); lineIndex++) {

				if (getLine(lineIndex).trim().isEmpty()) {
					
					// Current line is empty
					emptyLinesCounter++;

					// For all real distances
					for (int distance = 1; distance <= emptyLinesCounter; distance++) {
						if (!distances.contains(distance)) {
							continue;
						}

						if (!distancesToStartIndex.get(distance).equals(-1)) {

							String name = "distance" + distance + "-index" + distanceParts.get(distance).size();
							distanceParts.get(distance).add(
									(new TxtPart(this, name, distancesToStartIndex.get(distance), lastTextLineIndex)));
							distancesToStartIndex.put(distance, -1);
						}
					}

				} else {
					// First line after empty lines -> Remember the line index

					for (Integer distance : distances) {
						if (distancesToStartIndex.get(distance).equals(-1)) {
							distancesToStartIndex.put(distance, lineIndex);
						}
					}
					lastTextLineIndex = lineIndex;
					emptyLinesCounter = 0;
				}

			}

			// Add end parts
			for (Integer distance : distances) {
				if (!distancesToStartIndex.get(distance).equals(-1)) {
					distanceParts.get(distance)
							.add((new TxtPart(this, name, distancesToStartIndex.get(distance), lastTextLineIndex)));
				}
			}
		}
		return distanceParts;
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
	 * Sets name of text
	 */
	public void setName(String name, boolean parentAsPrefix) {
		if (parentAsPrefix && hasParent()) {
			this.name = getParent().getName() + "/" + name;
		} else {
			this.name = name;
		}
	}

	/**
	 * Returns string representation of text
	 */
	@Override
	public String toString() {
		return getName();
	}
}