package de.adrianwilke.gutenberg.comparators;

/**
 * Removes everything, starting at last dot in title.
 * 
 * @author Adrian Wilke
 */
public class LastPointComparator extends TitleComparator {

	@Override
	public boolean compare(String title1, String title2) {

		int lastIndex = title1.lastIndexOf(".");
		if (lastIndex != -1) {
			title1 = title1.substring(0, lastIndex - 1);
		}

		lastIndex = title2.lastIndexOf(".");
		if (lastIndex != -1) {
			title2 = title2.substring(0, lastIndex - 1);
		}

		title1 = keepAsciiLettersAndNumbers(title1);
		title2 = keepAsciiLettersAndNumbers(title2);

		if (title1.length() < 3 || title2.length() < 3) {
			return false;
		}

		if (title1.equals(title2)) {
			setMatchingString(title1);
			return true;
		} else {
			return false;
		}
	}
}