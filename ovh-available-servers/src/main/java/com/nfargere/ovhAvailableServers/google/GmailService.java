package com.nfargere.ovhAvailableServers.google;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import com.nfargere.ovhAvailableServers.config.AppConfig;
import com.nfargere.ovhAvailableServers.config.Properties;

public class GmailService {
	private static GmailService INSTANCE = null;
	private Gmail gmailService;
	private Logger logger = Logger.getLogger(GmailService.class);
	
	private String applicationName = "Gmail API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private java.io.File dataStoreDir;

	/** Global instance of the {@link FileDataStoreFactory}. */
	private FileDataStoreFactory dataStoreFactory;

	/** Global instance of the JSON factory. */
	private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HttpTransport;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * DATA_STORE_DIR
	 */
	private List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND);
	
	private GmailService() {}
	
	public static synchronized GmailService getInstance() {
		if (INSTANCE == null)
		{
			INSTANCE = new GmailService();
			INSTANCE.initGmailService();
		}
		
		return INSTANCE;
	}
	
	public void sendEmail(String to, String from, String subject, String bodyText) {
		
		try {
			MimeMessage mimeMsg = createEmail(to, from, subject, bodyText);			
			//Message msg = createMessageWithEmail(mimeMsg);
			sendMessage(getGmailService(), "me", mimeMsg);
		} 
		catch (Exception e) {
			logger.error("Error when sending the email: "+e.getMessage());
			logger.error(e);
		}
	}
	
	private void initGmailService() {
		try {
			AppConfig cfg = Properties.getValues();
			
			applicationName = cfg.googleAPIApplicationName();
			
			if(Paths.get(cfg.gmailDataStoreDir()).isAbsolute()) {
				dataStoreDir = new File(cfg.gmailDataStoreDir());
			}
			else {
				URL propFileUrl = Thread.currentThread().getContextClassLoader().getResource(Properties.PROPERTIES_FILE_NAME);
				File parentPropFile = Paths.get(propFileUrl.toURI()).getParent().toFile();				
				dataStoreDir = new File(parentPropFile,cfg.gmailDataStoreDir());
				//dataStoreDir = new File(Thread.currentThread().getContextClassLoader().getResource(cfg.gmailDataStoreDir()).getFile());
			}
			
			HttpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
			
			Credential credential = authorize();
			gmailService = new Gmail.Builder(HttpTransport, jsonFactory, credential).setApplicationName(applicationName).build();
		}
		catch (Throwable t) {
			logger.error("Error during the initialization of the Gmail service: " +t.getMessage());
			logger.error(t);			
		}
	}
	
	private Credential authorize() throws IOException {
		AppConfig cfg = Properties.getValues();
		
		// Load client secrets.
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(cfg.gmailClientSecret());
		if(in == null) {
			in = new FileInputStream(new File(cfg.gmailClientSecret()));
		}
		
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HttpTransport, jsonFactory, clientSecrets, SCOPES)
				.setDataStoreFactory(dataStoreFactory)
				.setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow,new LocalServerReceiver())
				.authorize("user");
		
		logger.info("Credentials saved to "+ dataStoreDir.getAbsolutePath());
		
		return credential;
	}
	
	/**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    private MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
    	java.util.Properties props = new  java.util.Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        //email.setText(bodyText);
        email.setContent(bodyText, "text/html; charset=utf-8");
        return email;
    }
    
    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    private Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
    
    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    private Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }
    
	public Gmail getGmailService() {
		return gmailService;
	}

	public void setGmailService(Gmail gmailService) {
		this.gmailService = gmailService;
	}
	
	public static void main(String[] args) throws IOException {
		getInstance().sendEmail("nicolas.fargere@gmail.com", "nicolas.fargere@gmail.com", "test", "Salut toi.");
//		// Build a new authorized API client service.
//		Gmail service = getInstance().getGmailService();
//
//		// Print the labels in the user's account.
//		String user = "me";
//		ListLabelsResponse listResponse = service.users().labels().list(user)
//				.execute();
//		List<Label> labels = listResponse.getLabels();
//		if (labels.size() == 0) {
//			System.out.println("No labels found.");
//		} else {
//			System.out.println("Labels:");
//			for (Label label : labels) {
//				System.out.printf("- %s\n", label.getName());
//			}
//		}
	}
}
