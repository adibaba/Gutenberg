package de.adrianwilke.gutenberg.content_analyzer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Start and end numbers of text part.
 * 
 * @author Adrian Wilke
 */
public class TextPart {

	public static List<TextPart> linesToTextParts(List<String> lines) {
		List<TextPart> textParts = new LinkedList<TextPart>();
		boolean isTextPart = false;
		TextPart textPart = null;
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
					textPart = new TextPart(l);
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

	private static List<Integer> getSortedDistances(List<TextPart> textParts) {
		List<Integer> distances = new LinkedList<Integer>();
		int previousEndIndex = -1;
		for (int i = 0; i < textParts.size(); i++) {
			TextPart textPart = textParts.get(i);
			if (previousEndIndex != -1) {
				int numberOfEmptyLines = textPart.getStartIndex() - previousEndIndex - 1;
				if (!distances.contains(numberOfEmptyLines)) {
					distances.add(new Integer(numberOfEmptyLines));
				}
			}
			previousEndIndex = textPart.getEndIndex();
		}
		distances.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		return distances;
	}

	public static Map<Integer, List<TextPart>> textPartsToSections(List<TextPart> textParts) {

		// Smallest distance already available
		List<Integer> distances = getSortedDistances(textParts);
		distances.remove(0);

		// Initialize return structures
		Map<Integer, List<TextPart>> sections = new HashMap<Integer, List<TextPart>>();
		for (Integer distance : distances) {
			sections.put(distance, new LinkedList<TextPart>());
		}

		// Helper
		Map<Integer, Integer> distanceToLoopIndexMap = new HashMap<Integer, Integer>();
		int contentStartIndex = textParts.get(0).getStartIndex();
		int previousEndIndex = -1;

		// Go through all parts
		for (int i = 0; i < textParts.size(); i++) {
			TextPart textPart = textParts.get(i);

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
						sections.get(distance).add(new TextPart(startIndex, endIndex));
						distanceToLoopIndexMap.put(distance, i);
					}
				}

			}
			previousEndIndex = textPart.getEndIndex();
		}
		for (Integer distance : distances) {
			int startIndex = textParts.get(distanceToLoopIndexMap.get(distance)).getStartIndex();
			int endIndex = textParts.get(textParts.size() - 1).getEndIndex();
			sections.get(distance).add(new TextPart(startIndex, endIndex));
		}
		return sections;
	}

	private int endIndex;
	private int startIndex;

	public TextPart(int startIndex) {
		this.startIndex = startIndex;
	}

	public TextPart(int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getEndLineNumber() {
		return endIndex + 1;
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