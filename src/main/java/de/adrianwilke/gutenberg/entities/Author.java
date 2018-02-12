package de.adrianwilke.gutenberg.entities;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.Uris;

/**
 * Gutenberg authors/agents.
 * 
 * URI http://www.gutenberg.org/2009/agents/7 represents URL
 * http://www.gutenberg.org/ebooks/author/7
 * 
 * @author Adrian Wilke
 */
public class Author extends Node {

	public Author(String uri) {
		super(uri);
	}

	public String getName() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.PGTERMS_NAME), "?value", "value");
	}

	public String getAlias() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.PGTERMS_ALIAS), "?value", "value");
	}

	public String getBirthdate() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.PGTERMS_BIRTHDATE), "?value", "value");
	}

	public String getDeathdate() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.PGTERMS_DEATHDATE), "?value", "value");
	}

	public String getWebpage() {
		return getValue(getEnclosedUri(), Uris.enclose(Uris.PGTERMS_WEBPAGE), "?value", "value");
	}

	@Override
	public String toString() {
		return getName() + "  " + getUri();
	}

	public List<RDFNode> getTextEbookNodes() {
		List<RDFNode> rdfNodes = new LinkedList<RDFNode>();
		SelectBuilder sb = new SelectBuilder();
		Triple creatorTriple = sb.makeTriplePath("?ebook", Uris.enclose(Uris.DCTERMS_CREATOR), getEnclosedUri())
				.asTriple();
		sb.setDistinct(true).addWhere(creatorTriple).addVar("ebook");
		for (Triple triple : new DcType(DcType.TEXT).getQueryTriples("ebook")) {
			sb.addWhere(triple);
		}
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			rdfNodes.add(results.nextSolution().get("ebook"));
		}
		return rdfNodes;
	}

	public List<Ebook> getTextEbooks() {
		List<Ebook> textEbools = new LinkedList<Ebook>();
		for (RDFNode rdfNode : getTextEbookNodes()) {
			textEbools.add(new Ebook(rdfNode.toString()));
		}
		return textEbools;
	}

	public void printTextEbooks() {
		for (Ebook ebook : getTextEbooks()) {
			System.out.println(ebook);
		}
	}
}
