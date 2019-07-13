package org.lonewolf2110.utils.kma;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import org.lonewolf2110.enums.FileType;
import org.lonewolf2110.utils.interfaces.IKSGStorage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KSGStorage implements IKSGStorage {
    private static final String APPLICATION_NAME = "KMA Schedule Generator";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "gdrive/tokens";
    private static final String CREDENTIALS_FILE_PATH = "gdrive/credentials.json";
    private static final String KSG_STORAGE_ID = "1JB-yMXH2fHq7QKnEzTifR5KnNzqyqIDr";

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";

    private Drive service;

    public KSGStorage() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    public void delete(String id) throws IOException {
        service.files()
                .delete(id)
                .execute();
    }

    @Override
    public void uploadFile(java.io.File filePath, String parentId, String filename, FileType fileType) throws IOException {
        deleteFile(parentId, filename);

        String mimeType = FileType.getContentType(fileType.getcType());

        File fileMetaData = new File();
        fileMetaData.setName(filename);
        fileMetaData.setMimeType(mimeType);
        fileMetaData.setParents(Collections.singletonList(parentId));

        FileContent fileContent = new FileContent(mimeType, filePath);

        service.files()
                .create(fileMetaData, fileContent)
                .execute();
    }

    @Override
    public List<File> searchFolder(String folder) throws IOException {
        String query = String.format("trashed = false and '%s' in parents and name = '%s'", KSG_STORAGE_ID, folder);
        return q(query);
    }

    @Override
    public File makeFolder(String folder) throws IOException {
        List<File> folderList = searchFolder(folder);

        if (folderList.size() == 0) {
            return createFolder(folder);
        } else {
            return folderList.get(0);
        }

    }

    private void deleteFile(String parentId, String filename) throws IOException {
        String query = String.format("trashed = false and '%s' in parents and name = '%s'", parentId, filename);
        List<File> fileList = q(query);

        for (File file : fileList) {
            delete(file.getId());
        }
    }

    private List<File> q(String query) throws IOException {
        FileList result = service.files().list()
                .setFields("nextPageToken, files(id, webViewLink)")
                .setQ(query)
                .execute();

        return result.getFiles();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = getClass().getClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(
                        new FileDataStoreFactory(
                                new java.io.File(
                                        Objects.requireNonNull(getClass().getClassLoader().getResource(TOKENS_DIRECTORY_PATH))
                                                .getFile()
                                )
                        )
                )
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private File createFolder(String name) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType(FOLDER_MIME_TYPE);
        fileMetadata.setParents(Collections.singletonList(KSG_STORAGE_ID));

        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");

        File file = service.files()
                .create(fileMetadata)
                .setFields("id, webViewLink")
                .execute();

        service.permissions()
                .create(file.getId(), permission)
                .execute();

        return file;
    }
}
