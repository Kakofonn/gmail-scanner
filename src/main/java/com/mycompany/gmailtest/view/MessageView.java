/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gmailtest.view;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.mycompany.gmailtest.dao.MessageDao;
import com.mycompany.gmailtest.dto.Message;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Anatolii
 */
@ManagedBean(name = "messageView")
@SessionScoped
public class MessageView implements Serializable {

    private static String authCode;
    private List<com.google.api.services.gmail.model.Message> messages;
    private List<Message> messageList;
    private String searchName;
    private Boolean connected = false;
    private static Boolean updateData = true;
    private static final String SCOPE = "https://www.googleapis.com/auth/gmail.readonly";
    private static final String APP_NAME = "Gmail test";
    private static final String USER = "me";
    private static GoogleClientSecrets clientSecrets;
    private static final String callbackUrl = "http://localhost:8080/gmail_test/faces/gmail_auth.xhtml";
    private String redirectUrl = "http://localhost:8080/gmail_test/faces/gmail_auth.xhtml";
    private static GoogleAuthorizationCodeFlow flow = null;
    private static HttpTransport httpTransport = new NetHttpTransport();
    private static JsonFactory jsonFactory = new JacksonFactory();
    private static Gmail service;

    public void startGmailService() throws IOException {
        String[] args = {};
        connectToApi(args);
    }

    public void getApiConnection() throws IOException {
        getConnection(authCode);
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
        connected = true;
    }

    public static void connectToApi(String[] args) throws IOException {
        httpTransport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();
        String CLIENT_SECRET_PATH = MessageView.class.getClassLoader().getResource("/client_secret.json").getPath();
        clientSecrets = GoogleClientSecrets.load(jsonFactory, new FileReader(CLIENT_SECRET_PATH));
        // Allow user to authorize via url.
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Arrays.asList(SCOPE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
        String url = flow.newAuthorizationUrl().setRedirectUri(callbackUrl)
                .build();
        updateData = true;
        //Redirect user to the main page
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(url);
    }

    public static void getConnection(String code) throws IOException {
        // Generate Credential using retrieved code.
        GoogleTokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri(callbackUrl).execute();
        GoogleCredential credential = new GoogleCredential()
                .setFromTokenResponse(response);
        // Create a new authorized Gmail API client
        service = new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APP_NAME).build();
    }

    public void loadMessages() throws IOException {
        if (updateData && !searchName.equals("")) {
            ListMessagesResponse messagesResponse = service.users().messages().list(USER).setQ(searchName).execute();
            messages = new ArrayList<>();
            while (messagesResponse.getMessages() != null) {
                messages.addAll(messagesResponse.getMessages());
                if (messagesResponse.getNextPageToken() != null) {
                    String pageToken = messagesResponse.getNextPageToken();
                    messagesResponse = service.users().messages().list(USER).setQ(searchName)
                            .setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }
            System.out.println("Messages found: " + messages.size());
            messageList = new ArrayList<>();
            for (com.google.api.services.gmail.model.Message message : messages) {
                String id = message.getId();
                com.google.api.services.gmail.model.Message textMessage = service.users().messages().get(USER, id).setFormat("full").execute();
                Message emailMessage = new Message();
                emailMessage.setMessageId(id);
                emailMessage.setSnippet(textMessage.getSnippet());
                for (MessagePartHeader header : textMessage.getPayload().getHeaders()) {
                    if (header.getName().equals("From")) {
                        try {
                        String email = header.getValue();
                        email = email.substring(email.indexOf("<") + 1, email.indexOf(">"));
                        emailMessage.setEmail(email);
                        } catch (IndexOutOfBoundsException e) {
                            emailMessage.setEmail(header.getValue());
                        }
                    }
                }
                messageList.add(emailMessage);
            }
            addMessages(messageList);
            updateData = false;
        }
    }

    private void addMessages(List<Message> messages) {
        MessageDao messageDao = new MessageDao();
        messageDao.addMessages(messages);
    }

    public List<Message> getMessagesFromDatabase() {
        MessageDao messageDao = new MessageDao();
        List<Message> messages = messageDao.getMessages();
        return messages;
    }
    
    public void deleteMessage(String id) {
        MessageDao messageDao = new MessageDao();
        messageDao.deleteMessage(id);
    }

    public String deleteAllMessages() {
        MessageDao messageDao = new MessageDao();
        messageDao.deleteMessages();
        updateData = true;
        return "index?faces-redirect=true";
    }

    public static com.google.api.services.gmail.model.Message getMessage(Gmail service, String userId, String messageId)
            throws IOException {
        com.google.api.services.gmail.model.Message message = service.users().messages().get(userId, messageId).execute();
        return message;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Boolean getConnected() {
        return connected;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public List<com.google.api.services.gmail.model.Message> getMessages() {
        return messages;
    }

    public void setMessages(List<com.google.api.services.gmail.model.Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
