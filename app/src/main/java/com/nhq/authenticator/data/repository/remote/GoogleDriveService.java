package com.nhq.authenticator.data.repository.remote;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.util.SingleTaskExecutor;
import com.nhq.authenticator.util.calculator.CalculationTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class GoogleDriveService {
    private static final String APP_DATA_FILE_NAME_PREFIX = "CodeData-";
    private final Executor executor = SingleTaskExecutor.getInstance();
    private final Drive driveService;

    public GoogleDriveService(Drive driveService) {
        this.driveService = driveService;
    }

    /**
     * Creates a text file in the appDataFolder with content and description and returns its file ID.
     */
    public Task<String> createFile(String content) {
        return Tasks.call(executor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList("appDataFolder"))
                    .setMimeType("text/plain")
                    .setName(APP_DATA_FILE_NAME_PREFIX + CalculationTask.getCorrectTimeSecond());
            ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

            File googleFile = driveService.files().create(metadata, contentStream).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }

            return googleFile.getId();
        });
    }

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<List<AuthCode>> readFile(String fileId) {
        return Tasks.call(executor, () -> {
            // Stream the file contents to a String.
            try (InputStream is = driveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                File metadata = driveService.files().get(fileId).execute();
                String des = metadata.getDescription();

                List<AuthCode> codes = new ArrayList<>();
                String line;

                while ((line = reader.readLine()) != null) {
                    AuthCode newCode = AuthCode.parseAuthCode(line);
                    if (newCode != null)
                        codes.add(newCode);
                }
                return codes;
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code content}.
     */
    public Task<Void> updateFile(String fileId, String content) {
        return Tasks.call(executor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File().
                    setName(APP_DATA_FILE_NAME_PREFIX + CalculationTask.getCorrectTimeSecond());

            // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

            // Update the metadata and contents.
            driveService.files().update(fileId, metadata, contentStream).execute();
            return null;
        });
    }

    /**
     * Returns a {@link FileList} containing all the data files in the user's appDataFolder
     */
    public Task<FileList> queryFiles() {
        String query = "name contains '" + APP_DATA_FILE_NAME_PREFIX + "'";
        return Tasks.call(executor, () ->
                driveService.files().list()
                .setQ(query)
                .setSpaces("appDataFolder").execute());
    }
}