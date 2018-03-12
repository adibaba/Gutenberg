package de.adrianwilke.gutenberg.content;

import java.util.Collection;

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

	public final static boolean DEBUG = false;

	TextComparators textComparators = new TextComparators();
	TextComparators textComparatorsReverse = new TextComparators();
	Text[] textsA;
	Text[] textsB;
	int[] useA;
	int[] useB;

	EqualPartsSearch(Collection<Text> textsA, Collection<Text> textsB) {
		this.textsA = textsA.toArray(new Text[0]);
		this.textsB = textsB.toArray(new Text[0]);

		this.useA = new int[this.textsA.length];
		this.useB = new int[this.textsB.length];

		double lengthRatio = 1d * LengthComparator.getLength(textsA) / LengthComparator.getLength(textsB);
		textComparators.addCorpusComparator(2);
		textComparatorsReverse.addCorpusComparator(2);
		textComparators.addLengthComparator(2, lengthRatio);
		textComparatorsReverse.addLengthComparator(2, 1 / lengthRatio);
		textComparators.addPunctuationComparator(1);
		textComparatorsReverse.addPunctuationComparator(1);
	}

	protected void printDebugInfo(int indexA, int bestSolutionCode) {
		System.out.println(bestSolutionCode);

		switch (bestSolutionCode) {
		case A1_B1:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsB[indexA].toStringAllLines(true));
			break;
		case A1_B0:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsB[indexA - 1].toStringAllLines(true));
			break;
		case A1_B2:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsB[indexA + 1].toStringAllLines(true));
			break;
		case A1_B0_B1:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsB[indexA - 1].toStringAllLines(true));
			System.out.println(textsB[indexA].toStringAllLines(true));
			break;
		case A1_B1_B2:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsB[indexA].toStringAllLines(true));
			System.out.println(textsB[indexA + 1].toStringAllLines(true));
			break;
		case A1_A2_B1:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsA[indexA + 1].toStringAllLines(true));
			System.out.println(textsB[indexA].toStringAllLines(true));
			break;
		case A1_A2_B2:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsA[indexA + 1].toStringAllLines(true));
			System.out.println(textsB[indexA + 1].toStringAllLines(true));
			break;
		case A1_A2_B1_B2:
			System.out.println(textsA[indexA].toStringAllLines(true));
			System.out.println(textsA[indexA + 1].toStringAllLines(true));
			System.out.println(textsB[indexA].toStringAllLines(true));
			System.out.println(textsB[indexA + 1].toStringAllLines(true));
			break;
		default:
			if (useA[indexA] == 0) {
				System.err.println(textsA[indexA].toStringAllLines(true));
			}
			break;
		}

		System.out.println();
	}

	public void search() {
		for (int i = 0; i < textsA.length; i++) {
			int bestSolutionCode = search(i);
			if (DEBUG) {
				printDebugInfo(i, bestSolutionCode);
			}
			setUse(i, bestSolutionCode);
		}
	}

	protected int search(int indexA) {

		double bestScore = 0;
		int bestSolutionCode = 0;

		// A0 : B-1
		if (indexA > 0) {
			if (useA[indexA] == 0 && useB[indexA - 1] == 0) {
				double score = textComparators.compareAll(textsA[indexA], textsB[indexA - 1]);
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B0;
				}
			}
		}

		// A0 : B-1 B0
		if (textsB.length > indexA && indexA > 0) {
			if (useA[indexA] == 0 && useB[indexA - 1] == 0 && useB[indexA] == 0) {
				double score = textComparatorsReverse.compareAll(textsB[indexA], textsB[indexA - 1], textsA[indexA]);
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B0_B1;
				}
			}
		}

		// Enforce not to skip line of text B
		if (indexA > 0 && useB[indexA - 1] == 0) {
			if (DEBUG) {
				System.out.println(bestScore);
			}
			return bestSolutionCode;
		}

		// A0 : B0
		if (textsB.length > indexA) {
			if (useA[indexA] == 0 && useB[indexA] == 0) {
				double score = textComparators.compareAll(textsA[indexA], textsB[indexA]);
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B1;
				}
			}
		}

		// A0 : B1
		if (textsB.length > indexA + 1) {
			if (useA[indexA] == 0 && useB[indexA + 1] == 0) {
				double score = textComparators.compareAll(textsA[indexA], textsB[indexA + 1]);
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B2;
				}
			}
		}

		// A0 : B0 B+1
		if (textsB.length > indexA + 1) {
			if (useA[indexA] == 0 && useB[indexA] == 0 && useB[indexA + 1] == 0) {
				double score = textComparatorsReverse.compareAll(textsB[indexA], textsB[indexA + 1], textsA[indexA]);
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_B1_B2;
				}
			}
		}

		// A0 A+1: B0
		if (textsA.length > indexA + 1 && textsB.length > indexA) {
			if (useA[indexA] == 0 && useA[indexA + 1] == 0 && useB[indexA] == 0) {
				double score = textComparators.compareAll(textsA[indexA], textsA[indexA + 1], textsB[indexA]);
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_A2_B1;
				}
			}
		}

		// A0 A+1: B+1
		if (textsA.length > indexA + 1 && textsB.length > indexA + 1) {
			if (useA[indexA] == 0 && useA[indexA + 1] == 0 && useB[indexA] == 0) {
				double score = textComparators.compareAll(textsA[indexA], textsA[indexA + 1], textsB[indexA + 1]);
				if (score > bestScore) {
					bestScore = score;
					bestSolutionCode = A1_A2_B2;
				}
			}
		}

		// A0 A+1 : B0 B+1
		if (textsA.length > indexA + 1 && textsB.length > indexA + 1) {
			if (useA[indexA] == 0 && useA[indexA + 1] == 0 && useB[indexA] == 0 && useB[indexA + 1] == 0) {
				double score = textComparators.compareAll(textsA[indexA], textsA[indexA + 1], textsB[indexA],
						textsB[indexA + 1]);
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

	protected void setUse(int indexA, int bestSolutionCode) {
		switch (bestSolutionCode) {
		case A1_B1:
			useA[indexA] = 1;
			useB[indexA] = 1;
			break;
		case A1_B0:
			useA[indexA] = 1;
			useB[indexA - 1] = 1;
			break;
		case A1_B2:
			useA[indexA] = 1;
			useB[indexA + 1] = 1;
			break;
		case A1_B0_B1:
			useA[indexA] = 1;
			useB[indexA - 1] = 1;
			useB[indexA] = 1;
			break;
		case A1_B1_B2:
			useA[indexA] = 1;
			useB[indexA] = 1;
			useB[indexA + 1] = 1;
			break;
		case A1_A2_B1:
			useA[indexA] = 1;
			useA[indexA + 1] = 1;
			useB[indexA] = 1;
			break;
		case A1_A2_B2:
			useA[indexA] = 1;
			useA[indexA + 1] = 1;
			useB[indexA + 1] = 1;
			break;
		case A1_A2_B1_B2:
			useA[indexA] = 1;
			useA[indexA + 1] = 1;
			useB[indexA] = 1;
			useB[indexA + 1] = 1;
			break;
		default:
			if (useA[indexA] == 0) {
				System.err.println("No equal text parts found for index: " + indexA);
			}
			break;
		}
	}
}