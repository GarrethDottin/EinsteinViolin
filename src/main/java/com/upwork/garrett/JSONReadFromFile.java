package com.upwork.garrett;

import net.didion.jwnl.data.Exc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dottig2-adm on 10/5/2016.
 */
public class JSONReadFromFile  {
    public Boolean endofScientistArray = false;
    private Integer currentScientistCount = 0;

    public static void main(String[] args) throws IOException{
        // Needs to take in an array of JSON COM
        // loop over each json array and return the JSON object (Each time you call it, it should return you the next item in the file
        // While a certain value is true (Include this in the main class)
        // Splice up the Text into manageable chunks
    }

    public JSONArray initObjects(String[] args) throws IOException {
        JSONArray scientists = new JSONArray();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("C:/Users/dottig2-adm/Desktop/EinsteinViolin/src/main/classifiers/Scientists"));
            JSONObject jsonObject = (JSONObject) obj;
            scientists = (JSONArray) jsonObject.get("scientists");
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return scientists;
    }

    private  JSONObject indivObj (JSONArray scientists) {
        JSONObject individualScientist = (JSONObject) scientists.get(currentScientistCount);
        currentScientistCount++;
        checkEndObj(scientists);
        return individualScientist;
    }

    private void checkEndObj(JSONArray scientists) {
        if (scientists.size()  == currentScientistCount ) {
            endofScientistArray = true;
        }
    }
}
