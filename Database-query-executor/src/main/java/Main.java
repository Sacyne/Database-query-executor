import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.apache.commons.io.FileUtils;

import javax.mail.MessagingException;
import java.io.*;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class Main {


    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {


        Connection connection = SqlConnect.start();
        File directory = new File("queries");
        File logFile = new File("log.txt");
        File[] list = directory.listFiles();
        String query = "";
        String errorMessage = "";
        String[] strings = null;
        Statement statement = null;
        boolean gotError = false;
        int succeededQueries=0;
        int failedQueries=0;
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        try {
            statement = connection.createStatement();
            FileUtils.write(logFile, "\r\rConnection Success\r", Charset.defaultCharset(), true);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            errorMessage = "SQL Connection error : " + e.getMessage();
            FileUtils.write(logFile, errorMessage, Charset.defaultCharset(), true);
            gotError=true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Log.log(e.getMessage());
            gotError=true;
        }

        for (File f : list) {
            try {
                strings = FileUtils.readFileToString(f, Charset.defaultCharset()).replace("\n", " ")
                        .replace("\r", " ").split(";");
                System.out.println(f.getName());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            try {
                //queryList = FileUtils.readLines(f, Charset.defaultCharset());
                for (String s : strings
                ) {
                    try {
                        query = s.trim();
                        System.out.println(query + ";");
                        statement.execute(query + ";");
                        Log.log(query += " : SUCCESS");
                        succeededQueries++;

                    } catch (SQLWarning e) {
                        System.out.println("Warning : ");
                        System.out.println(e.getNextWarning());
                    } catch (SQLException e) {
                        System.out.println("Error :");
                        System.out.println(e.getMessage());
                        Log.log(query += " : Fail: " + e.getMessage());
                        errorMessage="Fail: Check Log File";
                        failedQueries++;
                        gotError = true;
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (gotError) {

            try {
                Mail.sendMessage(service, "me", Mail.createEmail("swordsman94@gmail.com", "alr.leroy@gmail.com", "Rapport Database Cron task", "Failure \r succeeded Queries: "+succeededQueries+"\rFailed Queries: "+failedQueries));
            } catch (MessagingException e) {
                e.printStackTrace();
                Log.log(e.getMessage());
            }
        } else {

            try {
                Mail.sendMessage(service, "me", Mail.createEmail("swordsman94@gmail.com", "alr.leroy@gmail.com", "Rapport Database Cron task", "Success"));
            } catch (MessagingException e) {
                e.printStackTrace();
                Log.log(e.getMessage());
            }
        }


        try {
            statement.close();
            connection.close();
            FileUtils.write(logFile, errorMessage, Charset.defaultCharset(), true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
