package de.adrianwilke.gutenberg.generators;

import de.adrianwilke.gutenberg.content.Text;

/**
 * Generates a HTML page.
 * 
 * @author Adrian Wilke
 */
public class HtmlGeneratorSingle {

	protected StringBuilder stringBuilder;
	protected Text text;

	public HtmlGeneratorSingle(Text text) {
		stringBuilder = new StringBuilder();
		this.text = text;

	}

	public HtmlGeneratorSingle generate() {
		generateHeader();
		generateRows();
		return generateFooter();
	}

	/**
	 * Closes table and generates footer.
	 */
	public HtmlGeneratorSingle generateFooter() {
		stringBuilder.append("</table>");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("</body></html>");
		stringBuilder.append(System.lineSeparator());
		return this;
	}

	/**
	 * Generates header and opens table
	 */
	public HtmlGeneratorSingle generateHeader() {
		stringBuilder.append("<!doctype html><html><head><meta charset=\"utf-8\"/>");
		stringBuilder.append("<title></title>");
		stringBuilder.append("<link href=\"https://fonts.googleapis.com/css?family=Merriweather\" rel=\"stylesheet\">");
		stringBuilder.append("<style type=\"text/css\">");
		stringBuilder.append("table { margin:0 auto }");
		stringBuilder.append("td { border-bottom-style: solid; width:50% ; vertical-align:top}");
		stringBuilder.append("span { color:#999; background:#eee }");
		stringBuilder.append("html { font-size:9pt; font-family: 'Merriweather', serif; }");
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
	public HtmlGeneratorSingle generateRows() {
		stringBuilder.append("<tr>");
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("<td>");
		stringBuilder.append(System.lineSeparator());

		for (int i = text.getRoot().getIndexFirst(); i <= text.getRoot().getIndexLast(); i++) {
			stringBuilder.append(text.getLineIndexes().contains(i) ? "" : "<span>&#9679;");
			stringBuilder.append(text.getLine(i));
			stringBuilder.append(text.getLine(i).isEmpty() ? "<br/><br/>" : "");
			stringBuilder.append(text.getLineIndexes().contains(i) ? "" : "</span>");
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append("</td>");

		stringBuilder.append("</tr>");
		stringBuilder.append(System.lineSeparator());

		return this;
	}

	@Override
	public String toString() {
		return stringBuilder.toString();
	}
}
