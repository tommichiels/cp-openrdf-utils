package com.complexible.common.openrdf.model;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.turtle.TurtleUtil;

public final class Statements {
	private Statements() {
		throw new AssertionError();
	}

	public static Predicate<Statement> subjectIs(Resource theSubj) {
		return (theStmt) -> {
			return Objects.equals(theStmt.getSubject(), theSubj);
		};
	}

	public static Predicate<Statement> predicateIs(IRI thePred) {
		return (theStmt) -> {
			return Objects.equals(theStmt.getPredicate(), thePred);
		};
	}

	public static Predicate<Statement> objectIs(Value theObj) {
		return (theStmt) -> {
			return Objects.equals(theStmt.getObject(), theObj);
		};
	}

	public static Predicate<Statement> contextIs(Resource theContext) {
		return (theStmt) -> {
			return Objects.equals(theStmt.getContext(), theContext);
		};
	}

	public static Predicate<Statement> objectIs(Class<? extends Value> theValue) {
		return (theStmt) -> {
			return theValue.isInstance(theStmt.getObject());
		};
	}

	public static Function<Statement, Statement> applyContext(Resource theContext) {
		return applyContext(theContext, SimpleValueFactory.getInstance());
	}

	public static Function<Statement, Statement> applyContext(Resource theContext, ValueFactory theValueFactory) {
		return (theStmt) -> {
			return Objects.equals(theContext, theStmt.getContext())
					? theStmt
					: theValueFactory.createStatement(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject(),
							theContext);
		};
	}

	public static Predicate<Statement> matches(Resource theSubject, IRI thePredicate, Value theObject,
			Resource... theContexts) {
		return (theStatement) -> {
			if (theSubject != null && !theSubject.equals(theStatement.getSubject())) {
				return false;
			} else if (thePredicate != null && !thePredicate.equals(theStatement.getPredicate())) {
				return false;
			} else if (theObject != null && !theObject.equals(theStatement.getObject())) {
				return false;
			} else if (theContexts != null && theContexts.length != 0) {
				Resource aContext = theStatement.getContext();
				Resource[] arg5 = theContexts;
				int arg6 = theContexts.length;

				for (int arg7 = 0; arg7 < arg6; ++arg7) {
					Resource aCxt = arg5[arg7];
					if (aCxt == null && aContext == null) {
						return true;
					}

					if (aCxt != null && aCxt.equals(aContext)) {
						return true;
					}
				}

				return false;
			} else {
				return true;
			}
		};
	}

	public static Function<Statement, Optional<Resource>> subjectOptional() {
		return (theStatement) -> {
			return Optional.of(theStatement.getSubject());
		};
	}

	public static Function<Statement, Optional<IRI>> predicateOptional() {
		return (theStatement) -> {
			return Optional.of(theStatement.getPredicate());
		};
	}

	public static Function<Statement, Optional<Value>> objectOptional() {
		return (theStatement) -> {
			return Optional.of(theStatement.getObject());
		};
	}

	public static Function<Statement, Optional<Literal>> objectAsLiteral() {
		return (theStatement) -> {
			return theStatement.getObject() instanceof Literal
					? Optional.of((Literal) theStatement.getObject())
					: Optional.empty();
		};
	}

	public static Function<Statement, Optional<Resource>> objectAsResource() {
		return (theStatement) -> {
			return theStatement.getObject() instanceof Resource
					? Optional.of((Resource) theStatement.getObject())
					: Optional.empty();
		};
	}

	public static Function<Statement, Optional<Resource>> contextOptional() {
		return (theStatement) -> {
			return theStatement.getContext() != null ? Optional.of(theStatement.getContext()) : Optional.empty();
		};
	}

	public static boolean isLiteralValid(Literal theLiteral) {
		String aTypeName;
		if (Literals.isLanguageLiteral(theLiteral)) {
			aTypeName = (String) theLiteral.getLanguage().get();
			if (!TurtleUtil.isLanguageStartChar(aTypeName.charAt(0))) {
				return false;
			}

			for (int e = 1; e < aTypeName.length(); ++e) {
				if (!TurtleUtil.isLanguageChar(aTypeName.charAt(e))) {
					return false;
				}
			}
		}

		if (theLiteral.getDatatype() != null
				&& theLiteral.getDatatype().getNamespace().equals("http://www.w3.org/2001/XMLSchema#")) {
			aTypeName = theLiteral.getDatatype().getLocalName();

			try {
				if (aTypeName.equals(XMLSchema.DATETIME.getLocalName())) {
					theLiteral.calendarValue();
				} else if (aTypeName.equals(XMLSchema.INT.getLocalName())) {
					theLiteral.intValue();
				} else if (aTypeName.equals(XMLSchema.FLOAT.getLocalName())) {
					theLiteral.floatValue();
				} else if (aTypeName.equals(XMLSchema.LONG.getLocalName())) {
					theLiteral.longValue();
				} else if (aTypeName.equals(XMLSchema.DOUBLE.getLocalName())) {
					theLiteral.doubleValue();
				} else if (aTypeName.equals(XMLSchema.SHORT.getLocalName())) {
					theLiteral.shortValue();
				} else if (aTypeName.equals(XMLSchema.BOOLEAN.getLocalName())) {
					theLiteral.booleanValue();
				} else if (aTypeName.equals(XMLSchema.BYTE.getLocalName())) {
					theLiteral.byteValue();
				} else if (aTypeName.equals(XMLSchema.DECIMAL.getLocalName())) {
					theLiteral.decimalValue();
				}
			} catch (Exception arg2) {
				return false;
			}
		}

		return true;
	}
}