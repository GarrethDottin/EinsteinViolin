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


    private Properties createProps() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma,ner,regexner  ,parse, dcoref");
        props.put("regexner.mapping", "music_map.tsv");
        props.put("regexner.ignoreCase", "true");
        return props;
    }

    private void dissectSentence(List<SemanticGraphEdge> outEdgesSorted) {

        // check the props for the words
        for (SemanticGraphEdge edge : outEdgesSorted) {
            IndexedWord dep = edge.getDependent();
            IndexedWord gov = edge.getGovernor();

            GrammaticalRelation relation = edge.getRelation();
            System.out.println("End of sentence***********");
        }
    }

    private Annotation prepDoc(String fileName) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(System.currentTimeMillis());

        // inputText will be the text to evaluate in this examplefileName
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        File file = FileUtils.toFile(url);
        String text1 = FileUtils.readFileToString(file);

        //String sampleTxt = "He sat down at his piano and started playing. He continued playing and writing notes for half an hour.At the end of the two weeks, he came downstairs with two sheets of paper bearing his theory.He could also play the esraj, a musical instrument similar to a violin";
        String sampleTxt = "Partial invoice (€100,000, so roughly 40%) for the consignment C27655 we shipped on 15th August to London from the Make Believe Town depot.";
        Annotation document = new Annotation(sampleTxt);
        document.set(CoreAnnotations.DocDateAnnotation.class, currentTime);
        return document;
    }


    public static void main(String[] args) throws IOException {
        EntityNameAnnotationsExample EVTest = new EntityNameAnnotationsExample();
        System.out.println("testing");
        Properties props = EVTest.createProps();
        StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);
        Annotation document = EVTest.prepDoc("Albert_Einstein.txt");
        pipeLine.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            /* Next we will extract the SemanticGraph to examine the connection
            between the words in our evaluated sentence */
            SemanticGraph dependencies = sentence.get
                    (SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
            IndexedWord firstRoot = dependencies.getFirstRoot();
            // this section is same as above just we retrieve the OutEdges
            List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(firstRoot);
            EVTest.dissectSentence(outEdgesSorted);
        }
    }
}
