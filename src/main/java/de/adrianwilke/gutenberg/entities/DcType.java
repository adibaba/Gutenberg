package de.adrianwilke.gutenberg.entities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.Uris;

/**
 * DCMI Metadata Terms.
 * 
 * http://purl.org/dc/terms/type
 * 
 * @author Adrian Wilke
 */
public class DcType extends Literal {

	private static final Model DEFAULT_MODEL = ModelFactory.createDefaultModel();

	public static final org.apache.jena.rdf.model.Literal TEXT = DEFAULT_MODEL.createLiteral("Text");
	public static final org.apache.jena.rdf.model.Literal SOUND = DEFAULT_MODEL.createLiteral("Sound");
	public static final org.apache.jena.rdf.model.Literal DATASET = DEFAULT_MODEL.createLiteral("Dataset");
	public static final org.apache.jena.rdf.model.Literal MOVING_IMAGE = DEFAULT_MODEL.createLiteral("MovingImage");
	public static final org.apache.jena.rdf.model.Literal IMAGE = DEFAULT_MODEL.createLiteral("Image");
	public static final org.apache.jena.rdf.model.Literal COLLECTION = DEFAULT_MODEL.createLiteral("Collection");
	public static final org.apache.jena.rdf.model.Literal STILL_IMAGE = DEFAULT_MODEL.createLiteral("StillImage");

	public DcType(org.apache.jena.rdf.model.Literal rdfLiteral) {
		super(rdfLiteral);
	}

	public static List<String> getDcTypes() {
		List<String> dcTypes = new LinkedList<String>();
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("typeValue")
				.addWhere("?s", Uris.enclose(Uris.DCTERMS_TYPE), "?dctype")
				.addWhere("?dctype", Uris.enclose(Uris.RDF_VALUE), "?typeValue");
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			dcTypes.add(results.nextSolution().getLiteral("typeValue").toString());
		}
		return dcTypes;
	}

	public Set<Resource> getResourcesByDcType() {
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		Set<Resource> resources = new HashSet<Resource>();
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			resources.add(results.nextSolution().getResource("item"));
		}
		return resources;
	}

	public List<Triple> getQueryTriples(String subjectVariableName) {
		List<Triple> triples = new LinkedList<Triple>();
		SelectBuilder sb = new SelectBuilder();
		triples.add(
				sb.makeTriplePath("?" + subjectVariableName, Uris.enclose(Uris.DCTERMS_TYPE), "?dctype").asTriple());
		triples.add(sb.makeTriplePath("?dctype", Uris.enclose(Uris.RDF_VALUE), getRdfLiteral()).asTriple());
		return triples;
	}

}