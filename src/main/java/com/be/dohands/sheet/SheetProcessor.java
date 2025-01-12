package com.be.dohands.sheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SheetProcessor<T> {

    protected abstract TransformResult<T> transformRow(List<Object> rows, Integer sheetRow);
    protected abstract T saveEntity(T entity);

    protected static final String APPLICATION_NAME = "dohands";
    protected static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    protected static final String TOKENS_DIRECTORY_PATH = "tokens";

    protected static final List<String> SCOPES =
        Collections.singletonList(SheetsScopes.SPREADSHEETS);
    protected static final String CREDENTIALS_FILE_PATH = "/credentials.json";


    protected Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
        throws IOException {
        InputStream in = SheetProcessor.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * 시트 읽어와서 디비 연동하는 메서드
     * google api 기반 동작
     */
    @Deprecated
    public void readSheet(String spreadsheetId,String sheetName,String sheetRange, Integer startSheetRow) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String range = sheetName + sheetRange;

        Sheets service =
            new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();

        List<List<Object>> values = response.getValues();
        int startRow = startSheetRow;
        for (List<Object> row : values) {
            TransformResult<T> transformResult = transformRow(row, startRow);
            saveEntity(transformResult.getEntity());
            startRow += 1;
        }
    }

    /**
     * 시트 읽어와서 디비 연동하는 메서드
     * appscript 기반 동작
     *
     * TODO: 나중에 스트림으로 처리해서 알림여부 true인것만 올리는 방안으로 리펙토링
     * @return (엔티티, 알림전송여부) 리스트
     */
    public List<TransformResult<T>> readSheetAndUpdateDb(Map<String, Object> payload){

        List<Map<String, Object>> rowList = (List<Map<String, Object>>) payload.get("data");

        List<TransformResult<T>> results = new ArrayList<>();
        for (Map<String, Object> row : rowList) {

            int rowNumber = (int) row.get("rowNumber");                             // rowNumber 가져오기
            List<Object> rowData = (List<Object>) row.get("rowData");               // rowData 가져오기
            TransformResult<T> transformResult = transformRow(rowData,rowNumber);   // 변환
            T entity = saveEntity(transformResult.getEntity());                     // 저장

            // 저장된 엔티티로 TransformResult 생성
            TransformResult<T> resultWithId = TransformResult.of(entity, transformResult.isNotificationYn());
            results.add(resultWithId);
        }

        return results;
    }

    /**
     * 시트값 수정하는 메서드(앱 변경 -> 시트 자동 반영)
     * credential.json 없어서 현재 사용 불가
     */
    public UpdateValuesResponse updateValues(String spreadsheetId,
        String range,
        String valueInputOption,
        List<List<Object>> values)
        throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service =
            new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        UpdateValuesResponse result = null;

        try {
            ValueRange body = new ValueRange()
                .setValues(values);
            result = service.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption(valueInputOption)
                .execute();
            log.info("cells updated : " + result.getUpdatedCells());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                log.info("Spreadsheet not found with id : " + spreadsheetId);
            } else {
                throw e;
            }
        }

        return result;
    }
}