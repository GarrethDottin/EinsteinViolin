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
import org.apache.commons.lang.ObjectUtils;

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
    private static Properties props = createProps();

    public static void main(String[] args) throws IOException {


        // Create Music Dictionary
        EntityNameAnnotationsExample EVTest = new EntityNameAnnotationsExample();
        ReadCSV createHash = new ReadCSV();
        HashMap<String,String> musicDictionary = createHash.createDictionaryHash();
        WordVecDictionary expandedDictionary = new WordVecDictionary();
//        expandedDictionary.initDictionary();

        // Get list of Scientists Objects
        JSONReadFromFile test = new JSONReadFromFile();
        Results scientists = test.initScientistsObject();
        HashMap setOfScientists =  test.cycleSelectScientists(scientists, 0,10);
        EVTest.checkScientistSet(setOfScientists);
        Scientist scientistOnBoard = test.indivScientist(scientists);
        currentScientist = scientistOnBoard.getTitle();
        ArrayList sentenceFragment = test.splitTextBySentence(scientistOnBoard);


        // Init Props
        Properties props = createProps();
        StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);

        ArrayList<String> triggerWords = new ArrayList<String>();
        PotentialTriggerWords.put(currentScientist,triggerWords);

        EVTest.cycleThroughScientistText(sentenceFragment,EVTest, pipeLine, musicDictionary);
    }


    public void checkScientistSet (HashMap ScientistSet) {
        // can check a group of scientist for data
        // Loop over Hashmap
        // Loop over setence fragments within individual fragment
        // Once its done with each item set the currentscientist
        for (Object Scientist : ScientistSet.entrySet()){
            System.out.println(Scientist);
        }


    }
    private void cycleThroughScientistText  (ArrayList sentenceFragment,EntityNameAnnotationsExample EVTest,StanfordCoreNLP pipeLine, HashMap<String,String> musicDictionary  ) throws IOException{

        for (int i = 0; i < sentenceFragment.size(); i++) {
            String currentSentence = sentenceFragment.get(i).toString();
            Annotation document = EVTest.prepDoc(currentSentence);
            pipeLine.annotate(document);
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            identifySentenceTags(sentences);
        }
            checkIfArtist(PotentialTriggerWords, currentScientist,musicDictionary);
    }

    private Annotation prepDoc(String inputText) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(System.currentTimeMillis());
        Annotation document = new Annotation(inputText);
        document.set(CoreAnnotations.DocDateAnnotation.class, currentTime);
        return document;
    }


    public static void checkIfArtist (HashMap<String,ArrayList<String>> PotentialTriggerWords,String scientist, HashMap<String,String> musicDictionary ){
        // return a hashmap an add the item
        ArrayList<String> listOfWords = PotentialTriggerWords.get(scientist);
        for (String triggerWord : listOfWords) {
            for (String instrument : musicDictionary.keySet()) {
                if (triggerWord.equals(instrument)) {
                    polyMaths.add(scientist);
                    break;
                }
            }

        }
        System.out.println(polyMaths);
    }

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

    private static Properties createProps() {
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
            if (triggerWord == null) {
                triggerWord = dep.value();
            }
            if (dep.tag().equals(noun)) {
                ArrayList<String> TriggerWords = PotentialTriggerWords.get(currentScientist);
                TriggerWords.add(triggerWord);
                PotentialTriggerWords.put(currentScientist,TriggerWords);
            }
        }
    }


}
