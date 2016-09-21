package com.upwork.garrett;

import edu.stanford.nlp.util.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class BagofWordsClassifierExample {

    VocabCache vocabCache;
    LabelAwareIterator iterator;
    TokenizerFactory tokenizerFactory;
    HashMap<String, Integer>  vocabWeights = Constants.VOCAB_WEIGHTS;
    String[] stopWords = Constants.STOP_WORDS;

    private static List<String> splitOne(String wordString) {
        return Arrays.asList(wordString.split("#"));
    }

    private static List<List<String>> split(String wordListsString) {
        List<List<String>> result = new ArrayList<List<String>>();
        for (String wordString : wordListsString.split(" ")) {
            result.add(splitOne(wordString));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        BagofWordsClassifierExample app = new BagofWordsClassifierExample();
        ClassPathResource resource = new ClassPathResource("Albert_Einstein.txt");
        InputStream einsteinStrem = resource.getInputStream(); //Thread.currentThread().getContextClassLoader().getResourceAsStream("labeled/muisc/Albert_Einstein.txt");
        String s = IOUtils.toString(einsteinStrem);
        List items = split(s);
        List<List<String>> testing =  CollectionUtils.getNGrams(items, 1,3);

        app.weightDoc(testing);
        app.checkUnlabeledData();


    }

    public Integer weightDoc (List<List<String>> dataSet) {
        Integer weightedScore = 0;
        for (List<String> word : dataSet) {
            String concatenatedWords = Concat(word);
            weightedScore += setWeight(concatenatedWords);
        }
        return weightedScore;
    }
    private String Concat(List<String> word) {
        String finalizedWord = "";
        for (int i = 0; i < word.size(); i++)  {
            finalizedWord +=  word.get(i);
        }

        return finalizedWord;
    }
    private Integer setWeight(String word) {
        Integer indvidualWordWeight = 0;
        for (String key : vocabWeights.keySet() ) {
            if (word.equals(key)){
                indvidualWordWeight += vocabWeights.get(key);
            }
        }
        return indvidualWordWeight;
    }


    private void checkUnlabeledData() {
    }
}
