package com.complexible.common.openrdf.util;

import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.openrdf.util.ResourceBuilder;
import java.net.URI;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

public class ModelBuilder {
	private final Model mGraph = Models2.newModel();
	private final ValueFactory mValueFactory;

	public ModelBuilder() {
		this.mValueFactory = SimpleValueFactory.getInstance();
	}

	public ModelBuilder(ValueFactory theValueFactory) {
		this.mValueFactory = theValueFactory;
	}

	public Model model() {
		return Models2.newModel(this.mGraph);
	}

	public void reset() {
		this.mGraph.clear();
	}

	public ResourceBuilder iri(IRI theURI) {
		return new ResourceBuilder(this.mGraph, this.getValueFactory(),
				this.getValueFactory().createIRI(theURI.toString()));
	}

	public ResourceBuilder iri(String theURI) {
		return this.instance((IRI) null, (String) theURI);
	}

	public ResourceBuilder instance(IRI theType) {
		return this.instance(theType, (String) null);
	}

	public ResourceBuilder instance() {
		return this.instance((IRI) null, (String) ((String) null));
	}

	public ResourceBuilder instance(IRI theType, URI theURI) {
		return this.instance(theType, theURI.toString());
	}

	public ResourceBuilder instance(IRI theType, Resource theRes) {
		if (theType != null) {
			this.mGraph.add(theRes, RDF.TYPE, theType, new Resource[0]);
		}

		return new ResourceBuilder(this.mGraph, this.getValueFactory(), theRes);
	}

	public ResourceBuilder instance(IRI theType, String theURI) {
		Object aRes = theURI == null ? this.getValueFactory().createBNode() : this.getValueFactory().createIRI(theURI);
		if (theType != null) {
			this.mGraph.add((Resource) aRes, RDF.TYPE, theType, new Resource[0]);
		}

		return new ResourceBuilder(this.mGraph, this.getValueFactory(), (Resource) aRes);
	}

	public ValueFactory getValueFactory() {
		return this.mValueFactory;
	}
}