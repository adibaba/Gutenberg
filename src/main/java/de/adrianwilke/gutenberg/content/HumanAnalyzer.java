package de.adrianwilke.gutenberg.content;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;

import de.adrianwilke.gutenberg.generators.HtmlGenerator;
import de.adrianwilke.gutenberg.generators.HtmlGeneratorSingle;
import de.adrianwilke.gutenberg.io.TextFileAccessor;

/**
 * Human comparison of texts.
 * 
 * @author Adrian Wilke
 */
public class HumanAnalyzer {

	protected static boolean EXECUTE = false;
	protected static boolean GENERATE = false;
	protected static List<String> FILE_CHARSETS;
	protected static List<String> FILE_PATHS;
	protected static File DIRECTORY_GENERATION;

	public static void main(String[] args) {
		mainConfigure(args);

		List<Text> texts = new LinkedList<Text>();
		for (int i = 0; i < FILE_PATHS.size(); i++) {
			texts.add(new FullText(FILE_PATHS.get(i), FILE_CHARSETS.get(i)));
		}

		List<Text> textsCut = new LinkedList<Text>();
		Cutter cutter = new Cutter();
		for (int i = 0; i < FILE_PATHS.size(); i++) {
			textsCut.add(cutter.cut(texts.get(i)));
		}

		List<Text> textsCleaned = new LinkedList<Text>();
		Cleaner cleaner = new Cleaner();
		for (int i = 0; i < FILE_PATHS.size(); i++) {
			textsCleaned.add(cleaner.clean(textsCut.get(i)));
		}

		// -------------------------------------------------------------------------------------------------------------
		// Context of sections
		if (EXECUTE) {
			Text txt = textsCut.get(0);
			int range = 2;
			int section = 4;

			System.out.println("Text:");
			System.out.println(txt);
			System.out.println();

			System.out.println("Overview:");
			for (Entry<Integer, List<Text>> sectionEntry : txt.getSections().entrySet()) {
				System.out
						.println("Section " + sectionEntry.getKey() + ": " + sectionEntry.getValue().size() + " parts");
			}
			System.out.println();

			System.out.println("Context of section " + section + ":");
			System.out.println();

			printContextOfTexts(txt.getSections().get(section), range);
			System.out.println();
		}

		// -------------------------------------------------------------------------------------------------------------
		// Parts in sections
		if (EXECUTE) {
			Text textA = textsCut.get(0);
			Text textB = textsCut.get(1);
			printComparison(textA, textB);
		}

		// -------------------------------------------------------------------------------------------------------------
		// Search for chapters
		// Generate HTML
		if (EXECUTE) {
			boolean preferLongDistances = true;

			Text textA = textsCleaned.get(0);
			Text textB = textsCleaned.get(2);

			if (EXECUTE) {
				HtmlGeneratorSingle generatorSingle = new HtmlGeneratorSingle(textA);
				generatorSingle.generate();
				String filePathSingle = new File(DIRECTORY_GENERATION, "/testSingle.htm").getPath();
				TextFileAccessor.writeStringToFile(generatorSingle.toString(), filePathSingle);
			}

			// Search for chapters in text A
			ChapterSearch chapterSearchA = new ChapterSearch(textA);
			if (!chapterSearchA.search(preferLongDistances)) {
				System.err.println("No chapters found in text A:");
				System.err.println(textA);
				System.exit(1);
			}
			SortedSet<Text> chaptersA = chapterSearchA.searchAdditionalHeadings();
			// printContextOfTexts(new LinkedList<Text>(chaptersA) , 5);

			// Search for chapters in text B
			ChapterSearch chapterSearchB = new ChapterSearch(textB);
			if (!chapterSearchB.search(preferLongDistances)) {
				System.err.println("No chapters found in text B:");
				System.err.println(textB);
				System.exit(1);
			}
			SortedSet<Text> chaptersB = chapterSearchB.searchAdditionalHeadings();

			// TODO
			// new EqualPartsSearch(chaptersA.first().getSections().get(1),
			// chaptersB.first().getSections().get(1))
			// .search();
			Text[] chpterAarray = chaptersA.toArray(new Text[0]);
			Text[] chpterBarray = chaptersB.toArray(new Text[0]);
			for (int i = 0; i < chpterAarray.length; i++) {
				// if (i == 9)
				new EqualPartsSearch(chpterAarray.clone()[i].getSections().get(1),
						chpterBarray.clone()[i].getSections().get(1)).search();
			}

			// Generate html
			if (GENERATE) {
				List<Integer> splitAendIndexes = new LinkedList<Integer>();
				splitAendIndexes.add(chaptersA.first().getFirstIndex() - 1);
				for (Text text : chaptersA) {
					splitAendIndexes.add(text.getLastIndex());
				}
				List<Integer> splitBendIndexes = new LinkedList<Integer>();
				splitBendIndexes.add(chaptersB.first().getFirstIndex() - 1);
				for (Text text : chaptersB) {
					splitBendIndexes.add(text.getLastIndex());
				}

				HtmlGenerator generator = new HtmlGenerator(textA, textB);
				generator.generate(splitAendIndexes, splitBendIndexes);

				String filePath = new File(DIRECTORY_GENERATION, "/test.htm").getPath();
				TextFileAccessor.writeStringToFile(generator.toString(), filePath);
			}
		}
	}

	private static void mainConfigure(String[] args) {
		if (args.length == 0) {
			System.err.println("Please check arguments.");
			System.exit(1);
		} else if (args.length % 2 != 1) {
			System.err.println("Please check arguments.");
			System.exit(1);
		} else {
			DIRECTORY_GENERATION = new File(args[0]);
			System.out.println("Generation directory:");
			System.out.println(DIRECTORY_GENERATION);
			FILE_PATHS = new LinkedList<String>();
			FILE_CHARSETS = new LinkedList<String>();
			System.out.println("Files (with resulting indexes)  in arguments:");
			for (int argIndex = 1; argIndex < args.length; argIndex += 2) {
				System.out.print(((argIndex / 2)));
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
	protected static void printComparison(Text textA, Text textB) {

		System.out.println("File: " + textA);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Text>> section : textA.getSections().entrySet()) {
			System.out.println("Number of parts in section " + section.getKey() + ": " + section.getValue().size());
		}

		System.out.println();

		System.out.println("File: " + textB);

		// Get parts depending on distance (a.k.a. empty lines)
		for (Entry<Integer, List<Text>> section : textB.getSections().entrySet()) {
			System.out.println("Number of parts in section " + section.getKey() + ": " + section.getValue().size());
		}
	}

	/**
	 * Prints context of first line in text-parts
	 */
	protected static void printContextOfTexts(List<Text> list, int range) {
		for (int i = 0; i < list.size(); i++) {
			Text textPart = list.get(i);
			System.out.println("[" + i + "]");
			System.out.println(textPart.getContext(textPart.getLineIndexes().first() + 1, range));
		}
	}

}