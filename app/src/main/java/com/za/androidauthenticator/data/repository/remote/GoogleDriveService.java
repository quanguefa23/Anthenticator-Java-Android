package com.za.androidauthenticator.data.repository.remote;

import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.za.androidauthenticator.util.SingleTaskExecutor;
import com.za.androidauthenticator.util.calculator.CalculationTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.Executor;

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
public class GoogleDriveService {
    private static final String APP_DATA_FILE_NAME = "AuthCodeData";
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
                    .setDescription(Integer.toString(CalculationTask.getCorrectTimeSecond()))
                    .setMimeType("text/plain")
                    .setName(APP_DATA_FILE_NAME);
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
    public Task<Pair<String, String>> readFile(String fileId) {
        return Tasks.call(executor, () -> {
            // Retrieve the metadata as a File object.
            File metadata = driveService.files().get(fileId).execute();
            String name = metadata.getName();

            // Stream the file contents to a String.
            try (InputStream is = driveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String contents = stringBuilder.toString();

                return Pair.create(name, contents);
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code content}.
     */
    public Task<Void> updateFile(String fileId, String content) {
        return Tasks.call(executor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File()
                    .setDescription(Integer.toString(CalculationTask.getCorrectTimeSecond()));

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
        return Tasks.call(executor, () ->
                driveService.files().list()
                .setQ("name = '" + APP_DATA_FILE_NAME + "'")
                .setSpaces("appDataFolder").execute());
    }
}