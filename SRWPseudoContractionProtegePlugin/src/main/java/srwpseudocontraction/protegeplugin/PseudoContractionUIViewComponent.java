package srwpseudocontraction.protegeplugin;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * The main graphical component of the Protégé plug-in.
 *
 * @author Vinícius B. Matos
 */
public class PseudoContractionUIViewComponent extends AbstractOWLViewComponent {

    private static final long serialVersionUID = 4224262730671300149L;

    /**
     * The input field.
     */
    private ExpressionEditor<OWLAxiom> axiomInputField;

    @Override
    protected void disposeOWLView() {

    }

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        add(createEditorComponent(), BorderLayout.CENTER);
        validate();

    }

    /**
     * Creates the axiom editor component.
     * 
     * @return the editor component
     */
    private JComponent createEditorComponent() {
        MainPanel panel = new MainPanel();
        axiomInputField = new ExpressionEditor<OWLAxiom>(getOWLEditorKit(), new AlternativeExpressionChecker(this));
        PseudoContractAction action = new PseudoContractAction(axiomInputField, panel, getOWLModelManager());
        panel.addButtonAction(action);
        panel.addInputField(axiomInputField);
        return panel;
    }

}
