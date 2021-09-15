package uk.gov.hmcts.ccd.automated.email.reader;

import javax.mail.*;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLClientInfoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.time.LocalTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.parser.ParseException;
import org.springframework.cglib.core.Local;


public class EmailReciever {

    /**
     * Returns a Properties object which is configured for a POP3/IMAP server
     *
     * @param protocol either "imap" or "pop3"
     * @param host
     * @param port
     * @return a Properties object
     */
    private Properties getServerProperties(String protocol, String host,
                                           String port) {
        Properties properties = new Properties();

        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(
            String.format("mail.%s.socketFactory.class", protocol),
            "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
            String.format("mail.%s.socketFactory.fallback", protocol),
            "false");
        properties.setProperty(
            String.format("mail.%s.socketFactory.port", protocol),
            String.valueOf(port));

        return properties;
    }

    /**
     * Downloads new messages and fetches details for each message.
     * @param protocol
     * @param host
     * @param port
     * @param userName
     * @param password
     * @param lastReloadDate
     * @param keywords
     */
    public void downloadEmails(String protocol, String host, String port,
                               String userName, String password, LocalDate lastReloadDate, LocalTime lastReloadTime, ArrayList<String> keywords) {
        Properties properties = getServerProperties(protocol, host, port);
        //properties.setProperty("mail.debug", "true");
        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(host, Integer.parseInt(port), userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);

            // fetches new messages from server
            Message[] messages = folderInbox.getMessages();


            checkMessages(messages, lastReloadTime, lastReloadDate, keywords);



            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
    }

    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private String parseAddresses(Address[] address) {
        String listAddress = "";

        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }

        return listAddress;
    }

    /**
     * Test downloading e-mail messages
     */
    public static void main(String[] args) throws IOException {
        JsonParser jsonParser = new JsonParser();
        try {
            while (true) {
                JsonObject details = (JsonObject) jsonParser.parse(new FileReader("Details.JSON"));

                String protocol = details.get("protocol").getAsString();
                String host =  details.get("host").getAsString();
                String port =  details.get("port").getAsString();

                LocalDate lastReloadDate = LocalDate.parse(details.get("lastReloadDate").getAsString());
                LocalTime lastReloadTime = LocalTime.parse(details.get("lastReloadTime").getAsString());

                JsonArray listOfKeywords = details.getAsJsonArray("keywords");
                ArrayList<String> keywords = new ArrayList<>();
                for (int i = 0; i < listOfKeywords.size(); i++) {
                    keywords.add(listOfKeywords.get(i).getAsString());
                }

                //String userName = "alex.ross@HMCTS.net";
                String userName =  details.get("username").getAsString();
                String password =  details.get("password").getAsString();
                //String password = "B873^/keRenU@2j";
                Integer interval = details.get("intervalTimer").getAsInt();

                EmailReciever receiver = new EmailReciever();
                receiver.downloadEmails(protocol, host, port, userName, password, lastReloadDate, lastReloadTime, keywords);
                updateReloadValues.updateReloadValues();
                Thread.sleep(interval * 1000 * 60);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void checkMessages(Message[] messages, LocalTime lastReloadTime, LocalDate lastReloadDate, ArrayList<String> keywords) throws MessagingException {

        ArrayList<String> subjects = new ArrayList<>();
        for (int i = messages.length - 1; i > messages.length - 50; i--) {
            Message msg = messages[i];

            String subject = msg.getSubject();
            LocalDate sentDate = formatDate.formattedDate(msg.getSentDate());


            for (int j = 0; j < keywords.size(); j++) {

                if (subject.contains(keywords.get(j)) & (sentDate.isAfter(lastReloadDate))) {
                    Address[] fromAddress = msg.getFrom();
                    String from = fromAddress[0].toString();
                    String toList = parseAddresses(msg
                        .getRecipients(Message.RecipientType.TO));
                    String ccList = parseAddresses(msg
                        .getRecipients(Message.RecipientType.CC));


                    String contentType = msg.getContentType();
                    String messageContent = "";

                    if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                        try {
                            Object content = msg.getContent();
                            if (content != null) {
                                messageContent = content.toString();
                            }
                        } catch (Exception ex) {
                            messageContent = "[Error downloading content]";
                            ex.printStackTrace();
                        }
                    }

                    // print out details of each message
                    //Apply logic to grab the correct emails. Then output them to the speech engine
                    System.out.println("Message #" + (i + 1) + ":");
                    System.out.println("\t From: " + from);
                    System.out.println("\t To: " + toList);
                    System.out.println("\t CC: " + ccList);
                    System.out.println("\t Subject: " + subject);
                    System.out.println("\t Sent Date: " + sentDate);
                    System.out.println("\t Message: " + messageContent);

                    subjects.add(subject);
                }
            }
        }

        System.out.println("[Completed Search of Emails!]");
        if(subjects.size() != 0) {
            String[] subjectsToFormat = new String[subjects.size()];
            for (int i = 0; i < subjects.size(); i++) {
                subjectsToFormat[i] = subjects.get(i);
            }

            subjectsToTextSpeech.formatSubjects(subjectsToFormat);
        } else {
            System.out.println("[There are currently no new emails that meet the criteria!]");
        }
    }
}

