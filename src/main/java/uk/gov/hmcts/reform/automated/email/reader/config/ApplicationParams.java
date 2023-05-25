package uk.gov.hmcts.reform.automated.email.reader.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ApplicationParams {
    @Value("${email.address}")
    private String emailAddress;

    @Value("${email.password}")
    private String emailPassword;
}
