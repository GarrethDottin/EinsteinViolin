package com.upwork.garrett;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.UiServer;
import java.io.IOException;
import java.util.Collection;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.UiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by garrethdottin on 10/8/16.
 */
public class WordVecDictionary {

    public static void main(String[] args) {

    }

    // Download Libaries
    //
    public void initDictionary () throws IOException {

        WordVecDictionary expandedDictionary = new WordVecDictionary();
        SentenceIterator iterator = expandedDictionary.processText("Bach.txt");
        TokenizerFactory token = tokenizeData();
        trainModel(token, iterator);

    }

    public SentenceIterator processText(String  file) throws IOException {

        String filePath = new ClassPathResource(file).getFile().getAbsolutePath();
        SentenceIterator iter = new BasicLineIterator(filePath);
        System.out.println("Load & Vectorize Sentences....");
        return iter;
    }

    public TokenizerFactory tokenizeData(){
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        return t;
    }

    public void trainModel(TokenizerFactory t,SentenceIterator iter ) {
        System.out.println("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(2)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        System.out.println("Closest Words:");
        Collection<String> lst = vec.wordsNearest("music", 5);
        System.out.println(lst);
    }



}
