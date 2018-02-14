package de.adrianwilke.gutenberg.entities;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.arq.querybuilder.Order;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.rdf.SelectBldr;
import de.adrianwilke.gutenberg.rdf.Uris;

/**
 * Gutenberg eBook.
 * 
 * E.g. http://www.gutenberg.org/ebooks/19778
 * 
 * @author Adrian Wilke
 */
public class Ebook extends Node {

	/**
	 * @deprecated Do not work on nodes, nonsense.
	 */
	public static List<RDFNode> getEbookRdfNodes() {
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("ebook")
				.addWhere("?ebook", Uris.enclose(Uris.RDF_TYPE), Uris.enclose(Uris.PGTERMS_EBOOK))
				.addOrderBy("ebook", Order.ASCENDING);
		return sb.execute("ebook");
	}

	/**
	 * @deprecated Do not work on nodes, nonsense.
	 */
	public static List<RDFNode> getEbookRdfNodes(Language language) {
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		for (Triple triple : language.getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		return sb.execute("item");
	}

	/**
	 * @deprecated Do not work on nodes, nonsense.
	 */
	public static List<RDFNode> getEbookRdfNodes(Language language, DcType dcType) {
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		for (Triple triple : language.getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		for (Triple triple : dcType.getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		return sb.execute("item");
	}

	public static List<String> getEbookUris() {
		return getEbookUris(null, null);
	}

	public static List<String> getEbookUris(DcType dcType) {
		return getEbookUris(dcType, null);
	}

	public static List<String> getEbookUris(DcType dcType, Language language) {
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		if (language != null) {
			for (Triple triple : language.getQueryTriples("item")) {
				sb.addWhere(triple);
			}
		}
		if (dcType != null) {
			for (Triple triple : dcType.getQueryTriples("item")) {
				sb.addWhere(triple);
			}
		}
		return sb.executeGetStrings("item");
	}

	public static List<String> getEbookUris(Language language) {
		return getEbookUris(null, language);
	}

	/**
	 * Given variable will be an Gutenberg ebook.
	 */
	public static List<Triple> getQueryTriples(String subjectVariableName) {
		List<Triple> triples = new LinkedList<Triple>();
		SelectBldr sb = new SelectBldr();
		triples.add(sb.makeTriplePath("?" + subjectVariableName, Uris.enclose(Uris.RDF_TYPE),
				Uris.enclose(Uris.PGTERMS_EBOOK)).asTriple());
		return triples;
	}

	List<String> alternatives;

	List<Author> creators;

	List<String> creatorUris;

	List<Language> languages;

	List<String> titles;

	public Ebook(String uri) {
		super(uri);
	}

	public List<String> getAllTitles() {
		List<String> allTitles = new LinkedList<String>();
		allTitles.addAll(getTitles());
		allTitles.addAll(getAlternatives());
		return allTitles;
	}

	public List<String> getAlternatives() {
		if (alternatives == null) {
			alternatives = new SelectBldr().setDistinct(true).addVar("item")
					.addWhere(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_ALTERNATIVE), "?item")
					.executeGetStrings("item");
		}
		return alternatives;
	}

	public List<Author> getCreators() {
		if (creators == null) {
			creators = new LinkedList<Author>();
			for (String cratorUri : getCreatorUris()) {
				creators.add(new Author(cratorUri));
			}
		}
		return creators;
	}

	public List<String> getCreatorUris() {
		if (creatorUris == null) {
			creatorUris = new SelectBldr().setDistinct(true).addVar("item")
					.addWhere(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_CREATOR), "?item").executeGetStrings("item");
		}
		return creatorUris;
	}

	public List<Language> getLanguages() {
		if (languages == null) {
			languages = Language.getLanguages(getUri());
		}
		return languages;
	}

	public List<String> getTitles() {
		if (titles == null) {
			titles = new SelectBldr().setDistinct(true).addVar("item")
					.addWhere(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_TITLE), "?item").executeGetStrings("item");
		}
		return titles;
	}

	@Override
	public String toString() {
		// https://www.leveluplunch.com/java/examples/remove-newline-carriage-return-from-string/
		String title = getAllTitles().get(0).replaceAll("\n", " - ").replaceAll("\r", "");
		return title + "  " + getUri() + " (" + getCreators().get(0) + ")";
	}
}
