package de.adrianwilke.gutenberg.comparators;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class TitleComparator {

	private static List<TitleComparator> titleComparators = new LinkedList<TitleComparator>();

	static {
		addTitleComparator(new LastPointComparator());
		addTitleComparator(new ShortenerComparator());
		addTitleComparator(new ExactComparator());
	}

	public static void addTitleComparator(TitleComparator titleComparator) {
		titleComparators.add(titleComparator);
	}

	/**
	 * @return Matching class names and titles of TitleComparators
	 */
	public static Map<String, String> compareAll(String title1, String title2) {
		if (titleComparators.isEmpty()) {
			System.err.println("Warning: No comparator used.");
		}
		Map<String, String> matches = new HashMap<String, String>();
		for (TitleComparator matchingComparator : titleComparators) {
			if (matchingComparator.compare(title1, title2)) {
				matches.put(matchingComparator.getClassName(), matchingComparator.getMatchingString());
			}
		}
		return matches;
	}

	public static Collection<TitleComparator> getTitleComparators() {
		return titleComparators;
	}

	public static void reset() {
		titleComparators = new LinkedList<TitleComparator>();
	}

	private String matchingString;

	/**
	 * Has to call {@link TitleComparator}{@link #setMatchingString(String)}
	 */
	public abstract boolean compare(String title1, String title2);

	public String getClassName() {
		return getClass().getName();
	}

	public String getMatchingString() {
		return matchingString;
	}

	public String getSimpleClassName() {
		return getClass().getSimpleName();
	}

	protected String keepAsciiLettersAndNumbers(String string) {
		return string.replaceAll("[^A-Za-z0-9]", "");
	}

	public void setMatchingString(String matchingTitle) {
		this.matchingString = matchingTitle;
	}
}