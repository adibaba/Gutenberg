package de.adrianwilke.gutenberg.entities;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

import de.adrianwilke.gutenberg.rdf.SelectBldr;
import de.adrianwilke.gutenberg.rdf.Uris;

/**
 * DCMI Metadata Terms dcterms:type.
 * 
 * http://purl.org/dc/terms/type
 * 
 * Description: "Recommended best practice is to use a controlled vocabulary
 * such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format,
 * physical medium, or dimensions of the resource, use the Format element."
 * 
 * Comment: "The nature or genre of the resource."
 * 
 * http://dublincore.org/2012/06/14/dcterms#type
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
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("typeValue")
				.addWhere("?s", Uris.enclose(Uris.DCTERMS_TYPE), "?dctype")
				.addWhere("?dctype", Uris.enclose(Uris.RDF_VALUE), "?typeValue");
		for (RDFNode rdfNode : sb.execute("typeValue")) {
			dcTypes.add(rdfNode.toString());
		}
		return dcTypes;
	}

	public List<RDFNode> getResourcesByDcType() {
		SelectBldr sb = new SelectBldr().setDistinct(true).addVar("item");
		for (Triple triple : getQueryTriples("item")) {
			sb.addWhere(triple);
		}
		return sb.execute("item");
	}

	/**
	 * Given variable will be from this DC type.
	 */
	public List<Triple> getQueryTriples(String subjectVariableName) {
		List<Triple> triples = new LinkedList<Triple>();
		SelectBldr sb = new SelectBldr();
		triples.add(
				sb.makeTriplePath("?" + subjectVariableName, Uris.enclose(Uris.DCTERMS_TYPE), "?dctype").asTriple());
		triples.add(sb.makeTriplePath("?dctype", Uris.enclose(Uris.RDF_VALUE), getRdfLiteral()).asTriple());
		return triples;
	}

}