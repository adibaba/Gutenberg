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

	/**
	 * @see Normalizer https://stackoverflow.com/a/3322174/1543389
	 */
	public static String getAscii(String string) {
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		return string.replaceAll("[^\\p{ASCII}]", "");
	}

	public static String removeAllExeptAlphaNumber(String string) {
		return string.replaceAll("[^A-Za-z0-9]", "");
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

	/**
	 * Replaces all german umlauts in the input string with the usual replacement
	 * scheme, also taking into account capitilization. A test String such as "Käse
	 * Köln Füße Öl Übel Äü Üß ÄÖÜ Ä Ö Ü ÜBUNG" will yield the result "Kaese Koeln
	 * Fuesse Oel Uebel Aeue Uess AEOEUe Ae Oe Ue UEBUNG"
	 * 
	 * @see http://gordon.koefner.at/blog/coding/replacing-german-umlauts/
	 * 
	 * @param input
	 * @return the input string with replaces umlaute
	 */
	public static String replaceUmlauts(String input) {

		// replace all lower Umlauts
		String o_strResult = input.replaceAll("ü", "ue").replaceAll("ö", "oe").replaceAll("ä", "ae").replaceAll("ß",
				"ss");

		// first replace all capital umlaute in a non-capitalized context (e.g. Übung)
		o_strResult = o_strResult.replaceAll("Ü(?=[a-zäöüß ])", "Ue").replaceAll("Ö(?=[a-zäöüß ])", "Oe")
				.replaceAll("Ä(?=[a-zäöüß ])", "Ae");

		// now replace all the other capital umlaute
		o_strResult = o_strResult.replaceAll("Ü", "UE").replaceAll("Ö", "OE").replaceAll("Ä", "AE");

		return o_strResult;
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

	public RegEx removeAllExeptAlphaNumberUnderscore() {
		string = removeAllExeptAlphaNumberUnderscore(string);
		return this;
	}

	public RegEx replaceAllExeptAlphaNumberBySpace() {
		string = replaceAllExeptAlphaNumberBySpace(string);
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

	public RegEx replaceUmlauts() {
		string = replaceUmlauts(string);
		return this;
	}

	@Override
	public String toString() {
		return string;
	}
}