package de.adrianwilke.gutenberg.bilingual;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.adrianwilke.gutenberg.data.FileSerializer;
import de.adrianwilke.gutenberg.exceptions.FileNotFoundRuntimeException;
import de.adrianwilke.gutenberg.exceptions.FileSerializerRuntimeException;
import de.adrianwilke.gutenberg.tools.RegEx;

/**
 * Meta information of Bilingual match.
 * 
 * On changes: Beware of {@link BilingualMatch#serialVersionUID}
 * 
 * @author Adrian Wilke
 */
public class BilingualMatch implements Serializable {

	private static Set<BilingualMatch> bilingualMatches = new HashSet<>();

	private static final long serialVersionUID = 1L;

	public static boolean add(BilingualMatch bilingualMatch) {
		return bilingualMatches.add(bilingualMatch);
	}

	public static Set<BilingualMatch> getAll() {
		return bilingualMatches;
	}

	/**
	 * @throws FileNotFoundRuntimeException
	 * @throws FileSerializerRuntimeException
	 */
	@SuppressWarnings("unchecked")
	public static void readAll(String baseSerializationDirectory, String filePath)
			throws FileNotFoundException, IOException {
		bilingualMatches = (Set<BilingualMatch>) new FileSerializer(baseSerializationDirectory).read(filePath);
	}

	public static String toStringAll() {
		StringBuilder sb = new StringBuilder();

		SortedSet<BilingualMatch> sortedBilingualMatches = new TreeSet<BilingualMatch>(
				new Comparator<BilingualMatch>() {
					@Override
					public int compare(BilingualMatch m1, BilingualMatch m2) {
						return m1.originMatchingTitle.compareTo(m2.originMatchingTitle);
					}
				});
		sortedBilingualMatches.addAll(bilingualMatches);

		for (BilingualMatch bilingualMatch : sortedBilingualMatches) {
			sb = bilingualMatch.toStringBuilder(sb);
		}
		return sb.toString();
	}

	public static String toStringAllMultilines() {
		StringBuilder sb = new StringBuilder();
		for (BilingualMatch bilingualMatch : bilingualMatches) {
			sb = bilingualMatch.toStringBuilderMultilines(sb);
		}
		return sb.toString();
	}

	/**
	 * @throws FileNotFoundRuntimeException
	 * @throws FileSerializerRuntimeException
	 */
	public static void writeAll(String baseSerializationDirectory, String filePath)
			throws FileNotFoundException, IOException {
		new FileSerializer(baseSerializationDirectory).write(filePath, bilingualMatches);
	}

	private int candidateId;
	private String candidateMatchingTitle;
	private Map<String, String> matchingComparatorsAndTitles = new HashMap<String, String>();
	private int originId;

	private String originMatchingTitle;

	/**
	 * Replaces line breaks in titles.
	 */
	public BilingualMatch(int originId, int candidateId, String originMatchingTitle, String candidateMatchingTitle,
			Map<String, String> matchingComparatorsAndTitles) {
		this.originId = originId;
		this.candidateId = candidateId;
		this.originMatchingTitle = RegEx.replaceLinebreaksBySpaceMinusSpace(originMatchingTitle);
		this.candidateMatchingTitle = RegEx.replaceLinebreaksBySpaceMinusSpace(candidateMatchingTitle);
		this.matchingComparatorsAndTitles = matchingComparatorsAndTitles;
	}

	@Override
	public String toString() {
		return toStringBuilder(new StringBuilder()).toString();
	}

	public StringBuilder toStringBuilder(StringBuilder sb) {

		for (int i = 0; i < 6 - String.valueOf(originId).length(); i++) {
			sb.append(" ");
		}
		sb.append(originId);
		sb.append(" ");

		for (int i = 0; i < 6 - String.valueOf(candidateId).length(); i++) {
			sb.append(" ");
		}
		sb.append(candidateId);
		sb.append(" ");

		int numberOfComparators = matchingComparatorsAndTitles.size();
		for (int i = 0; i < 2 - String.valueOf(numberOfComparators).length(); i++) {
			sb.append(" ");
		}
		sb.append(numberOfComparators);

		sb.append(" [");
		sb.append(originMatchingTitle);
		sb.append("  |  ");
		sb.append(candidateMatchingTitle);
		sb.append("]");
		sb.append(System.lineSeparator());
		return sb;
	}

	public StringBuilder toStringBuilderMultilines(StringBuilder sb) {
		sb.append("Origin:    ");
		sb.append(originId);
		sb.append(System.lineSeparator());
		sb.append("Candidate: ");
		sb.append(candidateId);
		sb.append(System.lineSeparator());
		sb.append("Comparators: ");
		sb.append(System.lineSeparator());
		for (String comparator : matchingComparatorsAndTitles.keySet()) {
			sb.append(" Comparator: ");
			sb.append(comparator.substring(comparator.lastIndexOf(".") + 1));
			sb.append(System.lineSeparator());
			sb.append("  Origin title:    ");
			sb.append(originMatchingTitle);
			sb.append(System.lineSeparator());
			sb.append("  Candidate title: ");
			sb.append(candidateMatchingTitle);
			sb.append(System.lineSeparator());
			sb.append("  Matching string: ");
			sb.append(matchingComparatorsAndTitles.get(comparator));
			sb.append(System.lineSeparator());
		}
		return sb;
	}

	public String toStringMultilines() {
		return toStringBuilderMultilines(new StringBuilder()).toString();
	}
}