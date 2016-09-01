package com.upwork.garrett;

import org.apache.commons.io.IOUtils;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.dataset.DataSet;
import com.google.common.cache.AbstractCache;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hello world!
 */
public class BagofWordsClassifierExample {

    VocabCache vocabCache;
    LabelAwareIterator iterator;
    TokenizerFactory tokenizerFactory;
    HashMap<String, Integer>  vocabWeights = Constants.VOCAB_WEIGHTS;
    String[] stopWords = Constants.STOP_WORDS;

    public static void main(String[] args) throws Exception {
        BagofWordsClassifierExample app = new BagofWordsClassifierExample();
        app.buildTfIdfMatrix();
        app.checkUnlabeledData();


        System.out.println("Hello World!");
    }

    private void buildTfIdfMatrix() throws IOException {
        ClassPathResource resource = new ClassPathResource("Albert_Einstein.txt");
        vocabCache = new InMemoryLookupCache();
        iterator = new FileLabelAwareIterator.Builder().addSourceFolder(resource.getFile()).build();
        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());


        BagOfWordsVectorizer bagOfWordsVectorizer = new BagOfWordsVectorizer.Builder().setIterator(iterator).setTokenizerFactory(tokenizerFactory).setStopWords(Arrays.asList(stopWords)).setMinWordFrequency(1).setVocab(vocabCache).build();
        InputStream einsteinStrem = resource.getInputStream(); //Thread.currentThread().getContextClassLoader().getResourceAsStream("labeled/muisc/Albert_Einstein.txt");
        String s = IOUtils.toString(einsteinStrem);

        DataSet dataSet = bagOfWordsVectorizer.vectorize(s,"Music");

        System.out.println("dataSet = " + dataSet);
    }

    public Integer weightDoc (ArrayList<String> dataSet) {
        Integer weightedScore = 0;
        for (String word : dataSet) {
            weightedScore += setWeight(word);
        }
        return weightedScore;
    }
    public Integer setWeight(String word) {
        Integer indvidualWordWeight = 0;
        for (String key : vocabWeights.keySet() ) {
            if (word.equals(key)){
                indvidualWordWeight += vocabWeights.get(key);
                break;
            }
        }
        return indvidualWordWeight;
    }


    private void checkUnlabeledData() {
    }
}
