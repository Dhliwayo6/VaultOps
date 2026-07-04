//package com.vaultops.config;
//
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.SheetsScopes;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.GoogleCredentials;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.GeneralSecurityException;
//import java.util.Collections;
//
//@Configuration
//public class GoogleSheetsConfig {
//
//    @Value("${google.sheets.credentials.path}")
//    private String credentialsPath;
//
//    @Value("${google.sheets.application-name}")
//    private String applicationName;
//
//    @Bean
//    public Sheets sheetsService() throws IOException, GeneralSecurityException {
//        InputStream in = getClass().getClassLoader()
//                .getResourceAsStream("google-credentials.json");
//
//        if (in == null) {
//            throw new FileNotFoundException("Google credentials file not found");
//        }
//
//        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
//                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
//
//        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
//
//        return new Sheets.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                GsonFactory.getDefaultInstance(),
//                requestInitializer
//        )
//                .setApplicationName(applicationName)
//                .build();
//    }
//}