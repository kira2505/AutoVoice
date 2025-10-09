package com.autovoice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private final Sheets sheetsService;
    private final String spreadsheetId;

    public GoogleSheetsService(
            @Value("${google.sheets.spreadsheet-id}") String spreadsheetId,
            @Value("${google.sheets.credentials-file:google-credentials.json}") String credentialsFile
    ) throws Exception {
        this.spreadsheetId = spreadsheetId;

        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream(credentialsFile);
        if (credentialsStream == null) {
            throw new IllegalArgumentException("File credentials not found: " + credentialsFile);
        }

        GoogleCredential credential = GoogleCredential.fromStream(credentialsStream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

        sheetsService = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        )
                .setApplicationName("AutoVoice Bot")
                .build();
    }

    public void appendRow(List<Object> rowValues, String sheetName) throws Exception {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(rowValues));
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }
}
