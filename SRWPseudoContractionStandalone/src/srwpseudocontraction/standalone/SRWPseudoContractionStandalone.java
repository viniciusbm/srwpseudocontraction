package srwpseudocontraction.standalone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
import srwpseudocontraction.SelectionFunctionAny;

/**
 * Command-line interface of the SRW pseudo-contractor.
 *
 * @author Vinícius B. Matos
 *
 */
public class SRWPseudoContractionStandalone {

    @Parameter(names = { "-i", "--input" }, description = "Input file name (OWL ontology)", required = true)
    private String inputFileName;

    @Parameter(names = { "-o", "--output" }, description = "Output file name", required = true)
    private String outputFileName;

    @Parameter(names = { "-f", "--formula" }, description = "Formula to be pseudo-contracted", required = true)
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

        System.out.println("Opening the ontology...");
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(new File(inputFileName));
        } catch (Exception e) {
            System.err.printf("Could not open the ontology file '%s'.\n", inputFileName);
            return;
        }

        System.out.println("Parsing the formula...");
        OWLAxiom entailment = AlternativeOWLExpressionParser.parse(manager, ontology, formulaString);
        if (entailment == null) {
            System.err.printf("Bad formula: \n\t%s\n", formulaString);
            return;
        }

        System.out.println("Creating the pseudo-contractor...");
        SRWPseudoContractor pseudoContractor = new SRWPseudoContractor(manager, new ReasonerFactory(),
                new SelectionFunctionAny());
        pseudoContractor.setMaxRemainderElements(maxRemainderSize);
        pseudoContractor.setMaxQueueSize(maxQueueSize);

        System.out.println("Executing the operation...");
        OWLOntology inferredOntology;
        try {
            inferredOntology = manager.createOntology(pseudoContractor.pseudocontract(ontology, entailment));
        } catch (OWLException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Saving...");
        try {
            OutputStream s = new FileOutputStream(outputFileName);
            manager.saveOntology(inferredOntology, s);
        } catch (OWLOntologyStorageException | FileNotFoundException e) {
            System.err.printf("Could not save the ontology into '%s.'\n", outputFileName);
            e.printStackTrace();
            return;
        }

        System.out.printf("Success! Ontology saved to '%s'.", outputFileName);
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
