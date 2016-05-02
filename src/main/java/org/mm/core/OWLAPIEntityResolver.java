package org.mm.core;

import java.util.Optional;

import org.mm.exceptions.EntityCreationException;
import org.mm.exceptions.EntityNotFoundException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;

public class OWLAPIEntityResolver implements OWLEntityResolver
{
   private OWLOntology ontology;
   private OWLDataFactory owlDataFactory;
   private PrefixManager prefixManager;

   public OWLAPIEntityResolver(OWLOntology ontology, PrefixManager prefixManager)
   {
      this.ontology = ontology;
      this.prefixManager = prefixManager;
      owlDataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
   }

   @Override
   public <T extends OWLEntity> T resolve(String shortName, final Class<T> entityType)
         throws EntityNotFoundException
   {
      IRI entityIRI = prefixManager.getIRI(shortName);
      Optional<OWLEntity> foundEntity = ontology.getEntitiesInSignature(entityIRI).stream().findFirst();
      if (foundEntity.isPresent()) {
         try {
            return entityType.cast(foundEntity.get());
         }
         catch (ClassCastException e) {
            String template = "The expected entity '%s' does not have type: %s";
            throw new EntityNotFoundException(String.format(template, shortName, entityType.getSimpleName()));
         }
      }
      String template = "The expected entity '%s' does not exist in the ontology";
      throw new EntityNotFoundException(String.format(template, shortName));
   }

   @Override
   public <T extends OWLEntity> T create(String entityName, final Class<T> entityType)
         throws EntityCreationException
   {
      IRI entityIRI = prefixManager.getIRI(entityName);
      Optional<OWLEntity> foundEntity = ontology.getEntitiesInSignature(entityIRI).stream().findFirst();
      if (foundEntity.isPresent()) {
         return entityType.cast(foundEntity.get());
      } else {
         if (OWLClass.class.isAssignableFrom(entityType)) {
            return entityType.cast(owlDataFactory.getOWLClass(entityName, prefixManager));
         } else if (OWLObjectProperty.class.isAssignableFrom(entityType)) {
            return entityType.cast(owlDataFactory.getOWLObjectProperty(entityName, prefixManager));
         } else if (OWLDataProperty.class.isAssignableFrom(entityType)) {
            return entityType.cast(owlDataFactory.getOWLDataProperty(entityName, prefixManager));
         } else if (OWLNamedIndividual.class.isAssignableFrom(entityType)) {
            return entityType.cast(owlDataFactory.getOWLNamedIndividual(entityName, prefixManager));
         } else if (OWLAnnotationProperty.class.isAssignableFrom(entityType)) {
            return entityType.cast(owlDataFactory.getOWLAnnotationProperty(entityName, prefixManager));
         } else if (OWLDatatype.class.isAssignableFrom(entityType)) {
            return entityType.cast(owlDataFactory.getOWLDatatype(entityName, prefixManager));
         }
      }
      throw new IllegalStateException("Programmer error - report this (with stack trace) to the Protege mailing list");
   }
}
