package com.nhq.authenticator.data.repository.remote

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.nhq.authenticator.data.entity.AuthCode
import com.nhq.authenticator.data.entity.AuthCode.Companion.parseAuthCode
import com.nhq.authenticator.util.SingleTaskExecutor
import com.nhq.authenticator.util.calculator.CalculationTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class GoogleDriveService(private val driveService: Drive) {

    companion object {
        private const val APP_DATA_FILE_NAME_PREFIX = "CodeData-"
    }

    private val executor: Executor = SingleTaskExecutor.getInstance()

    /**
     * Creates a text file in the appDataFolder with content and description and returns its file ID.
     */
    fun createFile(content: String?): Task<String> {
        return Tasks.call(executor, Callable {
            val metadata = File()
                    .setParents(listOf("appDataFolder"))
                    .setMimeType("text/plain")
                    .setName(APP_DATA_FILE_NAME_PREFIX + CalculationTask.getCorrectTimeSecond())
            val contentStream = ByteArrayContent.fromString("text/plain", content)
            val googleFile = driveService.files().create(metadata, contentStream).execute()
                    ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        })
    }

    /**
     * Opens the file identified by `fileId` and returns a [Pair] of its name and
     * contents.
     */
    fun readFile(fileId: String?): Task<List<AuthCode>> {
        return Tasks.call(executor, Callable<List<AuthCode>> {
            driveService.files()[fileId].executeMediaAsInputStream().use { ins ->
                BufferedReader(InputStreamReader(ins)).use { reader ->
//                    val metadata = driveService.files()[fileId].execute()
//                    val des = metadata.description
                    val codes: MutableList<AuthCode> = ArrayList()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val newCode = parseAuthCode(line!!)
                        if (newCode != null) codes.add(newCode)
                    }
                    return@Callable codes
                }
            }
        })
    }

    /**
     * Updates the file identified by `fileId` with the given `content`.
     */
    fun updateFile(fileId: String?, content: String?): Task<Void?> {
        return Tasks.call(executor, Callable<Void?> {

            // Create a File containing any metadata changes.
            val metadata = File().setName(APP_DATA_FILE_NAME_PREFIX + CalculationTask.getCorrectTimeSecond())

            // Convert content to an AbstractInputStreamContent instance.
            val contentStream = ByteArrayContent.fromString("text/plain", content)

            // Update the metadata and contents.
            driveService.files().update(fileId, metadata, contentStream).execute()
            null
        })
    }

    /**
     * Returns a [FileList] containing all the data files in the user's appDataFolder
     */
    fun queryFiles(): Task<FileList> {
        val query = "name contains '$APP_DATA_FILE_NAME_PREFIX'"
        return Tasks.call(executor, Callable {
            driveService.files().list()
                    .setQ(query)
                    .setSpaces("appDataFolder").execute()
        })
    }
}