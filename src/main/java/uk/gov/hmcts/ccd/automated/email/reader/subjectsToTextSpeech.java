package uk.gov.hmcts.ccd.automated.email.reader;

import java.util.ArrayList;

public class subjectsToTextSpeech {
    public static void formatSubjects(String[] subjects){
        for (int i = 0; i < subjects.length; i++) {
            System.out.println(subjects[i]);
        }
        System.out.println("Done!");
    }
}
