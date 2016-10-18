package com.upwork.garrett;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.RegexNERAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
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
    private static HashMap<String,String> musicDictionary = new HashMap();

    public static void main(String[] args) throws IOException {
        // Init StanfordLibrary
        pipeLine = new StanfordCoreNLP(props);

        // Create Music Dictionary
        EntityNameAnnotationsExample EVTest = new EntityNameAnnotationsExample();
        ReadCSV createHash = new ReadCSV();
        musicDictionary = createHash.createDictionaryHash();

        // Get list of Scientists Objects
        JSONReadFromFile jsonReader = new JSONReadFromFile();
        Results scientists = jsonReader.initScientistsObject();
//        ArrayList<HashMap<String, ArrayList<String>>> setOfScientists =  jsonReader.cycleSelectScientists(scientists, 3,10);
//        EVTest.checkScientistSet(setOfScientists, musicDictionary);

        EVTest.testingData(jsonReader, scientists);
    }

    private void testingData(JSONReadFromFile jsonReader, Results scientists) {
        Scientist firstScientist = jsonReader.getindivScientist(scientists, 28);
        Scientist secondScientist = jsonReader.getindivScientist(scientists, 39);
        Scientist thirdScientist = jsonReader.getindivScientist(scientists, 139);
        Scientist fourthScientist = jsonReader.getindivScientist(scientists, 314);
        Scientist fifthScientist = jsonReader.getindivScientist(scientists, 144);
        Scientist sixthScientist = jsonReader.getindivScientist(scientists, 149);
        Scientist seventhScientist = jsonReader.getindivScientist(scientists, 0);
        Scientist eigthScientist = jsonReader.getindivScientist(scientists, 1);
        Scientist ninthScientist = jsonReader.getindivScientist(scientists, 2);
        Scientist tenthScientist = jsonReader.getindivScientist(scientists, 271);

        currentScientist = secondScientist.getTitle();
        System.out.println(currentScientist);
        secondScientist.setText("He was the music man. Michael was a talented musician.");
        HashMap lemmas = this.lemmatize("He was the music man. Michael was a talented musician.");
        HashMap<String, ArrayList<Integer>> scientistCheck = this.polyMathCheck(lemmas, musicDictionary,currentScientist);
        this.taggedSentence(secondScientist, scientistCheck);
    }

    public void checkIndividualScientist(Scientist scientist,HashMap<String,String> musicDictionary){
        currentScientist = scientist.getTitle();
        JSONReadFromFile jsonReader = new JSONReadFromFile();
        ArrayList sentenceFragments = jsonReader.splitTextBySentence(scientist);

        ArrayList<String> triggerWords = new ArrayList<String>();
        PotentialTriggerWords.put(currentScientist,triggerWords);
        try {
            this.cycleThroughScientistText(sentenceFragments, pipeLine, musicDictionary);
        }
        catch(IOException e){
            System.out.println(e);
        }
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

    private HashMap<String, ArrayList<Integer>> polyMathCheck (HashMap<String, Integer> Lemmas, HashMap<String,String> musicDictionary, String currentScientist ){
        // return a hashmap an add the item
        HashMap<String, ArrayList<Integer>> newScientist = new HashMap();
        ArrayList<Integer> Integers = new ArrayList();

        Lemmas.forEach((k,v) ->{
            for (String instrument : musicDictionary.keySet()) {
                if (k.equals(instrument)) {
                    Integers.add(v);
                    newScientist.put(currentScientist, Integers);
                    break;
                }
            }
        });
        System.out.println(newScientist);
        return newScientist;
    }

    public ArrayList<CoreMap> taggedSentence(Scientist scientist, HashMap<String, ArrayList<Integer>> metaData){
        Annotation document = new Annotation(scientist.getText());

        pipeLine.annotate(document);
        ArrayList<CoreMap> PotentialArtistSentences = new ArrayList<>();
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        ArrayList<Integer> taggedSentencePosition = metaData.get(scientist.getTitle());
        taggedSentencePosition.forEach((k) -> {
            PotentialArtistSentences.add(sentences.get(k - 1));
        });

        return PotentialArtistSentences;
    }

    // Create Method that grabs the sentence of the current scientist
    // Make sure the format for the current scientst is the same as everywhere else 

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
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        props.put("regexner.ignoreCase", "true");
        return props;
    }

    private static Properties createAdvanceProps() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma,ner,regexner  ,parse, dcoref");
        props.put("regexner.ignoreCase", "true");
        return props;
    }

    public HashMap<String,Integer> lemmatize(String documentText) {
        ArrayList<String> lemmas = new ArrayList<String>();
        HashMap<String,Integer> lemmaSet = new HashMap();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text
        pipeLine.annotate(document);

        Integer Counter = 0;
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            Counter++;
            // Iterate over all tokens in a sentence

            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
//                lemmas.add(token.get(LemmaAnnotation.class));
                lemmaSet.put(token.get(LemmaAnnotation.class), Counter);
            }
        }

        return lemmaSet;
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

    private void analyzeSentenceStructure(List<SemanticGraphEdge> outEdgesSorted) {
        String noun = "NN";
        SemanticGraphEdge firstItem = outEdgesSorted.get(0);
        String Name = firstItem.getTarget().value();

        if (firstItem.getRelation().getShortName().equals("nsubj") || firstItem.getRelation().getShortName().equals("nsubjpass")) {
            if (Name.equals(currentScientist) || Name.equals("He") || Name.equals("She")){
                // Check whether the edge is in the dictionary
                //firstItem.getGovernor()
                // firstItem.getSource()

            }
        }
        for (SemanticGraphEdge edge : outEdgesSorted) {
            edge.getTarget();
            IndexedWord dep = edge.getDependent();

        }

    }

    private void createEdgeGraph(String documentText) throws IOException{
        Annotation document = this.StanfordHelperPrepDoc(documentText);
        pipeLine.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            SemanticGraph dependencies = sentence.get
                    (SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
            IndexedWord firstRoot = dependencies.getFirstRoot();
            // this section is same as above just we retrieve the OutEdges
            List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(firstRoot);
            this.analyzeSentenceStructure(outEdgesSorted);
        }

    }
}



    //
    //