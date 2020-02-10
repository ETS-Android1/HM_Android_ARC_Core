package com.healthymedium.translations;

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
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TranslationDoc {
    private static final String APPLICATION_NAME = "HealthyMedium Translation Tool";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.dir")+"/tokens";

    public static List<LocaleResource> grabData() throws IOException, GeneralSecurityException {

        System.out.println("building client service");
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1OWIiUaUDlGwbYMKfeE1hvw8BeWM7MolmrokIfja8oYs";
        final String range = "Master Dev Data!B1:H";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        System.out.println("requesting sheet data");
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        // copy the generic structure into something we expect
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("no sheet available, quiting");
            return null;
        }
        System.out.println("sheet data received");

        List<List<String>> data = new ArrayList<>();
        for(List<Object> objectList : values){
            List<String> row = new ArrayList<>();
            for(Object obj : objectList){
                if(obj instanceof String){
                    row.add((String) obj);
                } else {
                    row.add("");
                }
            }
            data.add(row);
        }

        System.out.println("parsing sheet data into more formal structures");
        List<LocaleResource> resources = new ArrayList<>();
        List<String> localeNames = data.get(0);

        for(int i=1;i<localeNames.size();i++) {
            LocaleResource resource = new LocaleResource();
            resource.name = localeNames.get(i);
            resources.add(resource);
        }

        for(int i=1;i<data.size();i++) {
            List<String> row = data.get(i);
            String key = row.get(0);
            for(int j=1;j<row.size();j++) {
                String value = row.get(j);
                resources.get(j-1).map.put(key,value);
            }
        }

        return resources;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = TranslationDoc.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


}
