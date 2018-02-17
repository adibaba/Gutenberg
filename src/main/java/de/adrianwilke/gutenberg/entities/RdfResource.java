package de.adrianwilke.gutenberg.entities;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.rdf.SelectBldr;
import de.adrianwilke.gutenberg.utils.Comparators;

/**
 * Base class for RDF resources.
 * 
 * @author Adrian Wilke
 */
public class RdfResource extends RdfNode {

	private String uri;

	public RdfResource(String uri) {
		this.uri = uri;
	}

	public SortedMap<RDFNode, RDFNode> getContextAsSubject() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(
				new Comparators<RDFNode>().getToStringDefault());
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("p").addVar("o").addWhere(getEnclosedUri(), "?p",
				"?o");
		for (QuerySolution querySolution : sb.execute()) {
			rdfNodes.put(querySolution.get("p"), querySolution.get("s"));
		}
		return rdfNodes;
	}
	public SortedMap<RDFNode, RDFNode> getContextAsObject() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(
				new Comparators<RDFNode>().getToStringDefault());
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("s").addVar("p").addWhere("?s", "?p",
				getEnclosedUri());
		for (QuerySolution querySolution : sb.execute()) {
			rdfNodes.put(querySolution.get("p"), querySolution.get("s"));
		}
		return rdfNodes;
	}

	public SortedMap<RDFNode, RDFNode> getContextAsPredicate() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(
				new Comparators<RDFNode>().getToStringDefault());
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("st").addVar("ot")
				.addWhere("?s", getEnclosedUri(), "?o")
				.addWhere("?s", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "?st")
				.addWhere("?o", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "?ot");
		for (QuerySolution querySolution : sb.execute()) {
			rdfNodes.put(querySolution.get("p"), querySolution.get("s"));
		}
		return rdfNodes;
	}

	public String getEnclosedUri() {
		return "<" + uri + ">";
	}

	public String getUri() {
		return uri;
	}

	public void printContext() {
		System.out.println("Subject context of " + uri);
		for (QuerySolution soulution : getQuerySolutions(getEnclosedUri(), "?p", "?o", "p")) {
			System.out.println(" S  " + soulution.get("p") + "  " + soulution.get("o"));
		}
		System.out.println("Predicate context of " + uri);
		for (QuerySolution soulution : getQuerySolutions("?s", getEnclosedUri(), "?o", "s")) {
			System.out.println(" " + soulution.get("s") + "  P  " + soulution.get("o"));
		}
		System.out.println("Object context of " + uri);
		for (QuerySolution soulution : getQuerySolutions("?s", "?p", getEnclosedUri(), "s")) {
			System.out.println(" " + soulution.get("s") + "  " + soulution.get("p") + "  O");
		}
		System.out.println();
	}

	public void printContextExample() {
		System.out.println("Subject example context of " + uri);
		for (Entry<RDFNode, RDFNode> ocEntry : getContextAsSubject().entrySet()) {
			System.out.println(" S  " + ocEntry.getKey() + "  Ex:" + ocEntry.getValue());
		}
		System.out.println("Predicate example context of " + uri);
		for (Entry<RDFNode, RDFNode> ocEntry : getContextAsPredicate().entrySet()) {
			System.out.println(" Ex:" + ocEntry.getKey() + "  P  Ex:" + ocEntry.getValue());
		}
		System.out.println("Object example context of " + uri);
		for (Entry<RDFNode, RDFNode> ocEntry : getContextAsObject().entrySet()) {
			System.out.println(" Ex:" + ocEntry.getValue() + "  " + ocEntry.getKey() + "  O");
		}
		System.out.println();
	}
}
