package srwpseudocontraction.protegeplugin;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLAxiom;

import srwpseudocontraction.AlternativeOWLExpressionParser;

/**
 * Implements the parsing of an axiom in Manchester syntax that is input by the
 * user.
 *
 * @author Vinícius B. Matos
 */
public class AlternativeExpressionChecker implements OWLExpressionChecker<OWLAxiom> {

    /**
     * The main graphical component of the plug-in.
     */
    private AbstractOWLViewComponent view;

    /**
     * Instantiates the class.
     *
     * @param view
     *            the main graphical component
     */
    public AlternativeExpressionChecker(AbstractOWLViewComponent view) {
        this.view = view;
    }

    @Override
    public void check(String expression) throws OWLExpressionParserException {
        OWLModelManager mm = view.getOWLModelManager();
        try {
            AlternativeOWLExpressionParser.parse(mm.getOWLOntologyManager(), mm.getActiveOntology(), expression);

        } catch (Exception e) {
            throw new OWLExpressionParserException(e);
        }
    }

    @Override
    public OWLAxiom createObject(String expression) throws OWLExpressionParserException {
        OWLModelManager mm = view.getOWLModelManager();
        return AlternativeOWLExpressionParser.parse(mm.getOWLOntologyManager(), mm.getActiveOntology(), expression);
    }

}
