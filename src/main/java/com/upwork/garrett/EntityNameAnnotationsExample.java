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
    private static HashMap<String, ArrayList> scientistAndTaggedSentences = new HashMap<>();

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

        EVTest.testingData(jsonReader, scientists);
    }

    private HashMap<String, ArrayList> cycleThroughScientists(Results scientists) {

        for (Scientist scientist: scientists.getResults()){
            currentScientist = scientist.getTitle();
            HashMap lemmas = this.lemmatize(scientist.getText());
            HashMap<String, ArrayList<Integer>> scientistCheck = this.polyMathCheck(lemmas, musicDictionary,currentScientist);
            this.taggedSentence(scientist, scientistCheck);
        }
        return scientistAndTaggedSentences;
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

    // Need method to cycle through data

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

    public HashMap<String, ArrayList> taggedSentence(Scientist scientist, HashMap<String, ArrayList<Integer>> metaData){
        Annotation document = new Annotation(scientist.getText());
        pipeLine.annotate(document);
        ArrayList<CoreMap> PotentialArtistSentences = new ArrayList<>();
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        ArrayList<Integer> taggedSentencePosition = metaData.get(scientist.getTitle());
        taggedSentencePosition.forEach((k) -> {
            PotentialArtistSentences.add(sentences.get(k - 1));
        });

        scientistAndTaggedSentences.put(currentScientist, PotentialArtistSentences);

        return scientistAndTaggedSentences;
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
                // lemmas.add(token.get(LemmaAnnotation.class));
                lemmaSet.put(token.get(LemmaAnnotation.class), Counter);
            }
        }

        return lemmaSet;
    }
}