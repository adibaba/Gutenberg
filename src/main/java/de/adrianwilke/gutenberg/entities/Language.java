package de.adrianwilke.gutenberg.entities;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.arq.querybuilder.Order;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.rdf.SelectBldr;
import de.adrianwilke.gutenberg.rdf.Uris;

/**
 * Language literals.
 * 
 * @author Adrian Wilke
 */
public class Language extends Literal {

	private static final RDFDatatype DATATYPE_RFC4646 = TypeMapper.getInstance()
			.getSafeTypeByName(Uris.DCTERMS_RFC4646);
	private static final Model DEFAULT_MODEL = ModelFactory.createDefaultModel();
	public static final org.apache.jena.rdf.model.Literal LANG_DE = createTypedLiteral("de");
	public static final org.apache.jena.rdf.model.Literal LANG_EN = createTypedLiteral("en");
	public static final org.apache.jena.rdf.model.Literal LANG_ES = createTypedLiteral("es");
	public static final org.apache.jena.rdf.model.Literal LANG_FI = createTypedLiteral("fi");
	public static final org.apache.jena.rdf.model.Literal LANG_FR = createTypedLiteral("fr");
	public static final org.apache.jena.rdf.model.Literal LANG_IT = createTypedLiteral("it");
	public static final org.apache.jena.rdf.model.Literal LANG_NL = createTypedLiteral("nl");
	public static final org.apache.jena.rdf.model.Literal LANG_PT = createTypedLiteral("pt");

	public static Language create(String typedLiteralString) {
		return new Language(getLexicalForm(typedLiteralString));
	}

	public static org.apache.jena.rdf.model.Literal createTypedLiteral(String lexicalForm) {
		return DEFAULT_MODEL.createTypedLiteral(lexicalForm, DATATYPE_RFC4646);
	}

	public static List<String> getLanguages() {
		List<String> languages = new LinkedList<String>();
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("language")
				.addWhere("?s", Uris.enclose(Uris.DCTERMS_LANGUAGE), "?l")
				.addWhere("?l", Uris.enclose(Uris.RDF_VALUE), "?language").addOrderBy("?language", Order.ASCENDING);
		for (RDFNode rdfNode : sb.execute("language")) {
			languages.add(rdfNode.toString());
		}
		return languages;
	}

	public Language(org.apache.jena.rdf.model.Literal rdfLiteral) {
		super(rdfLiteral);
	}

	public Language(String lexicalForm) {
		this(DEFAULT_MODEL.createTypedLiteral(lexicalForm, DATATYPE_RFC4646));
	}

	public List<Triple> getQueryTriples(String subjectVariableName) {
		List<Triple> triples = new LinkedList<Triple>();
		SelectBldr sb = new SelectBldr();
		triples.add(sb.makeTriplePath("?" + subjectVariableName, Uris.enclose(Uris.DCTERMS_LANGUAGE), "?language")
				.asTriple());
		triples.add(sb.makeTriplePath("?language", Uris.enclose(Uris.RDF_VALUE), getRdfLiteral()).asTriple());
		return triples;
	}

	public List<RDFNode> getResourcesByLanguage() {
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		return sb.execute("item");
	}
}