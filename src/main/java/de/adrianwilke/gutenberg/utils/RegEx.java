package de.adrianwilke.gutenberg.utils;

import java.text.Normalizer;

/**
 * RegEx utils.
 * 
 * @author Adrian Wilke
 */
public class RegEx {

	private static String whitespaceChars = "" /* dummy empty string for homogeneity */
			+ "\\u0009" // CHARACTER TABULATION
			+ "\\u000A" // LINE FEED (LF)
			+ "\\u000B" // LINE TABULATION
			+ "\\u000C" // FORM FEED (FF)
			+ "\\u000D" // CARRIAGE RETURN (CR)
			+ "\\u0020" // SPACE
			+ "\\u0085" // NEXT LINE (NEL)
			+ "\\u00A0" // NO-BREAK SPACE
			+ "\\u1680" // OGHAM SPACE MARK
			+ "\\u180E" // MONGOLIAN VOWEL SEPARATOR
			+ "\\u2000" // EN QUAD
			+ "\\u2001" // EM QUAD
			+ "\\u2002" // EN SPACE
			+ "\\u2003" // EM SPACE
			+ "\\u2004" // THREE-PER-EM SPACE
			+ "\\u2005" // FOUR-PER-EM SPACE
			+ "\\u2006" // SIX-PER-EM SPACE
			+ "\\u2007" // FIGURE SPACE
			+ "\\u2008" // PUNCTUATION SPACE
			+ "\\u2009" // THIN SPACE
			+ "\\u200A" // HAIR SPACE
			+ "\\u2028" // LINE SEPARATOR
			+ "\\u2029" // PARAGRAPH SEPARATOR
			+ "\\u202F" // NARROW NO-BREAK SPACE
			+ "\\u205F" // MEDIUM MATHEMATICAL SPACE
			+ "\\u3000" // IDEOGRAPHIC SPACE
	;

	public static String getAscii(String string) {
		// https://stackoverflow.com/a/3322174/1543389
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		return string.replaceAll("[^\\p{ASCII}]", "");
	}

	public static String removeAllExeptAlphaNumberUnderscore(String string) {
		return string.replaceAll("[^a-zA-Z0-9_]", "");
	}

	public static String replaceAllExeptAlphaNumberBySpace(String string) {
		return string.replaceAll("[^a-zA-Z0-9]", " ");
	}

	public static String replaceLinebreaksBySpace(String string) {
		// https://www.leveluplunch.com/java/examples/remove-newline-carriage-return-from-string/
		return string.replaceAll("\n", " ").replaceAll("\r", "");
	}

	public static String replaceLinebreaksBySpaceMinusSpace(String string) {
		// https://www.leveluplunch.com/java/examples/remove-newline-carriage-return-from-string/
		return string.replaceAll("\n", " - ").replaceAll("\r", "");
	}

	public static String replaceSpacesByUnderscore(String string) {
		return string.replaceAll("[" + whitespaceChars + "]", "_");
	}

	private String string;

	public RegEx(String string) {
		this.string = string;
	}

	public RegEx getAscii() {
		string = getAscii(string);
		return this;
	}

	public RegEx removeAllExeptAlphaNumber() {
		string = removeAllExeptAlphaNumber(string);
		return this;
	}

	protected String removeAllExeptAlphaNumber(String string) {
		return string.replaceAll("[^A-Za-z0-9]", "");
	}

	public RegEx removeAllExeptAlphaNumberUnderscore() {
		string = removeAllExeptAlphaNumberUnderscore(string);
		return this;
	}

	public RegEx replaceLinebreaksBySpace() {
		string = replaceLinebreaksBySpace(string);
		return this;
	}

	public RegEx replaceLinebreaksBySpaceMinusSpace() {
		string = replaceLinebreaksBySpaceMinusSpace(string);
		return this;
	}

	public RegEx replaceSpacesByUnderscore() {
		string = replaceSpacesByUnderscore(string);
		return this;
	}

	@Override
	public String toString() {
		return string;
	}
}