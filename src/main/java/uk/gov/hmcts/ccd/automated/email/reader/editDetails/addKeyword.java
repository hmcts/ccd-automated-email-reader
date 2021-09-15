package uk.gov.hmcts.ccd.automated.email.reader.editDetails;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class addKeyword {

    public static void main (String[] args){

        ObjectMapper mapper = new ObjectMapper();
        String path = "Details.JSON";
        try {

            JSONObject jsonFile = mapper.readValue(new File(path), JSONObject.class);
            JSONArray listOfKeywords = (JSONArray) jsonFile.get("keywords");
            ArrayList<String> keywords = new ArrayList<>();
            for (int i = 0; i < listOfKeywords.size(); i++) {
                keywords.add((String) listOfKeywords.get(i));
            }

            for (int i = 0; i < args.length; i++) {
                keywords.add(args[i]);
            }

            jsonFile.put("keywords", keywords);
            System.out.println("[New keywords have been added!]");

        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
