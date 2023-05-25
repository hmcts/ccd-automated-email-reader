package uk.gov.hmcts.reform.automated.email.reader.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ServerDetails {

    public String protocol;
    public String host;
    public int port;
    public int timeout;
    public Boolean mailDebug;
    public Boolean mailDebugAuth;
    public Date lastReloadDate;
    public String lastReloadTime;
    public int intervalTimer;

    public static List<Object> setServerProperties(String emailAddress) throws IOException {
        //todo change 'imap' to %s
        File serverDetailsFile = new File("serverDetails.json");
        if (!serverDetailsFile.exists()) {
            throw new IOException(serverDetailsFile.getName() + " not found");
        }

        ObjectMapper mapper = new ObjectMapper();
        ServerDetails serverDetails = mapper.readValue(serverDetailsFile, ServerDetails.class);

        String host = serverDetails.host;
        //        String host = "outlook.office365.com";
        int port = serverDetails.port;
        String protocol = serverDetails.protocol;

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", protocol);
        props.setProperty("mail.imap.host", host);
        props.setProperty("mail.imap.port", String.valueOf(port));

        props.setProperty("mail.imap.ssl.enable", "true");
        props.setProperty("mail.imap.starttls.enable", "true");
        props.setProperty("mail.imap.auth", "true");
        props.setProperty("mail.imap.user", emailAddress);
        props.setProperty("mail.debug", serverDetails.mailDebug.toString());
        props.setProperty("mail.debug.auth", serverDetails.mailDebugAuth.toString());

        props.setProperty("mail.imap.connectiontimeout", String.valueOf(serverDetails.timeout));
        props.setProperty("mail.imap.timeout", String.valueOf(serverDetails.timeout));

        return List.of(serverDetails, props);
    }
}
