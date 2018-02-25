package de.adrianwilke.gutenberg.content.re;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Human comparison of texts.
 * 
 * @author Adrian Wilke
 */
public class TxtAnalyzer {

	public static boolean EXECUTE = false;
	public static List<String> FILE_CHARSETS;
	public static List<String> FILE_PATHS;

	public static void main(String[] args) {
		mainConfigure(args);

		List<Txt> texts = new LinkedList<Txt>();
		for (int i = 0; i < FILE_PATHS.size(); i++) {
			texts.add(new FullTxt(FILE_PATHS.get(i), FILE_CHARSETS.get(i)));
		}

		List<Txt> textsCut = new LinkedList<Txt>();
		TxtCutter cutter = new TxtCutter();
		for (int i = 0; i < FILE_PATHS.size(); i++) {
			textsCut.add(cutter.cut(texts.get(i)));
		}

		// Context of sections
		if (EXECUTE) {
			Txt txt = textsCut.get(0);
			int range = 2;
			int section = 4;

			System.out.println("Text:");
			System.out.println(txt);
			System.out.println();

			System.out.println("Overview:");
			for (Entry<Integer, List<Txt>> sectionEntry : txt.getSections().entrySet()) {
				System.out
						.println("Section " + sectionEntry.getKey() + ": " + sectionEntry.getValue().size() + " parts");
			}
			System.out.println();

			System.out.println("Context of section " + section + ":");
			System.out.println();
			new TxtAnalyzer().printContextOfTextPart(txt.getSections().get(section), range);
			System.out.println();
		}

		// Parts in sections
		if (EXECUTE) {
			Txt textA = textsCut.get(0);
			Txt textB = textsCut.get(1);
			new TxtAnalyzer().printComparison(textA, textB);
		}

	}

	private static void mainConfigure(String[] args) {
		if (args.length == 0) {
			System.err.println("Please check arguments.");
			System.exit(1);
		} else if (args.length % 2 != 0) {
			System.err.println("Please check arguments.");
			System.exit(1);
		} else {
			FILE_PATHS = new LinkedList<String>();
			FILE_CHARSETS = new LinkedList<String>();
			System.out.println("Files in arguments:");
			for (int argIndex = 0; argIndex < args.length; argIndex += 2) {
				System.out.print(((argIndex / 2) + 1));
				FILE_PATHS.add(args[argIndex]);
				System.out.print(" " + args[argIndex]);
				FILE_CHARSETS.add(args[(argIndex + 1)]);
				System.out.println(" " + args[(argIndex + 1)]);
			}
			System.out.println();
		}
	}

	/**
	 * Prints comparison of parts in sections
	 */
	protected void printComparison(Txt textA, Txt textB) {

		System.out.println("File: " + textA);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Txt>> section : textA.getSections().entrySet()) {
			System.out.println("Number of parts in section " + section.getKey() + ": " + section.getValue().size());
		}

		System.out.println();

		System.out.println("File: " + textB);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Txt>> section : textB.getSections().entrySet()) {
			System.out.println("Number of parts in section " + section.getKey() + ": " + section.getValue().size());
		}
	}

	/**
	 * Prints context of first line in text-parts
	 */
	protected void printContextOfTextPart(List<Txt> list, int range) {
		for (int i = 0; i < list.size(); i++) {
			Txt textPart = list.get(i);
			System.out.println("[" + i + "]");
			System.out.println(textPart.getContext(textPart.getLineIndexes().first() + 1, range));
		}
	}

}