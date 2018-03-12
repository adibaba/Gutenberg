package de.adrianwilke.gutenberg.content.comparator;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Sub-classes compare texts.
 * 
 * Returns value between 0 (not equal) and 1 (equal).
 * 
 * Order of texts is irrelevant.
 * 
 * @author Adrian Wilke
 */
public abstract class TextComparator {

	public abstract double compare(Text textA, Text textB);

	public abstract double compare(Text textA1, Text textA2, Text textB);

	public abstract double compare(Text textA1, Text textA2, Text textB1, Text textB2);

}