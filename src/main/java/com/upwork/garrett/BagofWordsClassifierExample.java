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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.sequencevectors.SequenceVectors;
import org.deeplearning4j.models.sequencevectors.iterators.AbstractSequenceIterator;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabConstructor;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    }

    private AbstractCache createCache() throws IOException {
        ClassPathResource resource = new ClassPathResource("Albert_Einstein.txt");
        File file = resource.getFile();
        AbstractCache<VocabWord> vocabCache =  new AbstractCache.Builder<VocabWord>().build();

        /*
            First we build line iterator
         */
        BasicLineIterator underlyingIterator = new BasicLineIterator(file);
                /*
            Now we need the way to convert lines into Sequences of VocabWords.
            In this example that's SentenceTransformer
         */
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        SentenceTransformer transformer = new SentenceTransformer.Builder()
                .iterator(underlyingIterator)
                .tokenizerFactory(t)
                .build();


        /*
            And we pack that transformer into AbstractSequenceIterator
         */
        AbstractSequenceIterator<VocabWord> sequenceIterator =
                new AbstractSequenceIterator.Builder(transformer).build();

        /*
            Now we should build vocabulary out of sequence iterator.
            We can skip this phase, and just set AbstractVectors.resetModel(TRUE), and vocabulary will be mastered internally
        */
        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>()
                .addSource(sequenceIterator, 5)
                .setTargetVocabCache(vocabCache)
                .build();

        constructor.buildJointVocabulary(false, true);

        return vocabCache;

    }

    private void buildTfIdfMatrix() throws IOException {
        ClassPathResource resource = new ClassPathResource("Albert_Einstein.txt");
        File file = resource.getFile();

        iterator = new FileLabelAwareIterator.Builder().addSourceFolder(resource.getFile()).build();
        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        AbstractCache<VocabWord> vocabCache = createCache();

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
