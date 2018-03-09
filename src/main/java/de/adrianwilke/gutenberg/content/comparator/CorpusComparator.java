package de.adrianwilke.gutenberg.content.comparator;

import java.util.HashSet;
import java.util.Set;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Compares the number of common words.
 * 
 * @author Adrian Wilke
 */
public class CorpusComparator extends TextComparator {

	protected double compare(Set<String> a, Set<String> b) {

		if (a.isEmpty() || b.isEmpty()) {
			return 0;

		} else {
			Set<String> baseA = new HashSet<String>(a);
			baseA.removeAll(b);
			Set<String> baseB = new HashSet<String>(b);
			baseB.removeAll(a);

			if (baseA.size() > baseB.size()) {
				return 1d * (a.size() - baseA.size()) / a.size();
			} else {
				return 1d * (b.size() - baseB.size()) / b.size();
			}
		}
	}

	@Override
	public double compare(Text textA, Text textB) {
		return compare(textA.getCorpus(), textB.getCorpus());
	}

	@Override
	public double compare(Text textA1, Text textA2, Text textB) {
		Set<String> a = textA1.getCorpus();
		a.addAll(textA2.getCorpus());

		return compare(a, textB.getCorpus());
	}

	@Override
	public double compare(Text textA1, Text textA2, Text textB1, Text textB2) {
		Set<String> a = textA1.getCorpus();
		a.addAll(textA2.getCorpus());

		Set<String> b = textB1.getCorpus();
		b.addAll(textB2.getCorpus());

		return compare(a, b);
	}
}