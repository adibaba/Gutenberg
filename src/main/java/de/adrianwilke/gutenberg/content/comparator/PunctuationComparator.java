package de.adrianwilke.gutenberg.content.comparator;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Compares the number of punctuation marks.
 * 
 * @author Adrian Wilke
 */
public class PunctuationComparator extends TextComparator {

	protected double compare(int a, int b) {
		if (a == 0 || b == 0) {
			return 0;
		} else if (a < b) {
			return 1d * a / b;
		} else {
			return 1d * b / a;
		}
	}

	@Override
	public double compare(Text textA, Text textB) {
		return compare(textA.getNumberOfPunctuationMarks(), textB.getNumberOfPunctuationMarks());
	}

	@Override
	public double compare(Text textA1, Text textA2, Text textB) {
		return compare(textA1.getNumberOfPunctuationMarks() + textA2.getNumberOfPunctuationMarks(),
				textB.getNumberOfPunctuationMarks());
	}

	@Override
	public double compare(Text textA1, Text textA2, Text textB1, Text textB2) {
		return compare(textA1.getNumberOfPunctuationMarks() + textA2.getNumberOfPunctuationMarks(),
				textB1.getNumberOfPunctuationMarks() + textB2.getNumberOfPunctuationMarks());
	}
}