package com.upwork.garrett;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
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
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by garrethdottin on 10/8/16.
 */
public class WordVecDictionary {

    public static void main(String[] args)  throws IOException{
        WordVecDictionary expandedDictionary = new WordVecDictionary();
        String filePath = new ClassPathResource("Bach.txt").getFile().getAbsolutePath();
        SentenceIterator iterator = new BasicLineIterator(filePath);

        TokenizerFactory token = tokenizeData();
        trainModel(token, iterator);
    }

    // Download Libaries
    //
    public void initDictionary () throws IOException {

        WordVecDictionary expandedDictionary = new WordVecDictionary();
        SentenceIterator iterator = expandedDictionary.processText("Bach.txt");
        TokenizerFactory token = tokenizeData();
        trainModel(token, iterator);

    }

    public static SentenceIterator processText(String  file) throws IOException {

        String filePath = new ClassPathResource(file).getFile().getAbsolutePath();
        SentenceIterator iter = new BasicLineIterator(filePath);
        System.out.println("Load & Vectorize Sentences....");
        return iter;
    }

    public static TokenizerFactory tokenizeData(){
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        return t;
    }

    public static void trainModel(TokenizerFactory t,SentenceIterator iter ) {
        System.out.println("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(1)
                .iterations(10)
                .layerSize(300)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
        vec.fit();
        System.out.println("Closest Words:");
        Collection<String> lst = vec.wordsNearest("violin", 6);
        System.out.println(lst);
    }



}
