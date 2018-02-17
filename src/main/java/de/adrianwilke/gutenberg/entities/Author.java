package de.adrianwilke.gutenberg.entities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.rdf.SelectBldr;
import de.adrianwilke.gutenberg.rdf.Uris;
import de.adrianwilke.gutenberg.utils.Comparators;

/**
 * Gutenberg authors/agents.
 * 
 * URI http://www.gutenberg.org/2009/agents/7 represents URL
 * http://www.gutenberg.org/ebooks/author/7
 * 
 * @author Adrian Wilke
 */
public class Author extends RdfResource {

	private String name;
	private List<String> textEbookUris;

	public Author(String uri) {
		super(uri);
	}

	public String getName() {
		if (name == null) {
			name = getValue(getEnclosedUri(), Uris.enclose(Uris.PGTERMS_NAME), "?value", "value");
		}
		return name;
	}

	@Override
	public String toString() {
		return getName() + "  " + getUri();
	}

	private List<RDFNode> getTextEbookNodes() {
		SelectBldr sb = new SelectBldr();

		// Ebooks of this autor
		Triple creatorTriple = sb.makeTriplePath("?ebook", Uris.enclose(Uris.DCTERMS_CREATOR), getEnclosedUri())
				.asTriple();
		sb.setDistinct(true).addWhere(creatorTriple).addVar("ebook");

		// Only type text
		for (Triple triple : new DcType(DcType.TEXT).getQueryTriples("ebook")) {
			sb.addWhere(triple);
		}
		return sb.execute("ebook");
	}

	public List<Ebook> getTextEbooks() {
		List<Ebook> textEbooks = new LinkedList<Ebook>();
		for (RDFNode rdfNode : getTextEbookNodes()) {
			textEbooks.add(new Ebook(rdfNode.toString()));
		}
		Collections.sort(textEbooks, new Comparators<Ebook>().getToStringDefault());
		return textEbooks;
	}

	public List<String> getTextEbookUris() {
		if (textEbookUris == null) {
			textEbookUris = new LinkedList<String>();
			for (RDFNode rdfNode : getTextEbookNodes()) {
				textEbookUris.add(rdfNode.toString());
			}
		}
		return textEbookUris;
	}

	public void printTextEbooks() {
		for (Ebook ebook : getTextEbooks()) {
			System.out.println(ebook);
		}
	}
}
