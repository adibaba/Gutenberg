package de.adrianwilke.gutenberg.content;

import java.util.List;

import de.adrianwilke.gutenberg.content.comparator.LengthComparator;
import de.adrianwilke.gutenberg.content.comparator.TextComparators;

/**
 * TODO: Return find indexes.
 * 
 * @author Adrian Wilke
 */
public class EqualPartsSearch {

	public final static int A1_A2_B1 = 1;
	public final static int A1_A2_B1_B2 = 2;
	public final static int A1_A2_B2 = 3;
	public final static int A1_B0 = 4;
	public final static int A1_B0_B1 = 5;
	public final static int A1_B1 = 6;
	public final static int A1_B1_B2 = 7;
	public final static int A1_B2 = 8;

	public static boolean DEBUG = false;
	public static boolean DEBUG_VERBOSE = false;

	TextComparators textComparators = new TextComparators();
	TextComparators textComparatorsReverse = new TextComparators();
	List<Text> textsA;
	List<Text> textsB;
	int[] useA;
	int[] useB;

	EqualPartsSearch(List<Text> textsA, List<Text> textsB) {
		this.textsA = textsA;
		this.textsB = textsB;

		this.useA = new int[this.textsA.size()];
		this.useB = new int[this.textsB.size()];

		double lengthRatio = 1d * LengthComparator.getLength(textsA) / LengthComparator.getLength(textsB);

		// TODO

		// No equal text parts found for index: 49
		// double pairBonus = 0.9;
		// int weightCorpus = 3;
		// int weightLengthRatio = 2;
		// int weightLength = 2;
		// int weightPunctuation = 1;

		// No error.
		// BUT: [0] Heading incorrect -> sum of length has to be 6.
		// double pairBonus = 0.9;
		// int weightCorpus = 3;
		// int weightLengthRatio = 4;
		// int weightLength = 0;
		// int weightPunctuation = 1;

		// [2] "Wie schade, daß" and "Wahrhaftig, du" parts should be combined

		double pairBonus = 0.7;
		int weightCorpus = 1;
		int weightLengthRatio = 2;
		int weightLength = 1;
		int weightPunctuation = 1;

		textComparators.addCorpusComparator(weightCorpus, pairBonus);
		textComparatorsReverse.addCorpusComparator(weightCorpus, pairBonus);
		textComparators.addLengthComparator(weightLengthRatio, lengthRatio);
		textComparatorsReverse.addLengthComparator(weightLengthRatio, 1 / lengthRatio);
		textComparators.addLengthComparator(weightLength, 1);
		textComparatorsReverse.addLengthComparator(weightLength, 1);
		textComparators.addPunctuationComparator(weightPunctuation);
		textComparatorsReverse.addPunctuationComparator(weightPunctuation);
	}

	/**
	 * TODO
	 */
	protected void checkUse(int[] use, List<Text> texts) {
		for (int i = 0; i < use.length; i++) {
			if (use[i] == 0) {
				System.err.println("Warning: Not used: " + texts.get(i).getName());
			}
		}
	}

	protected void printDebugInfo(int indexA, int bestSolutionCode) {
		System.out.println(bestSolutionCode);

		switch (bestSolutionCode) {
		case A1_B1:
			if (DEBUG_VERBOSE) {
				System.out.println(textsA.get(indexA).toStringAllLines(true));
				System.out.println(textsB.get(indexA).toStringAllLines(true));
			}
			break;
		case A1_B0:
			if (DEBUG_VERBOSE) {
				System.out.println(textsA.get(indexA).toStringAllLines(true));
				System.out.println(textsB.get(indexA - 1).toStringAllLines(true));
			}
			break;
		case A1_B2:
			if (DEBUG_VERBOSE) {
				System.out.println(textsA.get(indexA).toStringAllLines(true));
				System.out.println(textsB.get(indexA + 1).toStringAllLines(true));
			}
			break;
		case A1_B0_B1:
			if (DEBUG_VERBOSE) {
				System.err.println(textsA.get(indexA).toStringAllLines(true));
				System.err.println(textsB.get(indexA - 1).toStringAllLines(true));
				System.err.println(textsB.get(indexA).toStringAllLines(true));
			}
			break;
		case A1_B1_B2:
			if (DEBUG_VERBOSE) {
				System.err.println(textsA.get(indexA).toStringAllLines(true));
				System.err.println(textsB.get(indexA).toStringAllLines(true));
				System.err.println(textsB.get(indexA + 1).toStringAllLines(true));
			}
			break;
		case A1_A2_B1:
			if (DEBUG_VERBOSE) {
				System.err.println(textsA.get(indexA).toStringAllLines(true));
				System.err.println(textsA.get(indexA + 1).toStringAllLines(true));
				System.err.println(textsB.get(indexA).toStringAllLines(true));
			}
			break;
		case A1_A2_B2:
			if (DEBUG_VERBOSE) {
				System.err.println(textsA.get(indexA).toStringAllLines(true));
				System.err.println(textsA.get(indexA + 1).toStringAllLines(true));
				System.err.println(textsB.get(indexA + 1).toStringAllLines(true));
			}
			break;
		case A1_A2_B1_B2:
			if (DEBUG_VERBOSE) {
				System.err.println(textsA.get(indexA).toStringAllLines(true));
				System.err.println(textsA.get(indexA + 1).toStringAllLines(true));
				System.err.println(textsB.get(indexA).toStringAllLines(true));
				System.err.println(textsB.get(indexA + 1).toStringAllLines(true));
			}
			break;
		default:
			if (useA[indexA] == 0) {
				System.err.println(textsA.get(indexA).toStringAllLines(true));
			}
			break;
		}
		if (DEBUG_VERBOSE) {
			System.out.println();
		}
	}

	public void search() {
		for (int i = 0; i < textsA.size(); i++) {
			int bestSolutionCode = search(i);
			if (DEBUG) {
				printDebugInfo(i, bestSolutionCode);
			}
			setUse(i, bestSolutionCode);
		}
		checkUse(useA, textsA);
		checkUse(useB, textsB);
	}

	protected int search(int indexA) {

		double bestScore = 0;
		int bestSolutionCode = 0;

		// A0 : B-1
		if (indexA > 0) {
			if (useA[indexA] == 0 && useB[indexA - 1] == 0) {
				double score = textComparators.compareAll(textsA.get(indexA), textsB.get(indexA - 1));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B0;
				}
			}
		}

		// A0 : B-1 B0
		if (textsB.size() > indexA && indexA > 0) {
			if (useA[indexA] == 0 && useB[indexA - 1] == 0 && useB[indexA] == 0) {
				double score = textComparatorsReverse.compareAll(textsB.get(indexA), textsB.get(indexA - 1),
						textsA.get(indexA));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B0_B1;
				}
			}
		}

		// Enforce not to skip line of text B
		if (indexA > 0 && useB[indexA - 1] == 0) {
			if (DEBUG && DEBUG_VERBOSE) {
				System.out.println(bestScore);
			}
			return bestSolutionCode;
		}

		// A0 : B0
		if (textsB.size() > indexA) {
			if (useA[indexA] == 0 && useB[indexA] == 0) {
				double score = textComparators.compareAll(textsA.get(indexA), textsB.get(indexA));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B1;
				}
			}
		}

		// A0 : B1
		if (textsB.size() > indexA + 1) {
			if (useA[indexA] == 0 && useB[indexA + 1] == 0) {
				double score = textComparators.compareAll(textsA.get(indexA), textsB.get(indexA + 1));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B2;
				}
			}
		}

		// A0 : B0 B+1
		if (textsB.size() > indexA + 1) {
			if (useA[indexA] == 0 && useB[indexA] == 0 && useB[indexA + 1] == 0) {
				double score = textComparatorsReverse.compareAll(textsB.get(indexA), textsB.get(indexA + 1),
						textsA.get(indexA));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B1_B2;
				}
			}
		}

		// A0 A+1: B0
		if (textsA.size() > indexA + 1 && textsB.size() > indexA) {
			if (useA[indexA] == 0 && useA[indexA + 1] == 0 && useB[indexA] == 0) {
				double score = textComparators.compareAll(textsA.get(indexA), textsA.get(indexA + 1),
						textsB.get(indexA));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_A2_B1;
				}
			}
		}

		// A0 A+1: B+1
		if (textsA.size() > indexA + 1 && textsB.size() > indexA + 1) {
			if (useA[indexA] == 0 && useA[indexA + 1] == 0 && useB[indexA] == 0) {
				double score = textComparators.compareAll(textsA.get(indexA), textsA.get(indexA + 1),
						textsB.get(indexA + 1));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_A2_B2;
				}
			}
		}

		// A0 A+1 : B0 B+1
		if (textsA.size() > indexA + 1 && textsB.size() > indexA + 1) {
			if (useA[indexA] == 0 && useA[indexA + 1] == 0 && useB[indexA] == 0 && useB[indexA + 1] == 0) {
				double score = textComparators.compareAll(textsA.get(indexA), textsA.get(indexA + 1),
						textsB.get(indexA), textsB.get(indexA + 1));
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_A2_B1_B2;
				}
			}
		}

		if (DEBUG) {
			System.out.println(bestScore);
		}
		return bestSolutionCode;
	}

	protected boolean setUse(int indexA, int bestSolutionCode) {
		switch (bestSolutionCode) {
		case A1_B1:
			useA[indexA] = 1;
			useB[indexA] = 1;
			return true;
		case A1_B0:
			useA[indexA] = 1;
			useB[indexA - 1] = 1;
			return true;
		case A1_B2:
			useA[indexA] = 1;
			useB[indexA + 1] = 1;
			return true;
		case A1_B0_B1:
			useA[indexA] = 1;
			useB[indexA - 1] = 1;
			useB[indexA] = 1;
			return true;
		case A1_B1_B2:
			useA[indexA] = 1;
			useB[indexA] = 1;
			useB[indexA + 1] = 1;
			return true;
		case A1_A2_B1:
			useA[indexA] = 1;
			useA[indexA + 1] = 1;
			useB[indexA] = 1;
			return true;
		case A1_A2_B2:
			useA[indexA] = 1;
			useA[indexA + 1] = 1;
			useB[indexA + 1] = 1;
			return true;
		case A1_A2_B1_B2:
			useA[indexA] = 1;
			useA[indexA + 1] = 1;
			useB[indexA] = 1;
			useB[indexA + 1] = 1;
			return true;
		default:
			if (useA[indexA] == 0) {
				System.err.println("No equal text parts found for " + textsA.get(indexA).getName());
				return false;
			} else {
				return true;
			}
		}
	}
}