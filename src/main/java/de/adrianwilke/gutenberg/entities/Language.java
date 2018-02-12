package de.adrianwilke.gutenberg.entities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.arq.querybuilder.Order;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.Gutenberg;
import de.adrianwilke.gutenberg.Uris;

/**
 * Language literals.
 * 
 * @author Adrian Wilke
 */
public class Language extends Literal {

	private static final Model DEFAULT_MODEL = ModelFactory.createDefaultModel();
	private static final RDFDatatype TYPE_RFC4646 = TypeMapper.getInstance().getSafeTypeByName(Uris.DCTERMS_RFC4646);
	public static final org.apache.jena.rdf.model.Literal LANG_DE = DEFAULT_MODEL.createTypedLiteral("de",
			TYPE_RFC4646);
	public static final org.apache.jena.rdf.model.Literal LANG_EN = DEFAULT_MODEL.createTypedLiteral("en",
			TYPE_RFC4646);

	public static Language create(String typedLiteralString) {
		String lexicalForm = getLexicalForm(typedLiteralString);
		org.apache.jena.rdf.model.Literal typedLiteral = DEFAULT_MODEL.createTypedLiteral(lexicalForm, TYPE_RFC4646);
		return new Language(typedLiteral);
	}

	public static List<String> getLanguages() {
		List<String> languages = new LinkedList<String>();
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("language")
				.addWhere("?s", Uris.enclose(Uris.DCTERMS_LANGUAGE), "?l")
				.addWhere("?l", Uris.enclose(Uris.RDF_VALUE), "?language").addOrderBy("?language", Order.ASCENDING);
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			languages.add(results.next().getLiteral("language").toString());
		}
		return languages;
	}

	public Language(org.apache.jena.rdf.model.Literal rdfLiteral) {
		super(rdfLiteral);
	}

	public List<RDFNode> getResourcesByLanguage() {
		SelectBuilder sb = new SelectBuilder().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		Query query = sb.build();
		QueryExecution qexec = QueryExecutionFactory.create(query, Gutenberg.getInstance().getModel());
		List<RDFNode> resources = new LinkedList<RDFNode>();
		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			resources.add(results.nextSolution().get("item"));
		}
		Collections.sort(resources, Node.getIdComparator());
		return resources;
	}

	public List<Triple> getQueryTriples(String subjectVariableName) {
		List<Triple> triples = new LinkedList<Triple>();
		SelectBuilder sb = new SelectBuilder();
		triples.add(sb.makeTriplePath("?" + subjectVariableName, Uris.enclose(Uris.DCTERMS_LANGUAGE), "?language")
				.asTriple());
		triples.add(sb.makeTriplePath("?language", Uris.enclose(Uris.RDF_VALUE), getRdfLiteral()).asTriple());
		return triples;
	}
}