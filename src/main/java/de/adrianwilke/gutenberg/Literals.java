package de.adrianwilke.gutenberg;

import org.apache.jena.datatypes.RDFDatatype;

import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Literal constants.
 * 
 * @author Adrian Wilke
 */
public abstract class Literals {

	protected static final Model DEFAULT_MODEL = ModelFactory.createDefaultModel();
	protected static final RDFDatatype TYPE_RFC4646 = TypeMapper.getInstance().getSafeTypeByName(Uris.DCTERMS_RFC4646);

	public static final Literal LANG_DE = DEFAULT_MODEL.createTypedLiteral("de", TYPE_RFC4646);
	public static final Literal LANG_EN = DEFAULT_MODEL.createTypedLiteral("en", TYPE_RFC4646);
}