package uk.gov.hmcts.ccd.automated.email.reader;

import java.util.Locale;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class TextSpeech {

    public static void main(String[] args) {

        String[] subjects = args;
        try {
            // Set property as Kevin Dictionary
            System.setProperty(
                "freetts.voices",
                "com.sun.speech.freetts.en.us"
                    + ".cmu_us_kal.KevinVoiceDirectory");

            // Register Engine
            Central.registerEngineCentral(
                "com.sun.speech.freetts"
                    + ".jsapi.FreeTTSEngineCentral");

            // Create a Synthesizer
            Synthesizer synthesizer
                = Central.createSynthesizer(
                new SynthesizerModeDesc(Locale.US));

            // Allocate synthesizer
            synthesizer.allocate();
            synthesizer.enumerateQueue();
            // Resume Synthesizer
            synthesizer.resume();
            System.out.println(subjects[1]);
            //for (int i = 0; i > subjects.length; i++) {
                // Speaks the given text
                // until the queue is empty.
                synthesizer.speakPlainText(
                    args[0], null);
                synthesizer.waitEngineState(
                    Synthesizer.QUEUE_EMPTY);

            // Deallocate the Synthesizer.
            synthesizer.deallocate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

