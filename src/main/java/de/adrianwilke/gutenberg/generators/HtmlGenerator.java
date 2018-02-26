package de.adrianwilke.gutenberg.generators;

import java.util.Iterator;
import java.util.List;

import de.adrianwilke.gutenberg.content.Text;

public class HtmlGenerator {

	protected int indexA = 0;
	protected int indexB = 0;
	protected StringBuilder stringBuilder;
	protected Text textA;
	protected Text textB;

	public HtmlGenerator(Text textA, Text textB) {
		stringBuilder = new StringBuilder();
		this.textA = textA;
		this.textB = textB;
	}

	public HtmlGenerator generate(List<Integer> splitA, List<Integer> splitB) {
		generateHeader();

		Iterator<Integer> itA = splitA.iterator();
		Iterator<Integer> itB = splitB.iterator();
		while (itA.hasNext() && itB.hasNext()) {
			generateRow(itA.next(), itB.next());
		}

		// If there are more parts in A or B
		while (itA.hasNext()) {
			generateRow(itA.next(), -1);
		}
		while (itB.hasNext()) {
			generateRow(-1, itB.next());
		}

		// Text not contained in A or B
		generateRow(textA.getRoot().getLineIndexes().last(), textB.getRoot().getLineIndexes().last());

		return generateFooter();
	}

	/**
	 * Closes table and generates footer.
	 */
	public HtmlGenerator generateFooter() {
		stringBuilder.append("</table>");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("</body></html>");
		stringBuilder.append(System.lineSeparator());
		return this;
	}

	/**
	 * Generates header and opens table
	 */
	public HtmlGenerator generateHeader() {
		stringBuilder.append("<!doctype html><html><head><meta charset=\"utf-8\"/>");
		stringBuilder.append("<title></title>");
		stringBuilder.append("<style type=\"text/css\">");
		stringBuilder.append("table { margin:0 auto }");
		stringBuilder.append("td { border-bottom-style: solid; width:50% ; vertical-align:top}");
		stringBuilder.append("html { font-size:10pt; }");
		stringBuilder.append("</style>");
		stringBuilder.append("</head><body>");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("<table>");
		stringBuilder.append(System.lineSeparator());
		return this;
	}

	/**
	 * Generates one row holding two cells.
	 */
	public HtmlGenerator generateRow(int endIndexA, int endIndexB) {
		stringBuilder.append("<tr>");
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("<td>");
		stringBuilder.append(System.lineSeparator());
		for (int i = indexA; i <= endIndexA && indexA != -1; i++) {
			if (!textA.getLineIndexes().contains(i)) {
				stringBuilder.append("<p style=\"color:#666\">");
			}
			stringBuilder.append(textA.getLine(i));
			if (textA.getLine(i).isEmpty()) {
				stringBuilder.append("<br/><br/>");
			}
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append("</td>");

		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("<td>");
		stringBuilder.append(System.lineSeparator());
		for (int i = indexB; i <= endIndexB && indexB != -1; i++) {
			if (!textB.getLineIndexes().contains(i)) {
				stringBuilder.append("<p style=\"color:#666\">");
			}
			stringBuilder.append(textB.getLine(i));
			if (textB.getLine(i).isEmpty()) {
				stringBuilder.append("<br/><br/>");
			}
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append("</td>");
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("</tr>");
		stringBuilder.append(System.lineSeparator());

		indexA = endIndexA + 1;
		indexB = endIndexB + 1;

		return this;
	}

	@Override
	public String toString() {
		return stringBuilder.toString();
	}
}
