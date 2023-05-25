package uk.gov.hmcts.reform.automated.email.reader.service;

import com.sun.mail.imap.IMAPFolder;

import uk.gov.hmcts.reform.automated.email.reader.config.ApplicationParams;
import uk.gov.hmcts.reform.automated.email.reader.helper.ServerDetails;
import jakarta.mail.FetchProfile;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@RequiredArgsConstructor
@Service
public class EmailReceiverServiceImpl {
    private final ApplicationParams applicationParams;

    private static final Logger logger = LoggerFactory.getLogger(EmailReceiverServiceImpl.class);
    private ServerDetails serverDetails;
    private Properties props;
    private Folder folder;
    private Store store;

    public void start(String emailAddress, List<Object> connectionConfig) {
        serverDetails = (ServerDetails) connectionConfig.get(0);
        props = (Properties) connectionConfig.get(1);
        Message[] msgs = checkConnection(emailAddress, applicationParams.getEmailPassword());
        if (msgs != null && msgs.length > 0) {
            System.out.println("No of Messages : " + msgs.length);
            checkMessages(msgs);
        }
        logger.debug("no new messages");
        disconnect();
    }

    @SneakyThrows
    private void checkMessages(Message[] msgs) {
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        folder.fetch(msgs, fp);


        for (Message message : msgs) {
            if (message.getReceivedDate().after(serverDetails.lastReloadDate)) {
                System.out.println("==============================");
                IMAPFolder imapFolder = (IMAPFolder) folder;
                System.out.println(imapFolder.getUID(message));
                System.out.println("Sender: " + message.getFrom()[0]);
                System.out.println("Subject: " + message.getSubject());
                break;
            }
        }
    }

    @SneakyThrows
    private Message[] checkConnection(String emailAddress, String userPassword) {

        logger.debug("Ping...");
        Session session = Session.getDefaultInstance(props);
        store = session.getStore();
        store.connect(emailAddress, userPassword);

        folder = store.getFolder("INBOX");
        folder.open(1);

        return folder.getMessages();
    }

    @SneakyThrows
    private void disconnect() {
        folder.close();
        store.close();
    }

    private String getOauthToken() {
        return null;
    }
}
