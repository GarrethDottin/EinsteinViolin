package com.upwork.garrett;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.didion.jwnl.data.Exc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by dottig2-adm on 10/5/2016.
 */
public class JSONReadFromFile  {
    public Boolean endofScientistArray = false;
    private Integer currentScientistCount = 0;
    public Results allScientists;


    public static void main(String[] args) throws IOException{

    }

    public Results initScientistsObject() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            JSONParser parser = new JSONParser();
            allScientists = mapper.readValue(new File("/Users/garrethdottin/Desktop/tf-idf-vectorizer/src/main/resources/outputFile.json"), Results.class);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return allScientists;
    }

    public HashMap cycleSelectScientists (Results allScientists, Integer startingPoint, Integer endingPoint) {
        JSONReadFromFile JSONParser = new JSONReadFromFile();
        // Return a Hash with the Scientist Name the an array
        HashMap<String, ArrayList<String>> selectedScientists = new HashMap<>();
        for (Integer i = startingPoint; i < endingPoint; i++){
            Scientist currentScientist = allScientists.getResults().get(i);
            String scientistName = currentScientist.getTitle();
            ArrayList ParsedResults = JSONParser.splitTextBySentence(currentScientist);
            selectedScientists.put(scientistName, ParsedResults);
        }
        return selectedScientists;
    }

    public Scientist indivScientist (Results allScientists) {
        Scientist individualScientist = allScientists.getResults().get(39);
        currentScientistCount++;
        checkEndScientistsObj(allScientists);
        return individualScientist;
    }

    public ArrayList splitTextBySentence (Scientist scientistObj) {
        String text = scientistObj.getText();

        String[] arrayOfScientists = text.split("(?<=[a-z])\\.");
        ArrayList<String> modifiedArrayOfScientists = new ArrayList<String>(Arrays.asList(arrayOfScientists));

        return modifiedArrayOfScientists;
    }

    private void checkEndScientistsObj(Object scientists) {
        if (allScientists.getResults().size()  == currentScientistCount ) {
            endofScientistArray = true;
        }
    }
}