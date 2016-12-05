package srwpseudocontraction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

/**
 * Implements a Belief Revision operation proposed by Santos, Ribeiro and
 * Wassermann.
 *
 * The operation, which we will call SRW pseudo-contraction, is defined as:
 *
 * ∩ γ(Cn*(B) ⊥ α),
 *
 * where γ is a selection function that chooses some elements of the remainder
 * set, Cn* is a tarskian consequence operator
 *
 * The resulting set must not imply the formula α.
 *
 * @author Vinícius B. Matos
 */
public class SRWPseudoContractor {

    /**
     * A factory that constructs the reasoner.
     */
    private OWLReasonerFactory reasonerFactory;

    /**
     * The ontology manager.
     */
    private OWLOntologyManager manager;

    /**
     * A selection function implementation.
     */
    private SelectionFunction gamma;

    /**
     * The capacity of the queue used by this algorithm.
     */
    private int maxQueueSize = Integer.MAX_VALUE;

    /**
     * The maximum number of elements of the remainder set that will be
     * computed.
     */
    private int maxRemainderElements = Integer.MAX_VALUE;

    /**
     * Instantiates the class.
     *
     * @param manager
     *            the ontology manager
     * @param reasonerFactory
     *            a factory that constructs the reasoner
     * @param gamma
     *            a selection function implementation
     */
    public SRWPseudoContractor(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory,
            SelectionFunction gamma) {
        this.manager = manager;
        this.reasonerFactory = reasonerFactory;
        this.gamma = gamma;
    }

    /**
     * Executes the SRW pseudo-contraction operation on the ontology.
     *
     * @param ontology
     *            the initial ontology
     * @param sentence
     *            the sentence to be pseudo-contracted
     * @return the resulting belief set, not implying the sentence
     * @throws OWLException
     *             OWLException
     */
    public Set<OWLAxiom> pseudocontract(OWLOntology ontology, OWLAxiom sentence) throws OWLException {
        // create reasoner
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        // close under Cn*
        OWLOntology inferredOntology = manager.createOntology();
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = allAxiomGenerators();
        InferredOntologyGenerator ontologyGenerator = new InferredOntologyGenerator(reasoner, gens);
        ontologyGenerator.fillOntology(manager.getOWLDataFactory(), inferredOntology);

        // obtain remainder
        RemainderBuilder remainderBuilder = new RemainderBuilder(OWLManager.createOWLOntologyManager(),
                reasonerFactory);
        remainderBuilder.setMaxQueueSize(maxQueueSize);
        remainderBuilder.setMaxRemainderElements(maxRemainderElements);
        Set<OWLAxiom> kb = inferredOntology.getAxioms();
        if (kb.isEmpty())
            throw new OWLException("The reasoner has failed to find the logic closure.");
        Set<Set<OWLAxiom>> remainderSet = remainderBuilder.remainderSet(kb, sentence);

        // apply a selection function
        Set<Set<OWLAxiom>> best = gamma.select(ontology, remainderSet);

        // find intersection
        Iterator<Set<OWLAxiom>> it = best.iterator();
        Set<OWLAxiom> intersection = it.next();
        while (it.hasNext()) {
            Set<OWLAxiom> s = it.next();
            for (Iterator<OWLAxiom> i = s.iterator(); i.hasNext();) {
                OWLAxiom axiom = i.next();
                if (!s.contains(axiom))
                    i.remove();
            }
        }

        // remove "disjoint of owl:Nothing" axioms
        // (this prevents Protégé from showing owl:Nothing as a subclass of
        // owl:Thing)

        for (Iterator<OWLAxiom> i = intersection.iterator(); i.hasNext();) {
            OWLAxiom axiom = i.next();
            if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)) {
                for (OWLClass c : ((OWLDisjointClassesAxiom) axiom).getClassesInSignature())
                    if (c.isBottomEntity()) {
                        i.remove();
                        break;
                    }
            }

        }

        // return intersection as an ontology
        return intersection;
    }

    /**
     * Returns the axiom generators that will be used by the reasoner to close
     * the belief set under its consequence operator.
     *
     * @return the list of axiom generators
     */
    private static List<InferredAxiomGenerator<? extends OWLAxiom>> allAxiomGenerators() {
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
        // classes
        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add(new InferredSubClassAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());
        gens.add(new InferredDisjointClassesAxiomGenerator());
        // data properties
        gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
        gens.add(new InferredSubDataPropertyAxiomGenerator());
        // object properties
        gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
        gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
        gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredSubObjectPropertyAxiomGenerator());
        // individuals
        gens.add(new InferredPropertyAssertionGenerator());
        return gens;
    }

    /**
     * Sets the capacity of the queue used by the algorithm.
     *
     * @param maxQueueSize
     *            the limit of the size of the queue
     *
     */
    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * Gets the capacity of the queue used by the algorithm.
     *
     * @return the limit of the size of the queue
     *
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     *
     * Gets the maximum number of elements in the computed remainder set.
     *
     * @return the maximum size of the computed remainder set
     */
    public int getMaxRemainderElements() {
        return maxRemainderElements;
    }

    /**
     *
     * Sets the maximum number of elements in the computed remainder set.
     *
     * @param maxRemainderElements
     *            the maximum size of the computed remainder set
     */
    public void setMaxRemainderElements(int maxRemainderElements) {
        this.maxRemainderElements = maxRemainderElements;
    }

}
