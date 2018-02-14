package srwpseudocontraction.protegeplugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import srwpseudocontraction.SRWPseudoContractor;
import srwpseudocontraction.SelectionFunctionFull;

/**
 * The action that is run after the user presses the "Pseudocontract" button in
 * the Protégé plug-in.
 *
 * @author Vinícius B. Matos
 */
public class PseudoContractAction implements ActionListener {

    /**
     * The input field.
     */
    private ExpressionEditor<OWLAxiom> axiomInputField;

    /**
     * The model manager.
     */
    private OWLModelManager modelManager;

    /**
     * The main panel.
     */
    private MainPanel panel;

    /**
     * Instantiates the class.
     *
     * @param axiomInputField
     *            the input field
     * @param panel
     *            the main panel
     * @param modelManager
     *            the model manager
     */
    public PseudoContractAction(ExpressionEditor<OWLAxiom> axiomInputField,
            MainPanel panel, OWLModelManager modelManager) {
        this.axiomInputField = axiomInputField;
        this.modelManager = modelManager;
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (!axiomInputField.isWellFormed()) {
            showErrorMessage("Ill-formed axiom expression:\n\""
                    + axiomInputField.getText() + "\"");
            return;
        }
        OWLAxiom sentence;
        try {
            sentence = axiomInputField.createObject();
        } catch (OWLException e) {
            showErrorMessage("The following expression could not be parsed:\n\""
                    + axiomInputField.getText() + "\"");
            e.printStackTrace();
            return;
        }
        try {
            pseudocontract(sentence);
        } catch (OWLException e) {
            showErrorMessage("The operation has failed.");
            e.printStackTrace();
            return;
        }
        showOKMessage("The operation has been successfully executed.");
    }

    /**
     * Displays an error message.
     *
     * @param message
     *            the error message
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Display a success message.
     *
     * @param message
     *            the msessage
     */
    private void showOKMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Executes the SRW pseudo-contraction on the ontology.
     *
     * @param sentence
     *            the formula to be pseudo-contrated
     * @throws OWLException
     *             OWLException
     */
    private void pseudocontract(OWLAxiom sentence) throws OWLException {
        // get current ontology
        OWLOntology ontology = modelManager.getActiveOntology();
        // instantiate reasoner
        OWLReasonerManager reasonerManager = modelManager.getOWLReasonerManager();
        OWLReasonerFactory reasonerFactory = reasonerManager.getCurrentReasonerFactory()
                .getReasonerFactory();
        // create pseudo-contractor object
        SRWPseudoContractor pseudoContractor = new SRWPseudoContractor(
                modelManager.getOWLOntologyManager(), reasonerFactory,
                new SelectionFunctionFull());
        pseudoContractor.setMaxQueueSize(panel.getQueueCapacity());
        pseudoContractor.setMaxRemainderElements(panel.getMaxRemainderSize());
        // apply the operation
        Set<OWLAxiom> result = pseudoContractor.pseudocontract(ontology, sentence);
        // difference <- old \ new
        Set<OWLAxiom> difference = new HashSet<OWLAxiom>(ontology.getAxioms());
        difference.removeAll(result);
        // remove from the ontology the axioms not present in the result
        // and then add to the ontology all the axioms in the result
        OWLOntologyManager ontologyManager = modelManager.getOWLOntologyManager();
        ontologyManager.removeAxioms(ontology, difference);
        ontologyManager.addAxioms(ontology, result);
    }
}
