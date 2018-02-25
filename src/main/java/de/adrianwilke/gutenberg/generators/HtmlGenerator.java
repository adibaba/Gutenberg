package de.adrianwilke.gutenberg.generators;

import java.util.List;

import de.adrianwilke.gutenberg.content.Text;

public class HtmlGenerator {

	StringBuilder sb = new StringBuilder();
	Text textA;
	Text textB;

	public HtmlGenerator(Text textA, Text textB) {
		this.textA = textA;
		this.textB = textB;
	}

	public HtmlGenerator generateCells(List<Text> partsA, List<Text> partsB, int startIndexA, int startIndexB) {
		int j = -1;
		while (true) {
			j++;
			int indexA = startIndexA + j;
			int indexB = startIndexB + j;
			if (partsA.size() > indexA && partsB.size() > indexB) {
				generateCells(partsA.get(indexA), partsB.get(indexB));
			} else {
				// TODO: Handle retun value
				if (partsA.size() - 1 != indexA && partsB.size() - 1 != indexB) {
					System.err.println("Not all pars displayed!");
				}
				return this;
			}
		}
	}

	public HtmlGenerator generateCells(Text partA, Text partB) {
		sb.append("<tr>");
		sb.append(System.lineSeparator());
		sb.append("<td>");
		sb.append(System.lineSeparator());
		for (int i = partA.getLineIndexes().first(); i <= partA.getLineIndexes().last(); i++) {
			sb.append(textA.getLine(i));
			if (textA.getLine(i).isEmpty()) {
				sb.append("<br/><br/>");
			}
			sb.append(System.lineSeparator());
		}
		sb.append("</td>");
		sb.append(System.lineSeparator());
		sb.append("<td>");
		sb.append(System.lineSeparator());
		for (int i = partB.getLineIndexes().first(); i <= partB.getLineIndexes().last(); i++) {
			sb.append(textB.getLine(i));
			if (textB.getLine(i).isEmpty()) {
				sb.append("<br/><br/>");
			}
			sb.append(System.lineSeparator());
		}
		sb.append("</tr>");
		sb.append(System.lineSeparator());
		return this;
	}

	public HtmlGenerator generateFooter() {
		sb.append("</table>");
		sb.append(System.lineSeparator());
		sb.append("</body></html>");
		sb.append(System.lineSeparator());
		return this;
	}

	public HtmlGenerator generateHeader() {
		sb.append("<!doctype html><html><head><meta charset=\"utf-8\"/>");
		sb.append("<title></title>");
		sb.append("<style type=\"text/css\">");
		sb.append("table { margin:0 auto }");
		sb.append("td { border-bottom-style: solid; width:50% ; vertical-align:top}");
		sb.append("</style>");
		sb.append("</head><body>");
		sb.append(System.lineSeparator());
		sb.append("<table>");
		sb.append(System.lineSeparator());
		return this;
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
