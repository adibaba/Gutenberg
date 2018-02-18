package de.adrianwilke.gutenberg.comparators;

import de.adrianwilke.gutenberg.utils.RegEx;

/**
 * Removes everything, starting at last dot in title.
 * 
 * @author Adrian Wilke
 */
public class LastPointComparator extends TitleComparator {

	public static boolean EXECUTE = true;

	@Override
	public boolean compare(String title1, String title2) {

		int lastIndex = title1.lastIndexOf(".");
		if (lastIndex != -1) {
			title1 = title1.substring(0, lastIndex);
		}

		lastIndex = title2.lastIndexOf(".");
		if (lastIndex != -1) {
			title2 = title2.substring(0, lastIndex);
		}

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

		if (title1.equals(title2)) {
			setMatchingString(title1);
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		if (EXECUTE == false) {
			System.out.println(new LastPointComparator().compare("Aaa", "aaa.eng") ? "+" : "-");
			System.out.println(new LastPointComparator().compare("A-aa.ger", "aaa") ? "+" : "-");
			System.out.println(new LastPointComparator().compare("AaA.de", "a_aa.en") ? "+" : "-");
			System.out.println(new LastPointComparator().compare("Äaa.tar.gz", "aäa.tar.bla") ? "+" : "-");
			System.out.println(new LastPointComparator().compare("ßxx.y", "ssxx") ? "+" : "-");
			System.out.println(new LastPointComparator().compare("ßxx.y", "sxx") ? "+" : "-");
		}
	}
}