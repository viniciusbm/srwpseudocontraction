# SRW Pseudo-contraction

Implementation of the SRW pseudo-contraction operation 

### Binaries

The compiled JAR binaries can be found in the *dist* directory.

### Compiling

Requires Java 8 and Maven 3.

- Linux:

    `./compile-protege-plugin.sh`
     (to generate a plug-in for Protégé)

     `./compile-standalone.sh`
     (to generate a standalone version with HermiT reasoner embedded)


- Any OS:

     Firstly, run `mvn install` in the directory *SRWPseudoContraction*.
    
     Then, run `mvn package` either in the directory *SRWPseudoContractionProtegePlugin* or in the directory *SRWPseudoContractionStandalone* (respectively, to generate a plug-in for Protégé or a standalone version with HermiT reasoner embedded).


In any case, a JAR file will be generated in the *target* subdirectory.

### Running

To use the plug-in for Protégé, just copy the JAR file into the *plugins* subdirectory of your Protégé installation.  Then, on Protégé, the plug-in window can be added from the menu *Window > Views > Ontology views > SRW Pseudo-contraction*. The plug-in has been developed to work with Protégé 5.1.0, but it may work with other versions as well (not tested).


To use the standalone version, use the following command line (requires Java 8):

`java -jar srwpseudocontraction.standalone-1.jar -i <ontology-file-name> -o <output-file-name> -f <formula-to-be-contracted>`

Example:
`java -jar srwpseudocontraction.standalone-1.jar -i people+pets.owl.rdf -o output.owl -f "Rex Type: dog"`

