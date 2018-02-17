package de.adrianwilke.gutenberg.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.adrianwilke.gutenberg.rdf.SelectBldr;
import de.adrianwilke.gutenberg.rdf.Uris;
import de.adrianwilke.gutenberg.utils.Maps;

/**
 * DCMI Metadata Terms dcterms:hasFormat.
 * 
 * http://purl.org/dc/terms/hasFormat
 * 
 * Comment: "A related resource that is substantially the same as the
 * pre-existing described resource, but in another format"
 * 
 * Note: "This term is intended to be used with non-literal values as defined in
 * the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/). As
 * of December 2007, the DCMI Usage Board is seeking a way to express this
 * intention with a formal range declaration."
 * 
 * http://dublincore.org/2012/06/14/dcterms#hasFormat
 * 
 * @author Adrian Wilke
 */
public class DcFormat {

	public static String PREFIX_DIRS = "http://www.gutenberg.org/dirs/";
	public static String PREFIX_EBOOKS = "http://www.gutenberg.org/ebooks/";
	public static String PREFIX_EPUB = "http://www.gutenberg.org/cache/epub/";
	public static String PREFIX_FILES = "http://www.gutenberg.org/files/";

	// Numbers: 2018-02-10
	public static String TYPE_CASE_INSENSITIVE_ZIP = "zip"; // 136K
	public static String TYPE_CASE_INSENSITIVE_TXT = "txt"; // 88K
	public static String TYPE_CASE_INSENSITIVE_RDF = "rdf"; // 56K
	public static String TYPE_CASE_INSENSITIVE_EPUB_NOIMAGES = "epub.noimages"; // 55K
	public static String TYPE_CASE_INSENSITIVE_EPUB_IMAGES = "epub.images"; // 54K
	public static String TYPE_CASE_INSENSITIVE_KINDLE_NOIMAGES = "kindle.noimages"; // 54K
	public static String TYPE_CASE_INSENSITIVE_KINDLE_IMAGES = "kindle.images"; // 54K
	public static String TYPE_CASE_INSENSITIVE_HTM = "htm"; // 45K
	public static String TYPE_CASE_INSENSITIVE_TXT_UTF_8 = "txt.utf-8"; // 40K
	public static String TYPE_CASE_INSENSITIVE_MP3 = "mp3"; // 23K
	public static String TYPE_CASE_INSENSITIVE_COVER_SMALL_JPG = "cover.small.jpg"; // 19K
	public static String TYPE_CASE_INSENSITIVE_COVER_MEDIUM_JPG = "cover.medium.jpg"; // 19K
	public static String TYPE_CASE_INSENSITIVE_HTML_NOIMAGES = "html.noimages"; // 9K
	public static String TYPE_CASE_INSENSITIVE_HTML_IMAGES = "html.images"; // 9K
	public static String TYPE_CASE_INSENSITIVE_OGG = "ogg"; // 9K
	public static String TYPE_CASE_INSENSITIVE_SPX = "spx"; // 9K
	public static String TYPE_CASE_INSENSITIVE_M4B = "m4b"; // 9K
	public static String TYPE_CASE_INSENSITIVE_HTML = "html"; // 2K
	public static String TYPE_CASE_INSENSITIVE_PNG = "png"; // 2K
	public static String TYPE_CASE_INSENSITIVE_PDF = "pdf"; // 1K

	public static String TYPE_H_ZIP = "-h.zip"; // HTML ZIP

	public static TreeMap<String, Integer> getDcFormats(boolean caseSensitive) {

		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("format").addWhere("?s",
				Uris.enclose(Uris.DCTERMS_HAS_FORMAT), "?format");

		Map<String, Integer> formatsMap = new HashMap<String, Integer>();

		for (String format : sb.executeGetStrings("format")) {

			if (!caseSensitive) {
				format = format.toLowerCase();
			}
			format = format.substring(format.lastIndexOf("/"));
			format = format.substring(format.indexOf(".") + 1);

			if (formatsMap.containsKey(format)) {
				formatsMap.put(format, formatsMap.get(format) + 1);
			} else {
				formatsMap.put(format, 1);
			}
		}
		return Maps.sortMapByValue(formatsMap);
	}

	private String prefix;

	private String suffix;

	public DcFormat(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public boolean isFormat(String format) {
		if (prefix != null && suffix != null) {
			return format.startsWith(prefix) && format.endsWith(suffix);
		} else if (prefix != null) {
			return format.startsWith(prefix);
		} else if (suffix != null) {
			return format.endsWith(suffix);
		} else {
			System.err.println("Warning: " + DcFormat.class.getSimpleName() + " prefix and suffix NULL");
			return true;
		}
	}
}