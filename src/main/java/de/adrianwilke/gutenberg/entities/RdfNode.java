package de.adrianwilke.gutenberg.entities;

import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolution;

import de.adrianwilke.gutenberg.rdf.SelectBldr;

/**
 * Base class for RDF nodes.
 * 
 * @author Adrian Wilke
 */
public class RdfNode {

	protected List<QuerySolution> getQuerySolutions(Object s, Object p, Object o, String order) {
		SelectBldr sb = new SelectBldr();
		Triple triple = sb.makeTriplePath(s, p, o).asTriple();
		sb.setDistinct(true).addWhere(triple).addOrderBy(order);
		return sb.execute();
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
}