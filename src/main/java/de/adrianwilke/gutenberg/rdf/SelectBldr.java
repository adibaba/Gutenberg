package de.adrianwilke.gutenberg.rdf;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.Order;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.lang.sparql_11.ParseException;

import de.adrianwilke.gutenberg.Gutenberg;

/**
 * Executable SelectBuilder.
 * 
 * @author Adrian Wilke
 */
public class SelectBldr extends SelectBuilder {

	@Override
	public SelectBldr addFilter(Expr expr) {
		super.addFilter(expr);
		return this;
	}

	@Override
	public SelectBldr addFilter(String s) throws ParseException {
		super.addFilter(s);
		return this;
	}

	@Override
	public SelectBldr addMinus(AbstractQueryBuilder<?> t) {
		super.addMinus(t);
		return this;
	}

	@Override
	public SelectBldr addOrderBy(Object orderBy, Order order) {
		super.addOrderBy(orderBy, order);
		return this;
	}

	@Override
	public SelectBldr addVar(Object var) {
		super.addVar(var);
		return this;
	}

	@Override
	public SelectBldr addWhere(Object s, Object p, Object o) {
		super.addWhere(s, p, o);
		return this;
	}

	@Override
	public SelectBldr addWhere(Triple t) {
		super.addWhere(t);
		return this;
	}

	public List<QuerySolution> execute() {
		List<QuerySolution> solutions = new LinkedList<QuerySolution>();
		ResultSet results = QueryExecutionFactory.create(build(), Gutenberg.getInstance().getModel()).execSelect();
		while (results.hasNext()) {
			solutions.add(results.next());
		}
		return solutions;
	}

	public List<RDFNode> execute(String returnVariable) {
		List<RDFNode> nodes = new LinkedList<RDFNode>();
		ResultSet results = QueryExecutionFactory.create(build(), Gutenberg.getInstance().getModel()).execSelect();
		while (results.hasNext()) {
			nodes.add(results.next().get(returnVariable));
		}
		return nodes;
	}

	public List<String> executeGetStrings(String returnVariable) {
		List<String> values = new LinkedList<String>();
		ResultSet results = QueryExecutionFactory.create(build(), Gutenberg.getInstance().getModel()).execSelect();
		while (results.hasNext()) {
			values.add(results.next().get(returnVariable).toString());
		}
		return values;
	}

	@Override
	public SelectBldr setDistinct(boolean state) {
		super.setDistinct(state);
		return this;
	}
}