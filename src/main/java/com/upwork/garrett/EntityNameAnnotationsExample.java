package com.upwork.garrett;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.RegexNERAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokensRegexAnnotator;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.*;
/**
 * Created by satishterala on 9/12/16.
 */
public class EntityNameAnnotationsExample {
    private static HashMap<String,ArrayList<String>> PotentialTriggerWords = new HashMap<String, ArrayList<String>>();
    private static String currentScientist = "";
    private static ArrayList<String> polyMaths = new ArrayList<>();

    private Properties createProps() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma,ner,regexner  ,parse, dcoref");
        props.put("regexner.ignoreCase", "true");
        return props;
    }

    private static void createListTriggerWords(List<SemanticGraphEdge> outEdgesSorted) {
        String noun = "NN";

        for (SemanticGraphEdge edge : outEdgesSorted) {
            IndexedWord dep = edge.getDependent();
            String triggerWord = dep.lemma();

            if (dep.tag().equals(noun)) {
                ArrayList<String> TriggerWords = PotentialTriggerWords.get(currentScientist);
                TriggerWords.add(triggerWord);
                PotentialTriggerWords.put(currentScientist,TriggerWords);
            }
        }
    }

    // What Doc is being prepped?
    private Annotation prepDoc(String fileName) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(System.currentTimeMillis());
        // inputText will be the text to evaluate in this examplefileName
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        File file = FileUtils.toFile(url);
        String text1 = FileUtils.readFileToString(file);

        String sampleTxt = "He sat down at his piano and started playing. He continued playing and writing notes for half an hour.At the end of the two weeks, he came downstairs with two sheets of paper bearing his theory.He could also play the esraj, a musical instrument similar to a violin";
        Annotation document = new Annotation(sampleTxt);
        document.set(CoreAnnotations.DocDateAnnotation.class, currentTime);
        return document;
    }

    public static void main(String[] args) throws IOException {
        // Read Training File
        // Instantiate music word dictionary
        // Create list of potential trigger words from training file
        // Run the Training File Against the music word dictionary
        EntityNameAnnotationsExample EVTest = new EntityNameAnnotationsExample();
        ReadCSV createHash = new ReadCSV();
        HashMap<String,String> musicDictionary = createHash.createDictionaryHash();
        String inputText = "Albert_Einstein" + ".txt";
        currentScientist = "Albert_Einstein";
        Properties props = EVTest.createProps();
        StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);
        Annotation document = EVTest.prepDoc(inputText);
        ArrayList<String> triggerWords = new ArrayList<String>();
        PotentialTriggerWords.put(currentScientist,triggerWords);
        pipeLine.annotate(document);

        // Separate out into a function to read the text
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        HashMap<String,ArrayList<String>> tags = identifySentenceTags(sentences);
        checkIfArtist(tags, currentScientist,musicDictionary);

    }

    public static void checkIfArtist (HashMap<String,ArrayList<String>> PotentialTriggerWords,String scientist, HashMap<String,String> musicDictionary ){
        // return a hashmap an add the item
        ArrayList<String> listOfWords = PotentialTriggerWords.get(scientist);
        for (String triggerWord : listOfWords) {
            for (String instrument : musicDictionary.keySet()) {
                if (triggerWord.equals(instrument)) {
                    polyMaths.add(scientist);
                }
            }

        }
        System.out.println(polyMaths);
    }

    // How is it analyzing the text
    public static HashMap<String,ArrayList<String>> identifySentenceTags(List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            /* Next we will extract the SemanticGraph to examine the connection
            between the words in our evaluated sentence */
            SemanticGraph dependencies = sentence.get
                    (SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
            IndexedWord firstRoot = dependencies.getFirstRoot();
            // this section is same as above just we retrieve the OutEdges
            List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(firstRoot);
            createListTriggerWords(outEdgesSorted);
        }
        return PotentialTriggerWords;
    }
}
