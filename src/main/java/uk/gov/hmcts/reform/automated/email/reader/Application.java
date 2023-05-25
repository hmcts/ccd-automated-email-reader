package uk.gov.hmcts.reform.automated.email.reader;

import uk.gov.hmcts.reform.automated.email.reader.service.EmailReceiverServiceImpl;
import uk.gov.hmcts.reform.automated.email.reader.config.ApplicationParams;
import uk.gov.hmcts.reform.automated.email.reader.helper.ServerDetails;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private final ApplicationParams applicationParams;

    public Application(final ApplicationParams applicationParams) {
        this.applicationParams = applicationParams;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        String emailAddress = applicationParams.getEmailAddress();
        validateEmailAddress(emailAddress);
        EmailReceiverServiceImpl emailReceiver = new EmailReceiverServiceImpl(applicationParams);
        emailReceiver.start(emailAddress, ServerDetails.setServerProperties(emailAddress));
    }

    private static void validateEmailAddress(String emailAddress) {
        // todo validate email input. Throw error if failure
    }
}
