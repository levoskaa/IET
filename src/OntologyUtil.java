import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerRuntimeException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OntologyUtil {
    public static final String PCSHOP_ONTOLOGY_FNAME = "pc_shop.owl.xml";
    public static final String PCSHOP_BASE_URI = "http://mit.bme.hu/ontologia/iir_labor/pc_shop.owl#";
    public static final IRI ANNOTATION_TYPE_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLDataFactory factory;

    public OntologyUtil(String ontologyFileName) {
        manager = OWLManager.createOWLOntologyManager();
        ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFileName));
        } catch (Exception e) {
            System.err.println("Hiba az ontológia betöltése közben:\n\t"
                    + e.getMessage());
            System.exit(-1);
        }
        System.out.println("Ontológia betöltve: " + manager.getOntologyDocumentIRI(ontology));
        reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
        try {
            if (!reasoner.isConsistent()) {
                System.err.println("Az ontológia nem konzisztens!");
                Node<OWLClass> inconsistentClass = reasoner.getUnsatisfiableClasses();
                System.err.println("A következő osztályok nem konzisztensek: "
                        + Util.join(inconsistentClass.getEntities(), ", ") + ".");
                System.exit(-1);
            }
        } catch (OWLReasonerRuntimeException e) {
            System.err.println("Hiba a következtetőben: " + e.getMessage());
            System.exit(-1);
        }
        factory = manager.getOWLDataFactory();
    }

    public OWLClass getClass(String className) {
        IRI classIRI = IRI.create(PCSHOP_BASE_URI + className);
        if (!ontology.containsClassInSignature(classIRI))
            return null;
        return factory.getOWLClass(classIRI);
    }

    public Set<OWLClass> getSubClasses(String className, boolean direct) {
        IRI classIRI = IRI.create(PCSHOP_BASE_URI + className);
        if (!ontology.containsClassInSignature(classIRI)) {
            System.out.println("Nincs ilyen osztály az ontológiában: \""
                    + className + "\"");
            return Collections.emptySet();
        }

        OWLClass owlClass = factory.getOWLClass(classIRI);
        NodeSet<OWLClass> subClasses;
        try {
            subClasses = reasoner.getSubClasses(owlClass, direct);
        } catch (OWLReasonerRuntimeException e) {
            System.err.println("Hiba az alosztályok következtetése közben: "
                    + e.getMessage());
            return Collections.emptySet();
        }
        return subClasses.getFlattened();
    }

    public Set<String> getClassAnnotations(OWLEntity entity) {
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(ANNOTATION_TYPE_IRI);
        Set<String> result = new HashSet<>();
        for (OWLAnnotation a : entity.getAnnotations(ontology, label)) {
            if (a.getValue() instanceof OWLLiteral) {
                OWLLiteral value = (OWLLiteral) a.getValue();
                result.add(value.getLiteral());
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
