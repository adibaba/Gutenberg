package de.adrianwilke.gutenberg.comparators;

public class ExactComparator extends TitleComparator {

	@Override
	public boolean compare(String title1, String title2) {

		title1 = keepAsciiLettersAndNumbers(title1);
		title2 = keepAsciiLettersAndNumbers(title2);

		if (title1.length() < 3 || title2.length() < 3) {
			return false;
		}

		if (title1.equals(title2)) {
			return true;
		} else {
			return false;
		}
	}
}