package de.adrianwilke.gutenberg.content.comparator;

import java.util.Collection;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Compares string lengths.
 * 
 * @author Adrian Wilke
 */
public class LengthComparator extends TextComparator {

	public static int getLength(Collection<Text> texts) {
		int length = 0;
		for (Text text : texts) {
			length += getLength(text);
		}
		return length;
	}

	protected static int getLength(Text text) {
		int length = 0;
		for (Integer lineIndex : text.getLineIndexes()) {
			length += text.getLine(lineIndex).length();
		}
		return length;
	}

	protected double lengthRatio = 1;

	protected double compare(int a, int b) {

		if (a == 0 && b == 0) {
			return 1;

		} else if (a == 0 || b == 0) {
			return 0;

		} else {

			double relativeB = b * lengthRatio;
			if (a < relativeB) {
				return 1d * a / relativeB;
			} else {
				return 1d * relativeB / a;
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

	/**
	 * Sets length ratio of including text. If A includes textA and B includes
	 * textB, A length is 100 and B length is 200, the resulting ratio is A/B=0.5.
	 * The comparator will compare textA.length and textB.length * lengthRatio.
	 */
	public void setLengthRatio(double lengthRatio) {
		this.lengthRatio = lengthRatio;
	}
}