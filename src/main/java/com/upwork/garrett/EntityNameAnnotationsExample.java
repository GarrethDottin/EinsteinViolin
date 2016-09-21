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
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

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

    /*
    * TODO: Refactor from Private Static Methods to Private methods with an instantiated obj
    * TODO: Check if Governor matches something in the dictionary if doesnt match break
    * TODO: If does match check against the music dictionary
    * TODO: Setup a basic weighting model
    *
    *
    * */
    private static Properties createProps() {

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma,ner,regexner  ,parse, dcoref");
        props.put("regexner.mapping","music_map.tsv");
        props.put("regexner.ignoreCase","true");

        return props;
    }

    private  Annotation prepDoc(String inputText) throws IOException{
        // Next we generate an annotation object that we will use to annotate the text with
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(System.currentTimeMillis());
        URL url = Thread.currentThread().getContextClassLoader().getResource("Albert_Einstein.txt");
        File file = FileUtils.toFile(url);
        String text1 = FileUtils.readFileToString(file);

        Annotation document = new Annotation(inputText);
        document.set(CoreAnnotations.DocDateAnnotation.class, currentTime);

        return document;
    }
    private  void getSemanticGraphEdge (List<SemanticGraphEdge> outEdgesSorted) {
        for (SemanticGraphEdge edge : outEdgesSorted) {
            IndexedWord dep = edge.getDependent();
            System.out.println("Dependent=" + dep);
            IndexedWord gov = edge.getGovernor();
            System.out.println("Governor=" + gov);
            GrammaticalRelation relation = edge.getRelation();
            System.out.println("Relation=" + relation);
            // check the governor against
        }
    }


    public static void main(String[] args) throws IOException {
        EntityNameAnnotationsExample EVTest = new EntityNameAnnotationsExample();
        Properties props = EVTest.createProps();
        StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);
        String sampleTxt = "Partial invoice (€100,000, so roughly 40%) for the consignment C27655 we shipped on 15th August to London from the Make Believe Town depot.Customer contact (Sigourney) says they will pay this on the usual credit terms (30 days) And Play Piano and Violin";
        Annotation document = EVTest.prepDoc(sampleTxt);
        pipeLine.annotate(document);

        /* now that we have the document (wrapping our inputText) annotated we can extract the
        annotated sentences from it, Annotated sentences are represent by a CoreMap Object */
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);



        /* Next we can go over the annotated sentences and extract the annotated words,
        Using the CoreLabel Object */
        for (CoreMap sentence : sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);

            }
             /* Next we will extract the SemanitcGraph to examine the connection between the words in our evaluated sentence */
            SemanticGraph dependencies = sentence.get
                        (SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

            /* The IndexedWord object is very similar to the CoreLabel object only is used in the SemanticGraph context */
            IndexedWord firstRoot = dependencies.getFirstRoot();
            List<SemanticGraphEdge> incomingEdgesSorted =
                        dependencies.getIncomingEdgesSorted(firstRoot);

                // this section is same as above just we retrieve the OutEdges
            List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(firstRoot);
            EVTest.getSemanticGraphEdge(outEdgesSorted);

        }

    }
}
