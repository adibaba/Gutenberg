package de.adrianwilke.gutenberg.entities;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.Gutenberg;

/**
 * Base class for RDF nodes.
 * 
 * @author Adrian Wilke
 */
public class Node {

	private String uri;

	public Node(String uri) {
		this.uri = uri;
	}

	private Comparator<RDFNode> getComparator() {
		return (new Comparator<RDFNode>() {
			@Override
			public int compare(RDFNode o1, RDFNode o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
	}

	public String getEnclosedUri() {
		return "<" + uri + ">";
	}

	public SortedMap<RDFNode, RDFNode> getObjectContext() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(getComparator());
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("s").addVar("p").addWhere("?s", "?p",
				getEnclosedUri());
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			QuerySolution querySolution = results.next();
			rdfNodes.put(querySolution.get("p"), querySolution.get("s"));
		}
		return rdfNodes;
	}

	public SortedMap<RDFNode, RDFNode> getPredicateContext() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(getComparator());
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("st").addVar("ot")
				.addWhere("?s", getEnclosedUri(), "?o")
				.addWhere("?s", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "?st")
				.addWhere("?o", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "?ot");
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			QuerySolution querySolution = results.next();
			rdfNodes.put(querySolution.get("st"), querySolution.get("ot"));
		}
		return rdfNodes;
	}

	public SortedMap<RDFNode, RDFNode> getSubjectContext() {
		SortedMap<RDFNode, RDFNode> rdfNodes = new TreeMap<RDFNode, RDFNode>(getComparator());
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("p").addVar("o").addWhere(getEnclosedUri(),
				"?p", "?o");
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			QuerySolution querySolution = results.next();
			rdfNodes.put(querySolution.get("p"), querySolution.get("o"));
		}
		return rdfNodes;
	}

	public String getUri() {
		return uri;
	}

	protected static Comparator<RDFNode> getIdComparator() {
		return new Comparator<RDFNode>() {
			@Override
			public int compare(RDFNode o1, RDFNode o2) {
				Integer id1 = Integer.valueOf(o1.toString().substring(1 + o1.toString().lastIndexOf("/")));
				Integer id2 = Integer.valueOf(o2.toString().substring(1 + o2.toString().lastIndexOf("/")));
				return id1 - id2;
			}
		};
	}

	protected String getValue(Object s, Object p, Object o, String var) {
		SelectBuilder sb = new SelectBuilder();
		Triple triple = sb.makeTriplePath(s, p, o).asTriple();
		sb.setDistinct(true).addWhere(triple).addVar(var);
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		String value = null;
		if (results.hasNext()) {
			value = results.nextSolution().get(var).toString();
		}
		return value;
	}

	protected List<QuerySolution> getQuerySolutions(Object s, Object p, Object o, String order) {
		SelectBuilder sb = new SelectBuilder();
		Triple triple = sb.makeTriplePath(s, p, o).asTriple();
		sb.setDistinct(true).addWhere(triple).addOrderBy(order);
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		List<QuerySolution> solutions = new LinkedList<QuerySolution>();
		while (results.hasNext()) {
			solutions.add(results.nextSolution());
		}
		return solutions;
	}

	public void printContext() {
		System.out.println("Subject context of " + uri);
		for (Entry<RDFNode, RDFNode> ocEntry : getSubjectContext().entrySet()) {
			System.out.println(" S  " + ocEntry.getKey() + "  " + ocEntry.getValue());
		}
		System.out.println("Predicate context of " + uri);
		for (Entry<RDFNode, RDFNode> ocEntry : getPredicateContext().entrySet()) {
			System.out.println(" " + ocEntry.getKey() + "  P  " + ocEntry.getValue());
		}
		System.out.println("Object context of " + uri);
		for (Entry<RDFNode, RDFNode> ocEntry : getObjectContext().entrySet()) {
			System.out.println(" " + ocEntry.getValue() + "  " + ocEntry.getKey() + "  O");
		}
		System.out.println();
	}

	public void printExactContext() {
		System.out.println("Exact subject context of " + uri);
		for (QuerySolution soulution : getQuerySolutions(getEnclosedUri(), "?p", "?o", "p")) {
			System.out.println(" S  " + soulution.get("p") + "  " + soulution.get("o"));
		}
		System.out.println("Exact predicate context of " + uri);
		for (QuerySolution soulution : getQuerySolutions("?s", getEnclosedUri(), "?o", "s")) {
			System.out.println(" " + soulution.get("s") + "  P  " + soulution.get("o"));
		}
		System.out.println("Exact object context of " + uri);
		for (QuerySolution soulution : getQuerySolutions("?s", "?p", getEnclosedUri(), "s")) {
			System.out.println(" " + soulution.get("s") + "  " + soulution.get("p") + "  O");
		}
		System.out.println();
	}
}
