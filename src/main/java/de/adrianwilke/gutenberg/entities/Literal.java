package de.adrianwilke.gutenberg.entities;

/**
 * Base class for literals.
 * 
 * @author Adrian Wilke
 */
public class Literal {

	private org.apache.jena.rdf.model.Literal rdfLiteral;

	public Literal(org.apache.jena.rdf.model.Literal rdfLiteral) {
		this.rdfLiteral = rdfLiteral;
	}

	public org.apache.jena.rdf.model.Literal getRdfLiteral() {
		return rdfLiteral;
	}

	@Override
	public String toString() {
		return rdfLiteral.getLexicalForm();
	}

	public static String getLexicalForm(String typedLiteral) {
		return typedLiteral.substring(0, typedLiteral.indexOf("^^"));
	}

	public static String getDatatype(String typedLiteral) {
		return typedLiteral.substring(2 + typedLiteral.indexOf("^^"));
	}
}