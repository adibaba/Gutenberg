package de.adrianwilke.gutenberg.entities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.arq.querybuilder.Order;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.Uris;

/**
 * Gutenberg eBook.
 * 
 * E.g. http://www.gutenberg.org/ebooks/19778
 * 
 * @author Adrian Wilke
 */
public class Ebook extends Node {

	public static List<RDFNode> getEbooks() {
		List<RDFNode> rdfNodes = new LinkedList<RDFNode>();
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("ebook")
				.addWhere("?ebook", Uris.enclose(Uris.RDF_TYPE), Uris.enclose(Uris.PGTERMS_EBOOK))
				.addOrderBy("ebook", Order.ASCENDING);
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			rdfNodes.add(results.nextSolution().get("ebook"));
		}
		Collections.sort(rdfNodes, getIdComparator());
		return rdfNodes;
	}

	public static List<RDFNode> getEbooks(Language language) {
		List<RDFNode> rdfNodes = new LinkedList<RDFNode>();
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		for (Triple triple : language.getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			rdfNodes.add(results.nextSolution().get("item"));
		}
		Collections.sort(rdfNodes, getIdComparator());
		return rdfNodes;
	}

	public static List<RDFNode> getEbooks(Language language, DcType dcType) {
		List<RDFNode> rdfNodes = new LinkedList<RDFNode>();
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		for (Triple triple : language.getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		for (Triple triple : dcType.getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			QuerySolution x = results.nextSolution();
			rdfNodes.add(x.get("item"));
		}
		Collections.sort(rdfNodes, getIdComparator());
		return rdfNodes;
	}

	public static List<Triple> getQueryTriples(String subjectVariableName) {
		List<Triple> triples = new LinkedList<Triple>();
		SelectBuilder sb = new SelectBuilder();
		triples.add(sb.makeTriplePath("?" + subjectVariableName, Uris.enclose(Uris.RDF_TYPE),
				Uris.enclose(Uris.PGTERMS_EBOOK)).asTriple());
		return triples;
	}

	public Ebook(String uri) {
		super(uri);
	}

	public String getTitle() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_TITLE), "?value", "value");
	}

	public String getAlternative() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_ALTERNATIVE), "?value", "value");
	}

	public String getCreatorUri() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_CREATOR), "?value", "value");
	}

	public Author getCreator() {
		return new Author(getCreatorUri());
	}

	@Override
	public String toString() {
		String title = getValue(getEnclosedUri(), Uris.enclose(Uris.DCTERMS_TITLE), "?value", "value");
		title = getTitle().replace("\n", " - ").replace("\r", "");
		return title + "  " + getUri() + " (" + getCreator() + ")";
	}
}
