package com.complexible.common.openrdf.util;

import com.complexible.common.openrdf.model.Models2;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

public class ResourceBuilder implements Value {
	private final Model mGraph;
	private final Resource mRes;
	private final ValueFactory mValueFactory;

	public ResourceBuilder(Resource theRes) {
		this(Models2.newModel(), SimpleValueFactory.getInstance(), theRes);
	}

	protected ResourceBuilder(Model theGraph, ValueFactory theValueFactory, Resource theRes) {
		this.mRes = theRes;
		this.mGraph = theGraph;
		this.mValueFactory = theValueFactory;
	}

	public ResourceBuilder addProperty(IRI theProperty, URI theURI) {
		return this.addProperty(theProperty, (Value) this.mValueFactory.createIRI(theURI.toString()));
	}

	public ResourceBuilder addProperty(IRI theProperty, List<? extends Value> theList) {
		BNode aListRes = this.mValueFactory.createBNode();
		this.mGraph.add(this.getResource(), theProperty, aListRes, new Resource[0]);
		Iterator aResIter = theList.iterator();

		while (aResIter.hasNext()) {
			this.mGraph.add(aListRes, RDF.FIRST, (Value) aResIter.next(), new Resource[0]);
			if (aResIter.hasNext()) {
				BNode aNextListElem = this.mValueFactory.createBNode();
				this.mGraph.add(aListRes, RDF.REST, aNextListElem, new Resource[0]);
				aListRes = aNextListElem;
			} else {
				this.mGraph.add(aListRes, RDF.REST, RDF.NIL, new Resource[0]);
			}
		}

		return this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Value theValue) {
		if (theValue != null) {
			this.mGraph.add(this.mRes, theProperty, theValue, new Resource[0]);
		}

		return this;
	}

	public Resource getResource() {
		return this.mRes;
	}

	public Model model() {
		return this.mGraph;
	}

	public ResourceBuilder addProperty(IRI theProperty, ResourceBuilder theBuilder) {
		if (theBuilder != null) {
			this.addProperty(theProperty, (Value) theBuilder.getResource());
			this.mGraph.addAll(Sets.newHashSet(theBuilder.mGraph));
		}

		return this;
	}

	public ResourceBuilder addProperty(IRI theProperty, String theValue) {
		return theValue != null
				? this.addProperty(theProperty, (Value) this.mValueFactory.createLiteral(theValue))
				: this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Integer theValue) {
		return theValue != null
				? this.addProperty(theProperty, (Value) this.mValueFactory.createLiteral(theValue.intValue()))
				: this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Long theValue) {
		return theValue != null
				? this.addProperty(theProperty, (Value) this.mValueFactory.createLiteral(theValue.longValue()))
				: this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Short theValue) {
		return theValue != null
				? this.addProperty(theProperty, (Value) this.mValueFactory.createLiteral(theValue.shortValue()))
				: this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Double theValue) {
		return theValue != null
				? this.addProperty(theProperty, (Value) this.mValueFactory.createLiteral(theValue.doubleValue()))
				: this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Date theValue) {
		if (theValue != null) {
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(theValue);

			try {
				return this.addProperty(theProperty,
						(Value) this.mValueFactory.createLiteral(
								DatatypeFactory.newInstance().newXMLGregorianCalendar(c).toXMLFormat(),
								XMLSchema.DATETIME));
			} catch (DatatypeConfigurationException arg4) {
				throw new IllegalArgumentException(arg4);
			}
		} else {
			return this;
		}
	}

	public ResourceBuilder addProperty(IRI theProperty, Float theValue) {
		return theValue != null
				? this.addProperty(theProperty, (Value) this.mValueFactory.createLiteral(theValue.floatValue()))
				: this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Boolean theValue) {
		return theValue != null
				? this.addProperty(theProperty, (Value) this.mValueFactory.createLiteral(theValue.booleanValue()))
				: this;
	}

	public ResourceBuilder addProperty(IRI theProperty, Object theObject) {
		if (theObject == null) {
			return this;
		} else if (theObject instanceof ResourceBuilder) {
			return this.addProperty(theProperty, (ResourceBuilder) theObject);
		} else if (theObject instanceof Boolean) {
			return this.addProperty(theProperty, (Boolean) theObject);
		} else if (theObject instanceof Long) {
			return this.addProperty(theProperty, (Long) theObject);
		} else if (theObject instanceof Integer) {
			return this.addProperty(theProperty, (Integer) theObject);
		} else if (theObject instanceof Short) {
			return this.addProperty(theProperty, (Short) theObject);
		} else if (theObject instanceof Float) {
			return this.addProperty(theProperty, (Float) theObject);
		} else if (theObject instanceof Date) {
			return this.addProperty(theProperty, (Date) theObject);
		} else if (theObject instanceof Double) {
			return this.addProperty(theProperty, (Double) theObject);
		} else if (theObject instanceof Value) {
			return this.addProperty(theProperty, (Value) theObject);
		} else if (theObject instanceof List) {
			return this.addProperty(theProperty, (List) theObject);
		} else if (theObject instanceof URI) {
			return this.addProperty(theProperty, (URI) theObject);
		} else if (theObject instanceof String) {
			return this.addProperty(theProperty, theObject.toString());
		} else {
			throw new IllegalArgumentException(theObject + " is not a supported type");
		}
	}

	public ResourceBuilder addLabel(String theLabel) {
		return this.addProperty(RDFS.LABEL, theLabel);
	}

	public ResourceBuilder addType(IRI theType) {
		return this.addProperty(RDF.TYPE, (Value) theType);
	}

	public String stringValue() {
		return this.mRes.stringValue();
	}
}