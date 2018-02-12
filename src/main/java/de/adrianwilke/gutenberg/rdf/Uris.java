package de.adrianwilke.gutenberg.rdf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.vocabulary.DCTerms;

/**
 * URI constants and helper methods.
 * 
 * @author Adrian Wilke
 */
public abstract class Uris {

	public static final String DCTERMS = DCTerms.getURI();
	public static final String DCTERMS_LANGUAGE = DCTerms.language.getURI();
	public static final String DCTERMS_CREATOR = DCTerms.creator.getURI();
	public static final String DCTERMS_TITLE = DCTerms.title.getURI();
	public static final String DCTERMS_TYPE = DCTerms.type.getURI();
	public static final String DCTERMS_ALTERNATIVE = DCTerms.alternative.getURI();
	public static final String DCTERMS_RFC4646 = DCTERMS + "RFC4646";

	public static final String PGTERMS_EBOOK = "http://www.gutenberg.org/2009/pgterms/ebook";
	public static final String PGTERMS_ALIAS = "http://www.gutenberg.org/2009/pgterms/alias";
	public static final String PGTERMS_BIRTHDATE = "http://www.gutenberg.org/2009/pgterms/birthdate";
	public static final String PGTERMS_DEATHDATE = "http://www.gutenberg.org/2009/pgterms/deathdate";
	public static final String PGTERMS_NAME = "http://www.gutenberg.org/2009/pgterms/name";
	public static final String PGTERMS_WEBPAGE = "http://www.gutenberg.org/2009/pgterms/webpage";

	public static final Map<String, String> PREFIX_CC = new HashMap<String, String>();
	public static final Map<String, String> PREFIX_DCAM = new HashMap<String, String>();
	public static final Map<String, String> PREFIX_DCTERMS = new HashMap<String, String>();
	public static final Map<String, String> PREFIX_MARCREL = new HashMap<String, String>();
	public static final Map<String, String> PREFIX_RDF = new HashMap<String, String>();
	public static final Map<String, String> PREFIX_RDFS = new HashMap<String, String>();
	public static final Map<String, String> PREFIX = new HashMap<String, String>();

	public static final String RDF = org.apache.jena.vocabulary.RDF.getURI();
	public static final String RDF_TYPE = org.apache.jena.vocabulary.RDF.type.getURI();
	public static final String RDF_VALUE = org.apache.jena.vocabulary.RDF.value.getURI();

	public static final String RDFS = org.apache.jena.vocabulary.RDFS.getURI();

	static {
		PREFIX_CC.put("cc", "http://web.resource.org/cc/");
		PREFIX_DCAM.put("dcam", "http://purl.org/dc/dcam/");
		PREFIX_DCTERMS.put("dcterms", DCTERMS);
		PREFIX_MARCREL.put("marcrel", "http://id.loc.gov/vocabulary/relators/");
		PREFIX_RDF.put("rdf", RDF);
		PREFIX_RDFS.put("rdfs", RDFS);

		PREFIX.putAll(PREFIX_CC);
		PREFIX.putAll(PREFIX_DCAM);
		PREFIX.putAll(PREFIX_DCTERMS);
		PREFIX.putAll(PREFIX_MARCREL);
		PREFIX.putAll(PREFIX_RDF);
		PREFIX.putAll(PREFIX_RDFS);
	}

	public static String enclose(String uri) {
		return "<" + uri + ">";
	}

	public static String prefix(Map<String, String> prefixMap) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> prefix : prefixMap.entrySet()) {
			sb.append("PREFIX ");
			sb.append(prefix.getKey());
			sb.append(": <");
			sb.append(prefix.getValue());
			sb.append(">");
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

}