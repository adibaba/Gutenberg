package de.adrianwilke.gutenberg.comparators;

import de.adrianwilke.gutenberg.utils.RegEx;

/**
 * Shortens longer title to size of shorter title.
 * 
 * @author Adrian Wilke
 */
public class ShortenerComparator extends TitleComparator {

	public static boolean EXECUTE = true;

	@Override
	public boolean compare(String title1, String title2) {

		title1 = title1.toLowerCase();
		title2 = title2.toLowerCase();

		String title1a = new RegEx(title1).removeAllExeptAlphaNumber().toString();
		String title2a = new RegEx(title2).removeAllExeptAlphaNumber().toString();

		String title1b = new RegEx(title1.replace("ß", "s")).getAscii().removeAllExeptAlphaNumber().toString();
		String title2b = new RegEx(title2.replace("ß", "s")).getAscii().removeAllExeptAlphaNumber().toString();

		if (subCompare(title1a, title2a) || subCompare(title1b, title2b)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean subCompare(String title1, String title2) {

		if (title1.length() < 3 || title2.length() < 3) {
			return false;
		}

		if (title1.length() > title2.length()) {
			title1 = title1.substring(0, title2.length());
		} else {
			title2 = title2.substring(0, title1.length());
		}

		if (title1.equals(title2)) {
			setMatchingString(title1);
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		if (EXECUTE == false) {
			System.out.println(new ShortenerComparator().compare("Aaaaaaaaa", "aaa") ? "+" : "-");
			System.out.println(new ShortenerComparator().compare("A-aa", "aaaaaaaaaa") ? "+" : "-");
			System.out.println(new ShortenerComparator().compare("AaAaaaaaaa", "a_aa") ? "+" : "-");
			System.out.println(new ShortenerComparator().compare("Äaa", "aäaaaaaa") ? "+" : "-");
			System.out.println(new ShortenerComparator().compare("ßxx", "ssxx") ? "+" : "-");
			System.out.println(new ShortenerComparator().compare("ßxx", "sxxxxxxxxxx") ? "+" : "-");
		}
	}
}