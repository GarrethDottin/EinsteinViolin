package com.upwork.garrett;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class ReadCSV {


    public static void  main(String[] args) {

    }
    public HashMap<String, String> createDictionaryHash () {
        String csvFile = "/Users/garrethdottin/Desktop/tf-idf-vectorizer/src/main/resources/musicalInstruments.csv";
        String line = "";
        String cvsSplitBy = ",";
        HashMap<String, String> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);

                list.put(country[0].toLowerCase(), country[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(list);
        return list;
    }

}