package org.mm.renderer.owlapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mm.core.ReferenceDirectives;
import org.mm.core.ReferenceType;
import org.mm.renderer.InternalRendererException;
import org.mm.renderer.RendererException;
import org.mm.ss.SpreadsheetLocation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class OWLAPIObjectHandler
{
	private final Map<String, Map<String, OWLEntity>> createdOWLEntitiesUsingLabel; // Map of namespace to map of rdfs:label to rdf:ID

	private final Map<String, Map<SpreadsheetLocation, OWLEntity>> createdOWLEntitiesUsingLocation; // Map of namespace to map of location to rdf:ID

	private final OWLOntology ontology;
	
	private final OWLDataFactory owlDataFactory;

	private final PrefixManager prefixManager = new DefaultPrefixManager();

	public OWLAPIObjectHandler(OWLOntology ontology)
	{
		this.ontology = ontology;
		this.createdOWLEntitiesUsingLabel = new HashMap<>();
		this.createdOWLEntitiesUsingLocation = new HashMap<>();
		this.owlDataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		
		// Assemble the prefix manager for the given ontology
		OWLDocumentFormat format = ontology.getOWLOntologyManager().getOntologyFormat(ontology);
		if (format.isPrefixOWLOntologyFormat()) {
			prefixManager.copyPrefixesFrom(format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap());
		}
	}

	public IRI getQualifiedName(String shortName)
	{
		return prefixManager.getIRI(shortName);
	}

	public OWLDeclarationAxiom getOWLDeclarationAxiom(OWLEntity entity)
	{
		return owlDataFactory.getOWLDeclarationAxiom(entity);
	}

	public OWLClass getOWLClass(String shortName)
	{
		return getOWLClass(getQualifiedName(shortName));
	}

	public OWLClass getOWLClass(IRI iri)
	{
		return owlDataFactory.getOWLClass(iri);
	}

	public OWLNamedIndividual getOWLNamedIndividual(String shortName) throws RendererException
	{
		return getOWLNamedIndividual(getQualifiedName(shortName));
	}

	public OWLNamedIndividual getOWLNamedIndividual(IRI iri)
	{
		return owlDataFactory.getOWLNamedIndividual(iri);
	}

	public OWLObjectProperty getOWLObjectProperty(String shortName)
	{
		return getOWLObjectProperty(getQualifiedName(shortName));
	}

	public OWLObjectProperty getOWLObjectProperty(IRI iri)
	{
		return owlDataFactory.getOWLObjectProperty(iri);
	}

	public OWLDataProperty getOWLDataProperty(String shortName)
	{
		return getOWLDataProperty(getQualifiedName(shortName));
	}

	public OWLDataProperty getOWLDataProperty(IRI iri)
	{
		return owlDataFactory.getOWLDataProperty(iri);
	}

	public OWLAnnotationProperty getOWLAnnotationProperty(String shortName)
	{
		return getOWLAnnotationProperty(getQualifiedName(shortName));
	}

	public OWLAnnotationProperty getOWLAnnotationProperty(IRI iri)
	{
		return owlDataFactory.getOWLAnnotationProperty(iri);
	}

	public OWLAnnotationValue getOWLAnnotationValue(String value)
	{
		return owlDataFactory.getOWLLiteral(value);
	}

	public OWLAnnotationValue getOWLAnnotationValue(float value)
	{
		return owlDataFactory.getOWLLiteral(value);
	}

	public OWLAnnotationValue getOWLAnnotationValue(int value)
	{
		return owlDataFactory.getOWLLiteral(value+"", OWL2Datatype.XSD_INT);
	}

	public OWLAnnotationValue getOWLAnnotationValue(boolean value)
	{
		return owlDataFactory.getOWLLiteral(value);
	}

	public OWLDatatype getOWLDatatype(String shortName)
	{
		return owlDataFactory.getOWLDatatype(getQualifiedName(shortName));
	}

	public OWLLiteral getOWLLiteralString(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_STRING);
	}

	public OWLLiteral getOWLLiteralBoolean(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_BOOLEAN);
	}

	public OWLLiteral getOWLLiteralDouble(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_DOUBLE);
	}

	public OWLLiteral getOWLLiteralFloat(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_FLOAT);
	}

	public OWLLiteral getOWLLiteralLong(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_LONG);
	}

	public OWLLiteral getOWLLiteralInteger(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_INT);
	}

	public OWLLiteral getOWLLiteralShort(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_SHORT);
	}

	public OWLLiteral getOWLLiteralByte(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_BYTE);
	}

	public OWLLiteral getOWLLiteralDateTime(String value)
	{
		return owlDataFactory.getOWLLiteral(value, OWL2Datatype.XSD_DATE_TIME);
	}

	public OWLLiteral getOWLLiteralDate(String value)
	{
		return owlDataFactory.getOWLLiteral(value, owlDataFactory.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
	}

	public OWLLiteral getOWLLiteralTime(String value)
	{
		return owlDataFactory.getOWLLiteral(value, owlDataFactory.getOWLDatatype(XSDVocabulary.TIME.getIRI()));
	}

	public OWLLiteral getOWLLiteralDuration(String value)
	{
		return owlDataFactory.getOWLLiteral(value, owlDataFactory.getOWLDatatype(XSDVocabulary.DURATION.getIRI()));
	}

	public OWLObjectComplementOf getOWLObjectComplementOf(OWLClassExpression ce)
	{
		return owlDataFactory.getOWLObjectComplementOf(ce);
	}

	public OWLObjectIntersectionOf getOWLObjectIntersectionOf(Set<OWLClassExpression> ces)
	{
		return owlDataFactory.getOWLObjectIntersectionOf(ces);
	}

	public OWLObjectUnionOf getOWLObjectUnionOf(Set<OWLClassExpression> ces)
	{
		return owlDataFactory.getOWLObjectUnionOf(ces);
	}

	public OWLObjectOneOf getOWLObjectOneOf(Set<OWLNamedIndividual> inds)
	{
		return owlDataFactory.getOWLObjectOneOf(inds);
	}

	public OWLSubClassOfAxiom getOWLSubClassOfAxiom(OWLClassExpression child, OWLClassExpression parent)
	{
		return owlDataFactory.getOWLSubClassOfAxiom(child, parent);
	}

	public OWLSubObjectPropertyOfAxiom getOWLSubObjectPropertyOfAxiom(OWLObjectPropertyExpression child, OWLObjectPropertyExpression parent)
	{
		return owlDataFactory.getOWLSubObjectPropertyOfAxiom(child, parent);
	}

	public OWLSubDataPropertyOfAxiom getOWLSubDataPropertyOfAxiom(OWLDataPropertyExpression child, OWLDataPropertyExpression parent)
	{
		return owlDataFactory.getOWLSubDataPropertyOfAxiom(child, parent);
	}

	public OWLEquivalentClassesAxiom getOWLEquivalentClassesAxiom(Set<OWLClassExpression> ces)
	{
		return owlDataFactory.getOWLEquivalentClassesAxiom(ces);
	}

	public OWLEquivalentClassesAxiom getOWLEquivalentClassesAxiom(OWLClassExpression ce1, OWLClassExpression ce2)
	{
		return owlDataFactory.getOWLEquivalentClassesAxiom(ce1, ce2);
	}

	public OWLAnnotationAssertionAxiom getOWLAnnotationAssertionAxiom(OWLAnnotationProperty ap, OWLEntity entity, OWLLiteral value)
	{
		return getOWLAnnotationAssertionAxiom(ap, entity.getIRI(), value);
	}

	public OWLAnnotationAssertionAxiom getOWLAnnotationAssertionAxiom(OWLAnnotationProperty ap, OWLAnnotationSubject as, OWLAnnotationValue value)
	{
		return owlDataFactory.getOWLAnnotationAssertionAxiom(ap, as, value);
	}

	public OWLClassAssertionAxiom getOWLClassAssertionAxiom(OWLClassExpression ce, OWLNamedIndividual ind)
	{
		return owlDataFactory.getOWLClassAssertionAxiom(ce, ind);
	}

	public OWLObjectPropertyAssertionAxiom getOWLObjectPropertyAssertionAxiom(OWLObjectProperty op, OWLIndividual source, OWLIndividual target)
	{
		return owlDataFactory.getOWLObjectPropertyAssertionAxiom(op, source, target);
	}

	public OWLDataPropertyAssertionAxiom getOWLDataPropertyAssertionAxiom(OWLDataProperty dp, OWLIndividual source, OWLLiteral target)
	{
		return owlDataFactory.getOWLDataPropertyAssertionAxiom(dp, source, target);
	}

	public OWLSameIndividualAxiom getOWLSameIndividualAxiom(OWLIndividual ind1, OWLIndividual ind2)
	{
		return owlDataFactory.getOWLSameIndividualAxiom(ind1, ind2);
	}

	public OWLDifferentIndividualsAxiom getOWLDifferentIndividualsAxiom(OWLIndividual ind1, OWLIndividual ind2)
	{
		return owlDataFactory.getOWLDifferentIndividualsAxiom(ind1, ind2);
	}

	public OWLObjectAllValuesFrom getOWLObjectAllValuesFrom(OWLObjectProperty op, OWLClassExpression ce)
	{
		return owlDataFactory.getOWLObjectAllValuesFrom(op, ce);
	}

	public OWLObjectSomeValuesFrom getOWLObjectSomeValuesFrom(OWLObjectProperty op, OWLClassExpression ce)
	{
		return owlDataFactory.getOWLObjectSomeValuesFrom(op, ce);
	}

	public OWLDataAllValuesFrom getOWLDataAllValuesFrom(OWLDataProperty dp, OWLDatatype dt)
	{
		return owlDataFactory.getOWLDataAllValuesFrom(dp, dt);
	}

	public OWLDataSomeValuesFrom getOWLDataSomeValuesFrom(OWLDataProperty dp, OWLDatatype dt)
	{
		return owlDataFactory.getOWLDataSomeValuesFrom(dp, dt);
	}

	public OWLObjectExactCardinality getOWLObjectExactCardinality(int cardinality, OWLObjectProperty op)
	{
		return owlDataFactory.getOWLObjectExactCardinality(cardinality, op);
	}

	public OWLDataExactCardinality getOWLDataExactCardinality(int cardinality, OWLDataProperty dp)
	{
		return owlDataFactory.getOWLDataExactCardinality(cardinality, dp);
	}

	public OWLObjectMaxCardinality getOWLObjectMaxCardinality(int cardinality, OWLObjectProperty op)
	{
		return owlDataFactory.getOWLObjectMaxCardinality(cardinality, op);
	}

	public OWLDataMaxCardinality getOWLDataMaxCardinality(int cardinality, OWLDataProperty dp)
	{
		return owlDataFactory.getOWLDataMaxCardinality(cardinality, dp);
	}

	public OWLObjectMinCardinality getOWLObjectMinCardinality(int cardinality, OWLObjectProperty op)
	{
		return owlDataFactory.getOWLObjectMinCardinality(cardinality, op);
	}

	public OWLDataMinCardinality getOWLDataMinCardinality(int cardinality, OWLDataProperty dp)
	{
		return owlDataFactory.getOWLDataMinCardinality(cardinality, dp);
	}

	public OWLObjectHasValue getOWLObjectHasValue(OWLObjectProperty op, OWLNamedIndividual ind)
	{
		return owlDataFactory.getOWLObjectHasValue(op, ind);
	}

	public OWLDataHasValue getOWLDataHasValue(OWLDataProperty dp, OWLLiteral lit)
	{
		return owlDataFactory.getOWLDataHasValue(dp, lit);
	}

	public boolean isOWLClass(String shortName)
	{
		return this.ontology.containsClassInSignature(getQualifiedName(shortName));
	}

	public boolean isOWLNamedIndividual(String shortName)
	{
		return this.ontology.containsIndividualInSignature(getQualifiedName(shortName));
	}

	public boolean isOWLObjectProperty(String shortName)
	{
		return this.ontology.containsObjectPropertyInSignature(getQualifiedName(shortName));
	}

	public boolean isOWLDataProperty(String shortName)
	{
		return this.ontology.containsDataPropertyInSignature(getQualifiedName(shortName));
	}

	public boolean isOWLAnnotationProperty(String shortName)
	{
		return this.ontology.containsAnnotationPropertyInSignature(getQualifiedName(shortName));
	}

	public boolean isOWLDatatype(String shortName)
	{
		return this.ontology.containsDatatypeInSignature(getQualifiedName(shortName));
	}

	public boolean isOWLObjectProperty(OWLProperty property)
	{
		return this.ontology.containsObjectPropertyInSignature(property.getIRI());
	}

	public boolean isOWLDataProperty(OWLProperty property)
	{
		return this.ontology.containsDataPropertyInSignature(property.getIRI());
	}

	public boolean isOWLAnnotationProperty(OWLAnnotationProperty property)
	{
		return this.ontology.containsAnnotationPropertyInSignature(property.getIRI());
	}

	public String getNamespaceForPrefix(String prefix) throws RendererException
	{
		
		IRI iri = prefixManager.getIRI(prefix);
		if (iri != null) {
			return iri.toString();
		}
		throw new RendererException("Namespace for prefix '" + prefix + "' cannot be found!");
	}

	// TODO Use Optional return value
	public OWLEntity createOrResolveOWLEntity(SpreadsheetLocation location, String locationValue,
			ReferenceType referenceType, String rdfID, String rdfsLabel, String defaultNamespace, String language,
			ReferenceDirectives referenceDirectives) throws RendererException
	{
		OWLEntity createdOrResolvedOWLEntity;

		if (referenceDirectives.usesLocationWithDuplicatesEncoding()) {
			createdOrResolvedOWLEntity = resolveOWLEntityWithDuplicatesEncoding(referenceType, defaultNamespace);
		} else if (referenceDirectives.usesLocationEncoding()) {
			createdOrResolvedOWLEntity = resolveOWLEntityWithLocationEncoding(location, defaultNamespace, referenceType);
		} else { // Uses rdf:ID or rdfs:label encoding
			boolean isEmptyRDFIDValue = rdfID == null || rdfID.isEmpty();
			boolean isEmptyRDFSLabelText = rdfsLabel == null || rdfsLabel.isEmpty();

			if (isEmptyRDFIDValue && referenceDirectives.actualEmptyRDFIDDirectiveIsSkipIfEmpty()) {
				// log "skipping because of empty rdf:ID"
				createdOrResolvedOWLEntity = null;
			} else if (isEmptyRDFSLabelText && referenceDirectives.actualEmptyRDFSLabelDirectiveIsSkipIfEmpty()) {
				createdOrResolvedOWLEntity = null;
				// log "skipping because of empty rdfs:label"
			} else {
				if (isEmptyRDFIDValue && isEmptyRDFSLabelText) { // Empty rdf:ID and rdfs:label
					createdOrResolvedOWLEntity = createOrResolveOWLEntityWithEmptyIDAndEmptyLabel(location, defaultNamespace,
							referenceType);
				} else { // One or both of rdf:ID and label have values
					if (isEmptyRDFIDValue) { // rdf:ID is empty, label must then have a value. Use label to resolve possible existing entity.
						createdOrResolvedOWLEntity = createOrResolveOWLEntityWithEmptyIDAndNonEmptyLabel(location, referenceType,
								defaultNamespace, rdfsLabel, language, referenceDirectives);
					} else { // Has an rdf:ID value, may or may not have a label value
						if (isEmptyRDFSLabelText) { // Has a value for rdf:ID and an empty rdfs:label value
							createdOrResolvedOWLEntity = createOrResolveOWLEntityWithNonEmptyIDAndEmptyLabel(location, rdfID,
									referenceType, defaultNamespace, referenceDirectives);
						} else { // Has rdf:ID and rdfs:label values. Use rdf:ID to resolve resolve possible existing entity.
							createdOrResolvedOWLEntity = createOrResolveOWLEntityWithNonEmptyIDAndNonEmptyLabel(location, rdfID,
									rdfsLabel, referenceType, defaultNamespace, language, referenceDirectives);
						}
					}
				}
			}
		}

		if (createdOrResolvedOWLEntity == null) {
			// log "no " + referenceType + " created"
		} else {
			// log "resolved " + referenceType + " " + createdOrResolvedOWLEntity
		}

		return createdOrResolvedOWLEntity;
	}

	// TODO Use Optional return value
	private OWLEntity createOrResolveOWLEntityWithNonEmptyIDAndNonEmptyLabel(SpreadsheetLocation location, String rdfID,
			String labelText, ReferenceType referenceType, String namespace, String language,
			ReferenceDirectives referenceDirectives) throws RendererException
	{
		OWLEntity resolvedOWLEntity;

		if (shouldCreateOrResolveOWLEntityWithRDFID(namespace, rdfID, referenceDirectives)
				&& shouldCreateOrResolveOWLEntityWithRDFSLabel(labelText, language, referenceDirectives)) {
			if (hasOWLEntityBeenCreatedAtLocation(location,
					namespace)) { // Has an entity been created at this location already?
				resolvedOWLEntity = getCreatedEntityRDFIDAtLocation(referenceType, location, namespace);
				// log"--using existing " + referenceType + " " + resolvedOWLEntity + " created at this location"
			} else { // No existing entity created at this location -- create one. If an existing entity has this rdf:ID it will be reused.
				resolvedOWLEntity = createOWLEntity(location, namespace, rdfID, referenceType,
						referenceDirectives); // If entity exists, it will be retrieved.
				// log "--processReference: creating/resolving " + referenceType + " using rdf:ID " + rdfID
				recordCreatedOWLEntityRDFIDAtLocation(referenceType, location, namespace, resolvedOWLEntity);
			}
			// TODO Look at this because it allows a back door way to add a possibly duplicate label
			addRDFSLabelToOWLEntity(referenceType, resolvedOWLEntity, namespace, labelText, language);
		} else
			resolvedOWLEntity = null;

		return resolvedOWLEntity;
	}

	// TODO Use Optional return value
	private OWLEntity createOrResolveOWLEntityWithNonEmptyIDAndEmptyLabel(SpreadsheetLocation location, String rdfID,
			ReferenceType referenceType, String namespace, ReferenceDirectives referenceDirectives) throws RendererException
	{
		OWLEntity resolvedOWLEntity;

		if (shouldCreateOrResolveOWLEntityWithRDFID(namespace, rdfID, referenceDirectives)) {
			if (hasOWLEntityBeenCreatedAtLocation(location,
					namespace)) { // Has an entity for this location been created already?
				resolvedOWLEntity = getCreatedEntityRDFIDAtLocation(referenceType, location, namespace);
				// log using existing " + referenceType + " " + resolvedOWLEntity + " created at this location"
			} else { // No existing entity created at this location -- create one. If an existing entity has this rdf:ID it will be reused.
				resolvedOWLEntity = createOWLEntity(location, namespace, rdfID, referenceType,
						referenceDirectives); // If entity exists, it will be retrieved

				// log "--processReference: creating/resolving " + referenceType + " using rdf:ID " + rdfID
				recordCreatedOWLEntityRDFIDAtLocation(referenceType, location, namespace, resolvedOWLEntity);
			}
		} else
			resolvedOWLEntity = null;

		return resolvedOWLEntity;
	}

	// TODO Use Optional return value
	private OWLEntity createOrResolveOWLEntityWithEmptyIDAndNonEmptyLabel(SpreadsheetLocation locationNode,
			ReferenceType referenceType, String defaultNamespace, String labelText, String language,
			ReferenceDirectives referenceDirectives) throws RendererException
	{
		OWLEntity resolvedOWLEntity;

		if (shouldCreateOrResolveOWLEntityWithRDFSLabel(labelText, language, referenceDirectives)) {

			if (hasOWLEntityBeenCreatedWithLabel(defaultNamespace, labelText,
					language)) { // Have already created an entity using this label
				resolvedOWLEntity = getCreatedOWLEntityWithRDFSLabel(referenceType, defaultNamespace, labelText,
						language); // Find the existing one
				// log "using existing " + referenceType + " " + resolvedOWLEntity + " with rdfs:label " + labelText
				if (language == null || language.isEmpty()) {
					// log
				} else {
					// log xml:lang \"" + language + "\""
				}
			} else if (isExistingOWLEntityWithRDFSLabelAndLanguage(labelText, language)) {
				resolvedOWLEntity = getOWLEntityWithRDFSLabel(labelText, language);
				// log "using existing ontology " + referenceType + " " + resolvedOWLEntity+ " with rdfs:label " + labelText
			} else { // No existing entity with this label - create one
				// Has an entity for this location in the specified namespace been created already?
				if (hasOWLEntityBeenCreatedAtLocation(locationNode, defaultNamespace)) {
					resolvedOWLEntity = getCreatedEntityRDFIDAtLocation(referenceType, locationNode, defaultNamespace);
					// log "using existing " + referenceType + " " + resolvedOWLEntity
					// log " created at this location with namespace " + defaultNamespace
				} else { // No existing entity created at this location - create one with an auto-generated rdf:ID
					resolvedOWLEntity = createOWLEntity(referenceType, defaultNamespace);
					// TODO: call createOWLEntityUsingRDFSLabel when it is fixed to create in proper namespace
					// log "creating " + referenceType
					recordCreatedOWLEntityRDFIDAtLocation(referenceType, locationNode, defaultNamespace, resolvedOWLEntity);
				}
				addRDFSLabelToOWLEntity(referenceType, resolvedOWLEntity, defaultNamespace, labelText, language);
			}
		} else
			resolvedOWLEntity = null;

		return resolvedOWLEntity;
	}

	// TODO Use Optional return value
	private OWLEntity getOWLEntityWithRDFSLabel(String labelText, String language) throws RendererException
	{
		// TODO Confirm with Martin
		if (language != null && "*".equals(language)) {
			return getOWLEntityWithRDFSLabel(labelText).iterator().next(); // Match on any language or none
		} else if (language != null && "+".equals(language)) {
			return getOWLEntityWithRDFSLabelAndAtLeastOneLanguage(labelText).iterator().next(); // Match on at least one language
		} else {
			return getOWLEntityWithRDFSLabelAndLanguage(labelText, language).iterator().next(); // Match on specific language
		}
	}

	private boolean shouldCreateOrResolveOWLEntityWithRDFSLabel(String labelText, String language,
			ReferenceDirectives referenceDirectives) throws RendererException
	{
		if (isExistingOWLEntityWithRDFSLabelAndLanguage(labelText, language)) {
			if (referenceDirectives.actualIfExistsDirectiveIsError())
				throwOWLEntityExistsWithLabelException(labelText, language);
			else if (referenceDirectives.actualIfExistsDirectiveIsWarning())
				warnOWLEntityExistsWithRDFSLabel(labelText, language);
			else if (referenceDirectives.actualIfOWLEntityExistsDirectiveIsSkip()) {
				// log "skipping because OWL entity with this label already exists"
				return false;
			}
			// If setting is MM_RESOLVE_IF_EXISTS we resolve it.
		} else { // No existing entity
			if (referenceDirectives.actualIfOWLEntityDoesNotExistDirectiveIsError())
				throwNoExistingOWLEntityWithRDFSLabelException(labelText, language);
			else if (referenceDirectives.actualIfOWLEntityDoesNotExistDirectiveIsWarning())
				warnNoExistingOWLEntityWithRDFSLabel(labelText, language);
			else if (referenceDirectives.actualIfOWLEntityDoesNotExistDirectiveIsSkip()) {
				// log "skipping because OWL entity with this label does not exist");
				return false;
			}
			// If setting is MM_CREATE_IF_NOT_EXISTS we create it.
		}
		return true;
	}

	// TODO Code is currently very sloppy about fully qualified rdf:IDs vs. fragments.
	private boolean shouldCreateOrResolveOWLEntityWithRDFID(String namespace, String rdfID,
			ReferenceDirectives referenceDirectives) throws RendererException
	{
		if (isExistingOWLEntityWithRDFID(rdfID)) {
			if (referenceDirectives.actualIfExistsDirectiveIsError())
				throwOWLEntityExistsWithRDFIDException(namespace, rdfID);
			else if (referenceDirectives.actualIfExistsDirectiveIsWarning())
				warnOWLEntityExistsWithRDFID(namespace, rdfID);
			else if (referenceDirectives.actualIfOWLEntityExistsDirectiveIsSkip()) {
				return false;
			}
			// If setting is MM_RESOLVE_IF_EXISTS we resolve it.
		} else { // No existing entity
			if (referenceDirectives.actualIfOWLEntityDoesNotExistDirectiveIsError())
				throwNoExistingOWLEntityWithRDFIDException(namespace, rdfID);
			else if (referenceDirectives.actualIfOWLEntityDoesNotExistDirectiveIsWarning())
				warnNoExistingOWLEntityWithRDFID(namespace, rdfID);
			else if (referenceDirectives.actualIfOWLEntityDoesNotExistDirectiveIsSkip())
				return false;
			// If setting is MM_CREATE_IF_NOT_EXISTS we create it.
		}
		return true;
	}

	// TODO Use Optional return value
	private OWLEntity resolveOWLEntityWithDuplicatesEncoding(ReferenceType referenceType, String namespace)
			throws RendererException
	{
		// Create entity with an auto-generated rdf:ID
		OWLEntity resolvedOWLEntity = createOWLEntity(referenceType, namespace);
		// log "creating " + referenceType + " at this location using location with duplicates encoding"
		return resolvedOWLEntity;
	}

	// TODO Use Optional return value
	private OWLEntity resolveOWLEntityWithLocationEncoding(SpreadsheetLocation location, String namespace,
			ReferenceType referenceType) throws RendererException
	{
		OWLEntity resolvedOWLEntity;

		if (hasOWLEntityBeenCreatedAtLocation(location,
				namespace)) { // Has an entity of this type for this location been created already?
			resolvedOWLEntity = getCreatedEntityRDFIDAtLocation(referenceType, location, namespace);
			// log "using existing " + referenceType + " " + resolvedOWLEntity
			// log " created at this location using location encoding"
		} else { // Create entity with an auto-generated rdf:ID
			resolvedOWLEntity = createOWLEntity(referenceType, namespace);
			recordCreatedOWLEntityRDFIDAtLocation(referenceType, location, namespace, resolvedOWLEntity);
			// log "--processReference: creating " + referenceType + " at this location using location encoding"
		}
		return resolvedOWLEntity;
	}

	// TODO Use Optional return value
	private OWLEntity createOrResolveOWLEntityWithEmptyIDAndEmptyLabel(SpreadsheetLocation location, String namespace,
			ReferenceType referenceType) throws RendererException
	{
		OWLEntity resolvedOWLEntity;

		if (hasOWLEntityBeenCreatedAtLocation(location,
				namespace)) { // Has an entity for this location been created already?
			resolvedOWLEntity = getCreatedEntityRDFIDAtLocation(referenceType, location, namespace);
			// log "using existing " + referenceType + " " + resolvedOWLEntity + " created at this location"
		} else { // No existing entity created at this location -- create one with an auto-generated rdf:ID
			resolvedOWLEntity = createOWLEntity(referenceType, namespace);
			// log "creating " + referenceType
			recordCreatedOWLEntityRDFIDAtLocation(referenceType, location, namespace, resolvedOWLEntity);
		}
		return resolvedOWLEntity;
	}

	// Here, owlEntityName may represent an IRI, a prefixed name, or a short name. If a namespace or prefix is specified
	// for a reference then we assume that it is a fragment and prepend a namespace to it; otherwise we assume it can
	// be either of the three and let createOWLClass take care of it. An empty entityRDFID indicates that one should
	// be generated.
	// TODO Fix so that we are strict about rdf:ID
	// TODO Use Optional return value
	private OWLEntity createOWLEntity(SpreadsheetLocation location, String namespace, String entityLocalID,
			ReferenceType referenceType, ReferenceDirectives referenceDirectives) throws RendererException
	{
		boolean isEmptyName = entityLocalID.isEmpty();

		if (referenceType.isOWLClass()) {
			return isEmptyName ? createOWLClass(namespace) : createOWLClass(namespace, entityLocalID);
		} else if (referenceType.isOWLNamedIndividual()) {
			return isEmptyName ? createOWLNamedIndividual(namespace) : createOWLNamedIndividual(namespace, entityLocalID);
		} else if (referenceType.isOWLObjectProperty()) {
			return isEmptyName ? createOWLObjectProperty(namespace) : createOWLObjectProperty(namespace, entityLocalID);
		} else if (referenceType.isOWLDataProperty()) {
			return isEmptyName ? createOWLDataProperty(namespace) : createOWLDataProperty(namespace, entityLocalID);
		} else if (referenceType.isOWLAnnotationProperty()) {
			return isEmptyName ? createOWLAnnotationProperty(namespace) : createOWLAnnotationProperty(namespace, entityLocalID);
		}
			throw new RendererException(
					"unknown entity type " + referenceType + " for entity " + entityLocalID + " in namespace " + namespace
							+ "	in reference " + referenceDirectives + " at location " + location);
	}

	private void addRDFSLabelToOWLEntity(ReferenceType referenceType, OWLEntity resolvedOWLEntity, String namespace,
			String labelText, String language) throws RendererException
	{
		String creationLanguage = language != null && "*".equals(language) ? getDefaultLanguage() : language;

		addRDFSLabelToOWLEntity(resolvedOWLEntity, labelText, language);

		recordCreatedOWLEntityNameWithRDFSLabel(referenceType, namespace, labelText, creationLanguage, resolvedOWLEntity);
		// log "adding rdfs:label " + labelText
		if (creationLanguage == null || creationLanguage.isEmpty()) {
			// log
		} else {
			// log ", xml:lang \"" + creationLanguage + "\""
		}
	}

	private OWLEntity createOWLEntity(ReferenceType referenceType, String namespace) throws RendererException
	{
		if (referenceType.isOWLClass()) {
			return createOWLClass(namespace);
		} else if (referenceType.isOWLNamedIndividual()) {
			return createOWLNamedIndividual(namespace);
		} else if (referenceType.isOWLObjectProperty()) {
			return createOWLObjectProperty(namespace);
		} else if (referenceType.isOWLDataProperty()) {
			return createOWLDataProperty(namespace);
		} else
			throw new InternalRendererException("unknown entity type " + referenceType);
	}

	private boolean hasOWLEntityBeenCreatedAtLocation(SpreadsheetLocation location, String namespace)
			throws RendererException
	{
		if (this.createdOWLEntitiesUsingLocation.containsKey(namespace)) {
			return this.createdOWLEntitiesUsingLocation.get(namespace).containsKey(location);
		} else
			return false;
	}

	private OWLEntity getCreatedEntityRDFIDAtLocation(ReferenceType referenceType, SpreadsheetLocation location,
			String namespace) throws RendererException
	{
		if (this.createdOWLEntitiesUsingLocation.containsKey(namespace) && this.createdOWLEntitiesUsingLocation
				.get(namespace).containsKey(location))
			return this.createdOWLEntitiesUsingLocation.get(namespace).get(location);
		else
			throw new InternalRendererException(
					"" + referenceType + " with namespace " + namespace + " was not created at location " + location);
	}

	private void recordCreatedOWLEntityRDFIDAtLocation(ReferenceType referenceType, SpreadsheetLocation location,
			String namespace, OWLEntity owlEntity) throws RendererException
	{
		if (location != null) {
			if (this.createdOWLEntitiesUsingLocation.containsKey(namespace)) {
				if (!this.createdOWLEntitiesUsingLocation.get(namespace).containsKey(location))
					this.createdOWLEntitiesUsingLocation.get(namespace).put(location, owlEntity);
				else
					checkOWLReferenceType(referenceType, owlEntity);
			} else {
				this.createdOWLEntitiesUsingLocation.put(namespace, new HashMap<>());
				this.createdOWLEntitiesUsingLocation.get(namespace).put(location, owlEntity);
			}
		}
	}

	private void recordCreatedOWLEntityNameWithRDFSLabel(ReferenceType referenceType, String namespace, String labelText,
			String language, OWLEntity owlEntity) throws RendererException
	{
		String key = language == null ? labelText : labelText + "@" + language;

		if (this.createdOWLEntitiesUsingLabel.containsKey(namespace)) {
			if (!this.createdOWLEntitiesUsingLabel.get(namespace).containsKey(key))
				this.createdOWLEntitiesUsingLabel.get(namespace).put(key, owlEntity);
			else
				checkOWLReferenceType(referenceType, owlEntity);
		} else {
			this.createdOWLEntitiesUsingLabel.put(namespace, new HashMap<>());
			this.createdOWLEntitiesUsingLabel.get(namespace).put(key, owlEntity);
		}
	}

	// Duplicate labels are allowed if they are in different namespaces.
	private boolean hasOWLEntityBeenCreatedWithLabel(String namespace, String labelText, String language)
	{
		String key = language == null || language.isEmpty() ? labelText : labelText + "@" + language;

		if (this.createdOWLEntitiesUsingLabel.containsKey(namespace)) {
			return this.createdOWLEntitiesUsingLabel.get(namespace).containsKey(key);
		} else
			return false;
	}

	private void checkOWLReferenceType(ReferenceType expectedReferenceType, OWLEntity owlEntity) throws RendererException
	{
		if (expectedReferenceType.isOWLClass() && !this.ontology.containsClassInSignature(owlEntity.getIRI())
				|| expectedReferenceType.isOWLNamedIndividual() && !this.ontology
				.containsIndividualInSignature(owlEntity.getIRI())
				|| expectedReferenceType.isOWLObjectProperty() && !this.ontology
				.containsObjectPropertyInSignature(owlEntity.getIRI())
				|| expectedReferenceType.isOWLDataProperty() && !this.ontology
				.containsDataPropertyInSignature(owlEntity.getIRI()))
			throw new RendererException(
					"existing OWL entity with URI " + owlEntity + " has type that differs from expected " + expectedReferenceType
							.getTypeName());

	}

	private OWLEntity getCreatedOWLEntityWithRDFSLabel(ReferenceType referenceType, String namespace, String labelText,
			String language) throws RendererException
	{
		String key = language == null ? labelText : labelText + "@" + language;

		if (this.createdOWLEntitiesUsingLabel.containsKey(namespace) && this.createdOWLEntitiesUsingLabel.get(namespace)
				.containsKey(key))
			return this.createdOWLEntitiesUsingLabel.get(namespace).get(key);
		else
			throw new InternalRendererException(
					"" + referenceType + " with namespace " + namespace + " was not created with rdfs:label " + key);
	}

	private void addRDFSLabelToOWLEntity(OWLEntity entity, String labelText, String language)
	{
		throw new RuntimeException("not implemented");
	}

	private String getDefaultLanguage()
	{
		throw new RuntimeException("not implemented");
	}

	// TODO Use Optional return value
	private Set<OWLEntity> getOWLEntityWithRDFSLabel(String labelText)
	{
		// TODO Confirm with Martin
		return this.ontology.getEntitiesInSignature(IRI.create(labelText));
	}

	// TODO Use Optional return value
	private Set<OWLEntity> getOWLEntityWithRDFSLabelAndAtLeastOneLanguage(String labelText)
	{
		// TODO Confirm with Martin
		return this.ontology.getEntitiesInSignature(IRI.create(labelText));
	}

	// TODO Use Optional return value
	private Set<OWLEntity> getOWLEntityWithRDFSLabelAndLanguage(String labelText, String language)
	{
		// TODO Confirm with Martin
		return this.ontology.getEntitiesInSignature(IRI.create(labelText));
	}

	private boolean isExistingOWLEntityWithRDFSLabel(String labelText)
	{
		// TODO Confirm with Martin
		return this.ontology.containsEntityInSignature(IRI.create(labelText));
	}

	private boolean isExistingOWLEntityWithRDFSLabelAndLanguage(String labelText, String language)
	{
		// TODO Confirm with Martin
		return this.ontology.containsEntityInSignature(IRI.create(labelText));
	}

	private boolean isExistingOWLEntityWithRDFID(String rdfID)
	{
		return this.ontology.containsEntityInSignature(IRI.create(rdfID));
	}

	private OWLClass createOWLClass(String namespace, String localName)
	{
		return owlDataFactory.getOWLClass(IRI.create(namespace, localName));
	}

	private OWLClass createOWLClass(String namespace)
	{
		return owlDataFactory.getOWLClass(IRI.create(namespace));
	}

	private OWLNamedIndividual createOWLNamedIndividual(String namespace, String localName)
	{
		return owlDataFactory.getOWLNamedIndividual(IRI.create(namespace, localName));
	}

	private OWLNamedIndividual createOWLNamedIndividual(String namespace)
	{
		return owlDataFactory.getOWLNamedIndividual(IRI.create(namespace));
	}

	private OWLObjectProperty createOWLObjectProperty(String namespace, String localName)
	{
		return owlDataFactory.getOWLObjectProperty(IRI.create(namespace, localName));
	}

	private OWLObjectProperty createOWLObjectProperty(String namespace)
	{
		return owlDataFactory.getOWLObjectProperty(IRI.create(namespace));
	}

	private OWLDataProperty createOWLDataProperty(String namespace, String localName)
	{
		return owlDataFactory.getOWLDataProperty(IRI.create(namespace, localName));
	}

	private OWLDataProperty createOWLDataProperty(String namespace)
	{
		return owlDataFactory.getOWLDataProperty(IRI.create(namespace));
	}

	private OWLAnnotationProperty createOWLAnnotationProperty(String namespace, String localName)
	{
		return owlDataFactory.getOWLAnnotationProperty(IRI.create(namespace, localName));
	}

	private OWLAnnotationProperty createOWLAnnotationProperty(String namespace)
	{
		return owlDataFactory.getOWLAnnotationProperty(IRI.create(namespace));
	}

	private void throwOWLEntityExistsWithLabelException(String labelText, String language) throws RendererException
	{
		String errorMessage = "an OWL entity already exists with the rdfs:label " + labelText;
		if (language != null && !language.isEmpty())
			errorMessage += " and language " + language;
		throw new RendererException(errorMessage);
	}

	private void warnOWLEntityExistsWithRDFSLabel(String labelText, String language)
	{
		String errorMessage = "WARNING: an OWL entity already exists with the rdfs:label " + labelText;
		if (language != null && !language.isEmpty())
			errorMessage += " and language " + language;
		// log errorMessage
	}

	private void throwNoExistingOWLEntityWithRDFSLabelException(String labelText, String language)
			throws RendererException
	{
		String errorMessage = "an OWL entity does not exists with the rdfs:label " + labelText;
		if (language != null && !language.isEmpty())
			errorMessage += " and language " + language;
		throw new RendererException(errorMessage);
	}

	private void warnNoExistingOWLEntityWithRDFSLabel(String labelText, String language)
	{
		String errorMessage = "WARNING: an OWL entity does not exists with the rdfs:label " + labelText;
		if (language != null && !language.isEmpty())
			errorMessage += " and language " + language;
		// log errorMessage
	}

	private void throwOWLEntityExistsWithRDFIDException(String namespace, String rdfID) throws RendererException
	{
		throw new RendererException("an OWL entity already exists in namespace " + namespace + " with the rdf:ID " + rdfID);
	}

	private void warnOWLEntityExistsWithRDFID(String namespace, String rdfID) throws RendererException
	{
		// log "WARNING: an entity already exists in namespace " + namespace + " with the rdf:ID " + rdfID
	}

	private void throwNoExistingOWLEntityWithRDFIDException(String namespace, String rdfID) throws RendererException
	{
		throw new RendererException("an entity does not exists in namespace " + namespace + " with the rdf:ID " + rdfID);
	}

	private void warnNoExistingOWLEntityWithRDFID(String namespace, String rdfID) throws RendererException
	{
		// log "WARNING: an entity does not exists in namespace " + namespace + " with the rdf:ID " + rdfID
	}
}
