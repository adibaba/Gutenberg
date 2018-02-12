package de.adrianwilke.gutenberg.entities;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.rdf.SelectBldr;

/**
 * Base class for RDF nodes.
 * 
 * @author Adrian Wilke
 */
public class Node {

	// TODO: Not used yet
	protected static Comparator<RDFNode> getIdComparator() {
		return new Comparator<RDFNode>() {
			public int compare(RDFNode o1, RDFNode o2) {
				Integer id1 = Integer.valueOf(o1.toString().substring(1 + o1.toString().lastIndexOf("/")));
				Integer id2 = Integer.valueOf(o2.toString().substring(1 + o2.toString().lastIndexOf("/")));
				return id1 - id2;
			}
		};
	}

	private String uri;

	public Node(String uri) {
		this.uri = uri;
	}

	private Comparator<RDFNode> getComparator() {
		return (new Comparator<RDFNode>() {
			public int compare(RDFNode rdfNode1, RDFNode rdfNode2) {
				return rdfNode1.toString().compareTo(rdfNode2.toString());
			}
		});
	}

	public SortedMap<RDFNode, RDFNode> getContextAsObject() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(getComparator());
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("s").addVar("p").addWhere("?s", "?p",
				getEnclosedUri());
		for (QuerySolution querySolution : sb.execute()) {
			rdfNodes.put(querySolution.get("p"), querySolution.get("s"));
		}
		return rdfNodes;
	}

	public SortedMap<RDFNode, RDFNode> getContextAsPredicate() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(getComparator());
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("st").addVar("ot")
				.addWhere("?s", getEnclosedUri(), "?o")
				.addWhere("?s", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "?st")
				.addWhere("?o", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "?ot");
		for (QuerySolution querySolution : sb.execute()) {
			rdfNodes.put(querySolution.get("p"), querySolution.get("s"));
		}
		return rdfNodes;
	}

	public SortedMap<RDFNode, RDFNode> getContextAsSubject() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(getComparator());
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("p").addVar("o").addWhere(getEnclosedUri(), "?p",
				"?o");
		for (QuerySolution querySolution : sb.execute()) {
			rdfNodes.put(querySolution.get("p"), querySolution.get("s"));
		}
		return rdfNodes;
	}

	public String getEnclosedUri() {
		return "<" + uri + ">";
	}

	protected List<QuerySolution> getQuerySolutions(Object s, Object p, Object o, String order) {
		SelectBldr sb = new SelectBldr();
		Triple triple = sb.makeTriplePath(s, p, o).asTriple();
		sb.setDistinct(true).addWhere(triple).addOrderBy(order);
		return sb.execute();
	}

	public String getUri() {
		return uri;
	}

	protected String getValue(Object s, Object p, Object o, String var) {
		SelectBldr sb = new SelectBldr();
		Triple triple = sb.makeTriplePath(s, p, o).asTriple();
		sb.setDistinct(true).addWhere(triple).addVar(var);
		List<QuerySolution> solutions = sb.execute();
		if (solutions.size() > 1) {
			System.err.println("Warning: More values available for " + triple);
		}
		if (solutions.isEmpty()) {
			return null;
		} else {
			return solutions.get(0).get(var).toString();
		}
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
