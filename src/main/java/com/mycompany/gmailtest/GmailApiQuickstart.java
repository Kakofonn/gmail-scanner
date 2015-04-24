/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gmailtest;

/**
 *
 * @author Anatolii
 */
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Thread;
import com.google.api.services.gmail.model.ListThreadsResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class GmailApiQuickstart {

    // Check https://developers.google.com/gmail/api/auth/scopes for all available scopes
    private static final String SCOPE = "https://www.googleapis.com/auth/gmail.readonly";
    private static final String APP_NAME = "Gmail test";
    // Email address of the user, or "me" can be used to represent the currently authorized user.
    private static final String USER = "me";
    // Path to the client_secret.json file downloaded from the Developer Console
    private static final String CLIENT_SECRET_PATH = "C:" + File.separator + "var" + File.separator + "webapp" + File.separator + "images" + File.separator + "client_secret_956423263018-c4a8b50mv71v8n9adp1sdb3gt9i67qti.apps.googleusercontent.com.json";
    
    private static GoogleClientSecrets clientSecrets;
    
    private static final String clientId = "956423263018-bd6i9pvt39hmojm8b535p3h9pugs22gp.apps.googleusercontent.com";
    private static final String clientSecret = "YdolmsEZ4_jifAwpMhnjuBYf";
    private static final String callbackUrl = "http://localhost:8080/gmail_test/gmail_auth.xhtml";
    private static GoogleAuthorizationCodeFlow flow = null;
    private static HttpTransport httpTransport = new NetHttpTransport();
    private static JsonFactory jsonFactory = new JacksonFactory();

    public static void testMessages(String[] args) throws IOException {
        httpTransport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();

        clientSecrets = GoogleClientSecrets.load(jsonFactory, new FileReader(CLIENT_SECRET_PATH));

        // Allow user to authorize via url.
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Arrays.asList(SCOPE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
        System.out.println("Flow is: " + flow.toString());

        String url = flow.newAuthorizationUrl().setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI)
                .build();
        System.out.println("Processing...");
        
        System.out.println("Please open the following URL in your browser then type"
                + " the authorization code:\n" + url);

        // Read code entered by user.
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        

//        // Retrieve a page of Threads; max of 100 by default.
//        ListThreadsResponse threadsResponse = service.users().threads().list(USER).execute();
//        List<Thread> threads = threadsResponse.getThreads();
//
//        // Print ID of each Thread.
//        for (Thread thread : threads) {
//            System.out.println("Thread ID: " + thread.getId());
//        }
    }
    
    public static void getResponse(String code) throws IOException {
        //String code = null;
        System.out.println("Processing...");
        // Generate Credential using retrieved code.
        System.out.println("Flow is: " + flow.toString());
        GoogleTokenResponse response = flow.newTokenRequest(code).execute();
                //.setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential()
                .setFromTokenResponse(response);

        // Create a new authorized Gmail API client
        Gmail service = new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APP_NAME).build();
        
        // Retrieve a page of Threads; max of 100 by default.
        ListThreadsResponse threadsResponse = service.users().threads().list(USER).execute();
        List<Thread> threads = threadsResponse.getThreads();

        // Print ID of each Thread.
        for (Thread thread : threads) {
            System.out.println("Thread ID: " + thread.getId());
        }
    }

}
