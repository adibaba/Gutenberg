package de.adrianwilke.gutenberg.content.comparator;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Compares string lengths.
 * 
 * @author Adrian Wilke
 */
public class LengthComparatot extends TextComparator {

	protected double compare(int a, int b) {

		if (a == 0 && b == 0) {
			return 1;

		} else if (a == 0 || b == 0) {
			return 0;

		} else {

			if (a < b) {
				return 1d * a / b;
			} else {
				return 1d * b / a;
			}
		}
	}

	@Override
	public double compare(Text textA, Text textB) {
		return compare(getLength(textA), getLength(textB));
	}

	@Override
	public double compare(Text textA1, Text textA2, Text textB) {
		return compare(getLength(textA1) + getLength(textA2), getLength(textB));
	}

	@Override
	public double compare(Text textA1, Text textA2, Text textB1, Text textB2) {
		return compare(getLength(textA1) + getLength(textA2), getLength(textB1) + getLength(textB2));
	}

	public int getLength(Text text) {
		int length = 0;
		for (Integer lineIndex : text.getLineIndexes()) {
			length += text.getLine(lineIndex).length();
		}
		return length;
	}
}