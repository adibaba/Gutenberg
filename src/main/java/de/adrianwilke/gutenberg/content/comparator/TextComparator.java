package de.adrianwilke.gutenberg.content.comparator;

import java.util.LinkedList;
import java.util.List;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Sub-classes compare texts.
 * 
 * @author Adrian Wilke
 */
public abstract class TextComparator {

	private static List<TextComparator> textComparators = new LinkedList<TextComparator>();

	static {
		addTextComparator(new PunctuationComparator());
		addTextComparator(new CorpusComparator());
	}

	public static void addTextComparator(TextComparator textComparator) {
		textComparators.add(textComparator);
	}

	public abstract double compare(Text textA, Text textB);

	public abstract double compare(Text textA1, Text textA2, Text textB);

	public abstract double compare(Text textA1, Text textA2, Text textB1, Text textB2);
}
