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
    private static String currentScientist = "Peter_Aaby";
    private static ArrayList<String> polyMaths = new ArrayList<>();
    private static Properties props = createProps();
    private static StanfordCoreNLP pipeLine;

    public static void main(String[] args) throws IOException {
        // Init StanfordLibrary
        pipeLine = new StanfordCoreNLP(props);


        // Create Music Dictionary
        EntityNameAnnotationsExample EVTest = new EntityNameAnnotationsExample();
        ReadCSV createHash = new ReadCSV();
        HashMap<String,String> musicDictionary = createHash.createDictionaryHash();

        // WordVecDictionary expandedDictionary = new WordVecDictionary();
        //  expandedDictionary.initDictionary();

        // Get list of Scientists Objects
        JSONReadFromFile test = new JSONReadFromFile();
        Results scientists = test.initScientistsObject();
        ArrayList<HashMap<String, ArrayList<String>>> setOfScientists =  test.cycleSelectScientists(scientists, 3,10);


        EVTest.checkScientistSet(setOfScientists, musicDictionary);
        Scientist scientistOnBoard = test.getindivScientist(scientists);

        //
        currentScientist = scientistOnBoard.getTitle();
        ArrayList sentenceFragment = test.splitTextBySentence(scientistOnBoard);

    }

    public void checkScientistSet (ArrayList<HashMap<String, ArrayList<String>>> ScientistSet,HashMap<String,String> musicDictionary ) throws IOException {
        ScientistSet.forEach((key) ->{
            currentScientist = key.keySet().toString().replace("[", "").replace("]", "");

            // Create Trigger Words
            ArrayList<String> triggerWords = new ArrayList<String>();
            PotentialTriggerWords.put(currentScientist,triggerWords);

            // Set of Sentences
            Collection values = key.values();
            ArrayList sentenceFragments = new ArrayList<String>(values);

            try {
                this.cycleThroughScientistText(sentenceFragments, pipeLine, musicDictionary);
            }
            catch(IOException e){
                System.out.println(e);
            }
        });
        System.out.println(polyMaths);

    }
    private void cycleThroughScientistText  (ArrayList sentenceFragment,StanfordCoreNLP pipeLine, HashMap<String,String> musicDictionary) throws IOException{

        for (int i = 0; i < sentenceFragment.size(); i++) {
            String currentSentence = sentenceFragment.get(i).toString();
            Annotation document = this.StanfordHelperPrepDoc(currentSentence);
            pipeLine.annotate(document);
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            identifySentenceStructure(sentences);
        }
            checkIfArtist(PotentialTriggerWords, currentScientist,musicDictionary);
    }

    private Annotation StanfordHelperPrepDoc(String inputText) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(System.currentTimeMillis());
        Annotation document = new Annotation(inputText);
        document.set(CoreAnnotations.DocDateAnnotation.class, currentTime);
        return document;
    }


    private static void checkIfArtist (HashMap<String,ArrayList<String>> PotentialTriggerWords,String scientist, HashMap<String,String> musicDictionary ){
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

    private static HashMap<String,ArrayList<String>> identifySentenceStructure(List<CoreMap> sentences) {
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
