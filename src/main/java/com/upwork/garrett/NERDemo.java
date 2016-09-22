package com.upwork.garrett;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;

import java.util.List;


/** This is a demo of calling CRFClassifier programmatically.
 *  <p>
 *  Usage: {@code java -mx400m -cp "*" NERDemo [serializedClassifier [fileName]] }
 *  <p>
 *  If arguments aren't specified, they default to
 *  classifiers/english.all.3class.distsim.crf.ser.gz and some hardcoded sample text.
 *  If run with arguments, it shows some of the ways to get k-best labelings and
 *  probabilities out with CRFClassifier. If run without arguments, it shows some of
 *  the alternative output formats that you can get.
 *  <p>
 *  To use CRFClassifier from the command line:
 *  </p><blockquote>
 *  {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -textFile [file] }
 *  </blockquote><p>
 *  Or if the file is already tokenized and one word per line, perhaps in
 *  a tab-separated value format with extra columns for part-of-speech tag,
 *  etc., use the version below (note the 's' instead of the 'x'):
 *  </p><blockquote>
 *  {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -testFile [file] }
 *  </blockquote>
 *
 *  @author Jenny Finkel
 *  @author Christopher Manning
 */

public class NERDemo {

    public static void main(String[] args) throws Exception {

        // Create model using props file
        String serializedClassifier = "src/main/classifiers/english.all.3class.distsim.crf.ser.gz";

        if (args.length > 0) {
            serializedClassifier = args[0];
        }

        //

        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

    /* For either a file to annotate or for the hardcoded text example, this
       demo file shows several ways to process the input, for teaching purposes.
    */



      /* For the hard-coded String, it shows how to run it on a single
         sentence, and how to do this and produce several formats, including
         slash tags and an inline XML output format. It also shows the full
         contents of the {@code CoreLabel}s that are constructed by the
         classifier. And it shows getting out the probabilities of different
         assignments and an n-best list of classifications with probabilities.
      */

            String[] example = {"Good afternoon Rajat Raina, how are you today?",
                    "I go to school at Stanford University, which is located in California." };
            // This prints out all the details of what is stored for each token
          /*  Interesting piece of code to use for each token
            */

            for (String str : example) {
                for (List<CoreLabel> lcl : classifier.classify(str)) {
                    for (CoreLabel cl : lcl) {
                        String tempString = cl.toShorterString();
                        String unfinishedString = tempString.substring(tempString.indexOf(" Answer=") + 8);
                        String musicCheck = unfinishedString.substring(0, unfinishedString.length()-1);
                        System.out.println(musicCheck);

                        if (musicCheck == "MUSIC") {
                            // Add Text to doc
                        }

                    }
                }
            }

            System.out.println("---");

    }

}
