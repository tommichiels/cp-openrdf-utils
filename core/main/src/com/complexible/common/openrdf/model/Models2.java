package com.complexible.common.openrdf.model;

import com.complexible.common.openrdf.model.ModelIO;
import com.complexible.common.openrdf.model.Statements;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.rdf4j.common.iteration.Iteration;
import org.eclipse.rdf4j.common.iteration.Iterations;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

public final class Models2 {
	private Models2() {
		throw new AssertionError();
	}

	public static Collector<Statement, Model, Model> toModel() {
		return new Collector<Statement, Model, Model>() {

			@Override
			public Supplier<Model> supplier() {
				return Models2::newModel;
			}

			@Override
			public BiConsumer<Model, Statement> accumulator() {
				return Set::add;
			}

			@Override
			public BinaryOperator<Model> combiner() {
				return (theGraph, theOtherGraph) -> {
					theGraph.addAll((Collection) theOtherGraph);
					return theGraph;
				};
			}

			@Override
			public Function<Model, Model> finisher() {
				return Function.identity();
			}

			@Override
			public Set<Collector.Characteristics> characteristics() {
				return (Set<java.util.stream.Collector.Characteristics>) Sets.newHashSet((java.util.stream.Collector.Characteristics[]) new Collector.Characteristics[]{
						Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED});
			}
		};
	}

	public static Model of(Path thePath) throws IOException {
		return ModelIO.read((Path) thePath);
	}

	public static Model newModel() {
		return new LinkedHashModel();
	}

	public static Model newModel(Iterable<Statement> theStmts) {
		Model aModel = Models2.newModel();
		Iterables.addAll((Collection) aModel, theStmts);
		return aModel;
	}

	public static Model newModel(Iterator<Statement> theStmts) {
		Model aModel = Models2.newModel();
		Iterators.addAll((Collection) aModel, theStmts);
		return aModel;
	}

	public static Model newModel(Statement... theStmts) {
		Model aModel = Models2.newModel();
		Collections.addAll(aModel, theStmts);
		return aModel;
	}

	public static <E extends Exception, T extends Iteration<Statement, E>> Model newModel(T theStmts) throws Exception {
		Model aModel = Models2.newModel();
		Iterations.stream(theStmts).forEach(aModel::add);
		return aModel;
	}

	public static Model withContext(Iterable<Statement> theGraph, Resource theResource) {
		Model aModel = Models2.newModel();
		for (Statement aStmt : theGraph) {
			aModel.add((Statement) SimpleValueFactory.getInstance().createStatement(aStmt.getSubject(),
					aStmt.getPredicate(), aStmt.getObject(), theResource));
		}
		return aModel;
	}

	public static Model union(Model... theGraphs) {
		Model aModel = Models2.newModel();
		for (Model aGraph : theGraphs) {
			aModel.addAll((Collection) aGraph);
		}
		return aModel;
	}

	public static Optional<Value> getObject(Model theGraph, Resource theSubj, IRI thePred) {
		Iterator aCollection = theGraph.filter(theSubj, thePred, null, new Resource[0]).objects().iterator();
		if (aCollection.hasNext()) {
			return Optional.of((Value)aCollection.next());
		}
		return Optional.empty();
	}

	public static Optional<Literal> getLiteral(Model theGraph, Resource theSubj, IRI thePred) {
		Optional<Value> aVal = Models2.getObject(theGraph, theSubj, thePred);
		if (aVal.isPresent() && aVal.get() instanceof Literal) {
			return Optional.of((Literal) aVal.get());
		}
		return Optional.empty();
	}

	public static Optional<Resource> getResource(Model theGraph, Resource theSubj, IRI thePred) {
		Optional<Value> aVal = Models2.getObject(theGraph, theSubj, thePred);
		if (aVal.isPresent() && aVal.get() instanceof Resource) {
			return Optional.of((Resource) aVal.get());
		}
		return Optional.empty();
	}

	public static Optional<Boolean> getBooleanValue(Model theGraph, Resource theSubj, IRI thePred) {
		Optional<Literal> aLitOpt = Models2.getLiteral(theGraph, theSubj, thePred);
		if (!aLitOpt.isPresent()) {
			return Optional.empty();
		}
		Literal aLiteral = aLitOpt.get();
		if (aLiteral.getDatatype() != null && aLiteral.getDatatype().equals((Object) XMLSchema.BOOLEAN)
				|| aLiteral.getLabel().equalsIgnoreCase("true") || aLiteral.getLabel().equalsIgnoreCase("false")) {
			return Optional.of(Boolean.valueOf(aLiteral.getLabel()));
		}
		return Optional.empty();
	}

	public static boolean isList(Model theGraph, Resource theRes) {
		return theRes != null && (theRes.equals((Object) RDF.NIL) || theGraph.stream().filter(
				Statements.matches((Resource) theRes, (IRI) RDF.FIRST, (Value) null, (Resource[]) new Resource[0]))
				.findFirst().isPresent());
	}

	public static List<Value> asList(Model theGraph, Resource theRes) {
		ArrayList aList = Lists.newArrayList();
		Resource aListRes = theRes;
		while (aListRes != null) {
			Optional<Value> aFirst = Models2.getObject(theGraph, aListRes, RDF.FIRST);
			Optional<Resource> aRest = Models2.getResource(theGraph, aListRes, RDF.REST);
			if (aFirst.isPresent()) {
				aList.add(aFirst.get());
			}
			if (((Resource) aRest.orElse((Resource) RDF.NIL)).equals((Object) RDF.NIL)) {
				aListRes = null;
				continue;
			}
			aListRes = aRest.get();
		}
		return aList;
	}

	public static Model toList(List<Value> theResources) {
		Model aResult = Models2.newModel();
		Models2.toList(theResources, aResult);
		return aResult;
	}

	public static Resource toList(List<Value> theResources, Model theGraph) {
		BNode aCurr = SimpleValueFactory.getInstance().createBNode();
		int i = 0;
		BNode aHead = aCurr;
		for (Value aRes : theResources) {
			BNode aNext = SimpleValueFactory.getInstance().createBNode();
			theGraph.add((Resource) aCurr, RDF.FIRST, aRes, new Resource[0]);
			theGraph.add((Resource) aCurr, RDF.REST, (Value) (++i < theResources.size() ? aNext : RDF.NIL),
					new Resource[0]);
			aCurr = aNext;
		}
		return aHead;
	}

	public static Iterable<Resource> getTypes(Model theGraph, Resource theRes) {
		return (Iterable) theGraph.stream().filter(Statements.matches(theRes, RDF.TYPE, (Value) null, new Resource[0]))
				.map(Statement::getObject).map((theObject) -> {
					return (Resource) theObject;
				}).collect(Collectors.toList());
	}

	public static boolean isInstanceOf(Model theGraph, Resource theSubject, Resource theType) {
		return theGraph.contains(
				(Object) SimpleValueFactory.getInstance().createStatement(theSubject, RDF.TYPE, (Value) theType));
	}

}