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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

/**
 * Created by satishterala on 9/12/16.
 */
public class EntityNameAnnotationsExample {

    public static void main(String[] args) throws IOException {

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma,ner,regexner  ,parse, dcoref");
        props.put("regexner.mapping", "music_map.tsv");
        props.put("regexner.ignoreCase", "true");

        StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);


        // Next we generate an annotation object that we will use to annotate the text with
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(System.currentTimeMillis());
        // inputText will be the text to evaluate in this example
        URL url = Thread.currentThread().getContextClassLoader().getResource("Albert_Einstein.txt");
        File file = FileUtils.toFile(url);
        String text1 = FileUtils.readFileToString(file);
        //System.out.println("text1 = " + text1);
        //String sampleTxt = "He sat down at his piano and started playing. He continued playing and writing notes for half an hour.At the end of the two weeks, he came downstairs with two sheets of paper bearing his theory.He could also play the esraj, a musical instrument similar to a violin";
        String sampleTxt = "Partial invoice (€100,000, so roughly 40%) for the consignment C27655 we shipped on 15th August to London from the Make Believe Town depot.Customer contact (Sigourney) says they will pay this on the usual credit terms (30 days) And Play Piano and Violin";
        Annotation document = new Annotation(sampleTxt);
        document.set(CoreAnnotations.DocDateAnnotation.class, currentTime);
        // Finally we use the pipeline to annotate the document we created
        pipeLine.annotate(document);

        /* now that we have the document (wrapping our inputText) annotated we can extract the
    annotated sentences from it, Annotated sentences are represent by a CoreMap Object */
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

/* Next we can go over the annotated sentences and extract the annotated words,
    Using the CoreLabel Object */
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Using the CoreLabel object we can start retrieving NLP annotation data
                // Extracting the Text Entity
                String text = token.getString(CoreAnnotations.TextAnnotation.class);

                // Extracting Name Entity Recognition
                String ner = token.getString(CoreAnnotations.NamedEntityTagAnnotation.class);


                // Extracting Part Of Speech
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // Extracting the Lemma
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                System.out.println("text=" + text + ";NER=" + ner +
                        ";POS=" + pos + ";LEMMA=" + lemma);

        /* There are more annotation that are available for extracting
            (depending on which "annotators" you initiated with the pipeline properties",
            examine the token, sentence and document objects to find any relevant annotation
            you might need */
            }

/*
            List<CoreMap> coreMaps = sentence.get(CoreAnnotations.MentionsAnnotation.class);
            for (CoreMap coreMap : coreMaps) {
                coreMap.get()
            }
*/


    /* Next we will extract the SemanitcGraph to examine the connection
       between the words in our evaluated sentence */
            SemanticGraph dependencies = sentence.get
                    (SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

    /* The IndexedWord object is very similar to the CoreLabel object
        only is used in the SemanticGraph context */
            IndexedWord firstRoot = dependencies.getFirstRoot();
            List<SemanticGraphEdge> incomingEdgesSorted =
                    dependencies.getIncomingEdgesSorted(firstRoot);

            for (SemanticGraphEdge edge : incomingEdgesSorted) {
                // Getting the target node with attached edges
                IndexedWord dep = edge.getDependent();

                // Getting the source node with attached edges
                IndexedWord gov = edge.getGovernor();

                // Get the relation name between them
                GrammaticalRelation relation = edge.getRelation();
            }

            // this section is same as above just we retrieve the OutEdges
            List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(firstRoot);
            for (SemanticGraphEdge edge : outEdgesSorted) {
                IndexedWord dep = edge.getDependent();
                System.out.println("Dependent=" + dep);
                IndexedWord gov = edge.getGovernor();
                System.out.println("Governor=" + gov);
                GrammaticalRelation relation = edge.getRelation();
                System.out.println("Relation=" + relation);
            }
        }


    }
}
