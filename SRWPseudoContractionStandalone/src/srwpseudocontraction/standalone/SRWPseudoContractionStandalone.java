package srwpseudocontraction.standalone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import srwpseudocontraction.AlternativeOWLExpressionParser;
import srwpseudocontraction.SRWPseudoContractor;
import srwpseudocontraction.SelectionFunctionFull;

/**
 * Command-line interface of the SRW pseudo-contractor.
 *
 * @author Vinícius B. Matos
 *
 */
public class SRWPseudoContractionStandalone {

    @Parameter(names = { "-i",
            "--input" }, description = "Input file name (OWL ontology)", required = true)
    private String inputFileName;
    @Parameter(names = { "-o",
            "--output" }, description = "Output file name", required = true)
    private String outputFileName;
    @Parameter(names = { "-f",
            "--formula" }, description = "Formula to be pseudo-contracted", required = true)
    private String formulaString;
    @Parameter(names = { "--queue-limit" }, description = "Limit of the queue size")
    private Integer maxQueueSize = Integer.MAX_VALUE;
    @Parameter(names = {
            "--remainder-limit" }, description = "Maximum number of elements in the computer remainder set")
    private Integer maxRemainderSize = Integer.MAX_VALUE;
    @Parameter(names = { "-h", "--help" }, help = true)
    private boolean help = false;

    private void run() {
        OWLOntologyManager manager;
        OWLOntology ontology;
        Logger.getLogger("SRW").log(Level.INFO, "Opening the ontology...");
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(inputFileName));
        } catch (Exception e) {
            Logger.getLogger("SRW").log(Level.SEVERE, String
                    .format("Could not open the ontology file '%s'.\n", inputFileName));
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO, "Parsing the formula...");
        OWLAxiom entailment = AlternativeOWLExpressionParser.parse(manager, ontology,
                formulaString);
        if (entailment == null) {
            Logger.getLogger("SRW").log(Level.SEVERE,
                    String.format("Bad formula: \n\t%s\n", formulaString));
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO, "Creating the pseudo-contractor...");
        SRWPseudoContractor pseudoContractor = new SRWPseudoContractor(manager,
                new ReasonerFactory(), new SelectionFunctionFull());
        pseudoContractor.setMaxRemainderElements(maxRemainderSize);
        pseudoContractor.setMaxQueueSize(maxQueueSize);
        Logger.getLogger("SRW").log(Level.INFO, "Executing the operation...");
        OWLOntology inferredOntology;
        try {
            inferredOntology = manager.createOntology(
                    pseudoContractor.pseudocontract(ontology, entailment));
        } catch (OWLException e) {
            e.printStackTrace();
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO, "Saving...");
        try {
            OutputStream s = new FileOutputStream(outputFileName);
            manager.saveOntology(inferredOntology, s);
        } catch (OWLOntologyStorageException | FileNotFoundException e) {
            Logger.getLogger("SRW").log(Level.SEVERE, String
                    .format("Could not save the ontology into '%s.'\n", outputFileName));
            e.printStackTrace();
            return;
        }
        Logger.getLogger("SRW").log(Level.INFO,
                String.format("Success! Ontology saved to '%s'.\n", outputFileName));
    }

    private boolean isHelp() {
        return help;
    }

    public static void main(String[] args) throws Exception {
        SRWPseudoContractionStandalone srw = new SRWPseudoContractionStandalone();
        JCommander jc = new JCommander(srw);
        jc.setProgramName("java -jar srwpseudocontraction.standalone-<version>.jar");
        try {
            jc.parse(args);
        } catch (ParameterException e) {
            StringBuilder out = new StringBuilder();
            jc.usage(out);
            System.err.println(out);
            return;
        }
        if (srw.isHelp()) {
            jc.usage();
            return;
        }
        srw.run();
    }
}
