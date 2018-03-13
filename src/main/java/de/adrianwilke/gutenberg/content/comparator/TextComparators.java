package de.adrianwilke.gutenberg.content.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Configuration of text comparators.
 * 
 * @author Adrian Wilke
 */
public class TextComparators {

	protected Map<TextComparator, Integer> textComparators = new HashMap<TextComparator, Integer>();

	public TextComparators addCorpusComparator(int weight, double pairBonus) {
		CorpusComparator corpusComparator = new CorpusComparator();
		corpusComparator.setPairBonus(pairBonus);
		textComparators.put(corpusComparator, weight);
		return this;
	}

	/**
	 * @param lengthRatio
	 *            Sets length ratio of including text. If A includes textA and B
	 *            includes textB, A length is 100 and B length is 200, the resulting
	 *            ratio is A/B=0.5. The comparator will compare textA.length and
	 *            textB.length * lengthRatio.
	 */
	public TextComparators addLengthComparator(int weight, double lengthRatio) {
		LengthComparator lengthComparator = new LengthComparator();
		lengthComparator.setLengthRatio(lengthRatio);
		textComparators.put(lengthComparator, weight);
		return this;
	}

	public TextComparators addPunctuationComparator(int weight) {
		textComparators.put(new PunctuationComparator(), weight);
		return this;
	}

	public double compareAll(Text textA, Text textB) {
		if (textComparators.isEmpty()) {
			System.err.println("Warning: No comparator used.");
		}

		double score = 0;
		for (Entry<TextComparator, Integer> textComparator : textComparators.entrySet()) {
			score += textComparator.getValue() * textComparator.getKey().compare(textA, textB);
		}

		int weights = 0;
		for (Integer iterable_element : textComparators.values()) {
			weights += iterable_element;
		}
		return score / weights;
	}

	public double compareAll(Text textA1, Text textA2, Text textB) {
		if (textComparators.isEmpty()) {
			System.err.println("Warning: No comparator used.");
		}

		double score = 0;
		for (Entry<TextComparator, Integer> textComparator : textComparators.entrySet()) {
			score += textComparator.getValue() * textComparator.getKey().compare(textA1, textA2, textB);
		}

		int weights = 0;
		for (Integer iterable_element : textComparators.values()) {
			weights += iterable_element;
		}
		return score / weights;
	}

	public double compareAll(Text textA1, Text textA2, Text textB1, Text textB2) {
		if (textComparators.isEmpty()) {
			System.err.println("Warning: No comparator used.");
		}

		double score = 0;
		for (Entry<TextComparator, Integer> textComparator : textComparators.entrySet()) {
			score += textComparator.getValue() * textComparator.getKey().compare(textA1, textA2, textB1, textB2);
		}

		int weights = 0;
		for (Integer iterable_element : textComparators.values()) {
			weights += iterable_element;
		}
		return score / weights;
	}
}