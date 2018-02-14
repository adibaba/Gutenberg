package de.adrianwilke.gutenberg.comparators;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class TitleComparator {

	private static List<TitleComparator> titleComparators = new LinkedList<TitleComparator>();

	public static void addTitleComparator(TitleComparator titleComparator) {
		titleComparators.add(titleComparator);
	}

	/**
	 * @return simple class names of matching TitleComparators
	 */
	public static Set<String> compareAll(String title1, String title2) {
		if (titleComparators.isEmpty()) {
			System.err.println("Warning: No comparator used.");
		}
		Set<String> matchingComparators = new HashSet<String>();
		for (TitleComparator matchingComparator : titleComparators) {
			if (matchingComparator.compare(title1, title2)) {
				matchingComparators.add(matchingComparator.getSimpleClassName());
			}
		}
		return matchingComparators;
	}

	public static Collection<TitleComparator> getTitleComparators() {
		return titleComparators;
	}

	public abstract boolean compare(String title1, String title2);

	public String getSimpleClassName() {
		return getClass().getSimpleName();
	}

	protected String keepAsciiLettersAndNumbers(String string) {
		return string.replaceAll("[^A-Za-z0-9]", "");
	}
}