package com.upwork.garrett;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;
import java.util.Set;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


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
        String serializedClassifier = "src/main/classifiers/music-pos-tagger-model.ser.gz";


        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

        HashMap<String, String[]> sampleText = new HashMap<>();
        String[] example = {"Good afternoon Rajat Raina, how are you today?",
                "I go to school at Stanford University, which is located in California." };
        sampleText.put("Einstein", example);
        HashMap<String, String> possibleMusicians = new HashMap<>();

        String Scientist = sampleText.keySet().stream().findFirst().toString();

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
                        if (musicCheck == "music") {
                            // Add Text to doc
                        }

                    if (musicCheck == "MUSIC") {
                        possibleMusicians.put(Scientist, str);
                        break;
                    }

                }
            }
        }

        System.out.println("---");

    }

}