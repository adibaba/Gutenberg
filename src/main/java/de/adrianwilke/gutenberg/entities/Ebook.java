package de.adrianwilke.gutenberg.entities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.arq.querybuilder.Order;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.rdf.SelectBldr;
import de.adrianwilke.gutenberg.rdf.Uris;
import de.adrianwilke.gutenberg.utils.RegEx;
import de.adrianwilke.gutenberg.utils.Strings;

/**
 * Gutenberg eBook.
 * 
 * E.g. http://www.gutenberg.org/ebooks/19778
 * 
 * @author Adrian Wilke
 */
public class Ebook extends RdfResource {

	public static final String PREFIX = "http://www.gutenberg.org/ebooks/";

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
	List<String> formats;
	List<Language> languages;
	List<String> titles;

	public Ebook(int id) {
		super(PREFIX + id);
	}

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

	public List<String> getFormatUrls() {
		if (formats == null) {
			formats = new SelectBldr().setDistinct(true).addVar("item")
					.addWhere(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_HAS_FORMAT), "?item")
					.executeGetStrings("item");
		}
		return formats;
	}

	public Set<String> getFormatUrls(DcFormat dcFormat) {
		Set<String> ebookFormats = new HashSet<String>();
		for (String formatUrl : getFormatUrls()) {
			if (dcFormat.isFormat(formatUrl)) {
				ebookFormats.add(formatUrl);
			}
		}
		return ebookFormats;
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

	public Integer getId() {
		return Strings.urlToId(getUri());
	}

	public String getPrefixForFileSystemStorage() {

		String saveCreator = "";
		if (!getCreators().isEmpty()) {
			saveCreator = new RegEx(getCreators().get(0).toString().split("\\s")[0]).replaceLinebreaksBySpace()
					.getAscii().replaceSpacesByUnderscore().removeAllExeptAlphaNumberUnderscore().toString();
			if (saveCreator.length() > 10) {
				saveCreator = saveCreator.substring(0, 11);
			}
			saveCreator += "__";
		}

		String saveTitle = "";
		if (!getAllTitles().isEmpty()) {
			saveTitle = new RegEx(getAllTitles().get(0)).replaceLinebreaksBySpace().replaceSpacesByUnderscore()
					.getAscii().removeAllExeptAlphaNumberUnderscore().toString();
			if (saveTitle.length() > 30) {
				saveTitle = saveTitle.substring(0, 31);
			}
			saveTitle += "__";
		}

		String saveLanguages = "";
		if (!getLanguages().isEmpty()) {
			saveLanguages = new RegEx(getLanguages().toString()).replaceSpacesByUnderscore()
					.removeAllExeptAlphaNumberUnderscore().toString();
			saveLanguages += "__";
		}

		return saveCreator + saveTitle + saveLanguages + getId();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (!getAllTitles().isEmpty()) {
			sb.append(new RegEx(getAllTitles().get(0)).replaceLinebreaksBySpaceMinusSpace());
			sb.append("  ");
		}

		sb.append(getUri());

		if (!getCreators().isEmpty()) {
			sb.append(" (");
			sb.append(getCreators().get(0).toString());
			sb.append(")");
		}

		return sb.toString();
	}
}
