package de.adrianwilke.gutenberg.content;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Start and end numbers of text part.
 * 
 * Does not contain text.
 * 
 * @author Adrian Wilke
 */
public class Part {

	private static List<Integer> getSortedDistances(List<Part> textParts) {
		List<Integer> distances = new LinkedList<Integer>();
		int previousEndIndex = -1;
		for (int i = 0; i < textParts.size(); i++) {
			Part textPart = textParts.get(i);
			if (previousEndIndex != -1) {
				int numberOfEmptyLines = textPart.getStartIndex() - previousEndIndex - 1;
				if (!distances.contains(numberOfEmptyLines)) {
					distances.add(new Integer(numberOfEmptyLines));
				}
			}
			previousEndIndex = textPart.getEndIndex();
		}
		distances.sort(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		return distances;
	}

	/**
	 * Gets sets of non-empty lines. Lines are trimmed for comparison.
	 */
	public static List<Part> linesToTextParts(List<String> lines) {
		List<Part> textParts = new LinkedList<Part>();
		boolean isTextPart = false;
		Part textPart = null;
		for (int l = 0; l < lines.size(); l++) {
			String line = lines.get(l).trim();
			if (line.isEmpty()) {
				if (isTextPart) {
					textPart.setEndIndex(l - 1);
					textParts.add(textPart);
					isTextPart = false;
				}
			} else {
				if (!isTextPart) {
					textPart = new Part(l);
					isTextPart = true;
				}
			}
		}
		if (isTextPart) {
			textPart.setEndIndex(lines.size() - 1);
			textParts.add(textPart);
			isTextPart = false;
		}
		return textParts;
	}

	public static Map<Integer, List<Part>> textPartsToSections(List<Part> textParts) {

		// Smallest distance already available. Is re-added afterwards.
		List<Integer> distances = getSortedDistances(textParts);
		distances.remove(0);

		// Initialize return structures
		Map<Integer, List<Part>> sections = new HashMap<Integer, List<Part>>();
		for (Integer distance : distances) {
			sections.put(distance, new LinkedList<Part>());
		}

		// Helper
		Map<Integer, Integer> distanceToLoopIndexMap = new HashMap<Integer, Integer>();
		int contentStartIndex = textParts.get(0).getStartIndex();
		int previousEndIndex = -1;

		// Go through all parts
		for (int i = 0; i < textParts.size(); i++) {
			Part textPart = textParts.get(i);

			// Skip first iteration, there is no data about previous parts
			if (i > 0) {
				int numberOfEmptyLines = textPart.getStartIndex() - previousEndIndex - 1;

				// Update current and smaller distances
				for (Integer distance : distances) {
					if (distance <= numberOfEmptyLines) {
						int startIndex = distanceToLoopIndexMap.containsKey(distance)
								? textParts.get(distanceToLoopIndexMap.get(distance)).getStartIndex()
								: contentStartIndex;
						int endIndex = previousEndIndex;
						sections.get(distance).add(new Part(startIndex, endIndex));
						distanceToLoopIndexMap.put(distance, i);
					}
				}

			}
			previousEndIndex = textPart.getEndIndex();
		}
		for (Integer distance : distances) {
			int startIndex = textParts.get(distanceToLoopIndexMap.get(distance)).getStartIndex();
			int endIndex = textParts.get(textParts.size() - 1).getEndIndex();
			sections.get(distance).add(new Part(startIndex, endIndex));
		}

		// Re-add smallest distance
		sections.put(1, textParts);

		return sections;
	}

	private int endIndex;

	private int startIndex;

	public Part(int startIndex) {
		this.startIndex = startIndex;
	}

	public Part(int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getEndLineNumber() {
		return endIndex + 1;
	}

	public int getNumberOfLines() {
		return getEndIndex() - getStartIndex() + 1;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getStartLineNumber() {
		return startIndex + 1;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	@Override
	public String toString() {
		return "[" + getStartLineNumber() + "," + getEndLineNumber() + "]";
	}
}