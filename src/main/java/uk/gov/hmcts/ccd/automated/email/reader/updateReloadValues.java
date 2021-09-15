package uk.gov.hmcts.ccd.automated.email.reader;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class updateReloadValues {
    public static void main(String[] args) throws IOException {
        updateReloadValues();
    }

    public static void updateReloadValues() throws IOException {
        String path = "Details.JSON";

        String newDate = LocalDate.now().toString();
        String newTime = LocalTime.now().toString().substring(0, 8);

        ObjectMapper mapper = new ObjectMapper();

        JSONObject jsonDetails = mapper.readValue(new File(path), JSONObject.class);

        jsonDetails.put("lastReloadDate", newDate);
        jsonDetails.put("lastReloadTime", newTime);

        //Write into the file
        try (FileWriter file = new FileWriter(path))
        {
            file.write(jsonDetails.toString());
            System.out.println("Successfully updated json object to file...!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
