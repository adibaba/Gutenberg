package de.adrianwilke.gutenberg.comparators;

import java.util.Comparator;

public class Comparators<T> {

	/**
	 * toString() has to return URI.
	 */
	public Comparator<T> getToStringId() {
		return new Comparator<T>() {
			public int compare(T o1, T o2) {
				Integer id1 = Integer.valueOf(o1.toString().substring(1 + o1.toString().lastIndexOf("/")));
				Integer id2 = Integer.valueOf(o2.toString().substring(1 + o2.toString().lastIndexOf("/")));
				return id1 - id2;
			}
		};

	}

	/**
	 * toString() is compared lexicographically.
	 */
	public Comparator<T> getToStringDefault() {
		return (new Comparator<T>() {
			public int compare(T o1, T o2) {
				if (o1 == null || o2 == null) {
					System.err.println("NULL in " + Comparators.class.getSimpleName());
					return 0;
				} else if (o1.toString() == null || o2.toString() == null) {
					System.err.println("String is NULL in " + Comparators.class.getSimpleName() + o1 + " " + o2);
					return 0;
				}
				return o1.toString().compareTo(o2.toString());
			}
		});
	}
}
