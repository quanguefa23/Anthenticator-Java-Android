package com.nhq.authenticator.data.repository.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import com.nhq.authenticator.appcomponent.AuthenticatorApp
import com.nhq.authenticator.data.entity.AuthCode
import com.nhq.authenticator.data.entity.ResponseTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val timeRetrofitService: TimeRetrofitService) {
    private var googleDriveService: GoogleDriveService? = null

    interface SignInCallback {
        fun onSuccess()
    }

    fun handleSignInResult(result: Intent?, context: Context?, callback: SignInCallback) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                    Log.d(AuthenticatorApp.APP_TAG, "Signed in as " + googleAccount.email)
                    createGoogleDriveService(context, googleAccount)
                    callback.onSuccess()
                }
                .addOnFailureListener {
                    exception: Exception? ->
                    Log.e(AuthenticatorApp.APP_TAG, "Unable to sign in", exception)
                }
    }

    private fun createGoogleDriveService(context: Context?, googleAccount: GoogleSignInAccount) {
        // Use the authenticated account to sign in to the Drive service.
        val credential = GoogleAccountCredential
                .usingOAuth2(context, setOf(DriveScopes.DRIVE_APPDATA))
        credential.selectedAccount = googleAccount.account
        val ggDrive = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(), credential)
                .setApplicationName("Drive API Migration")
                .build()
        googleDriveService = GoogleDriveService(ggDrive)
    }

    interface GetListFileCallBack {
        fun onReceive(fileList: FileList?)
        fun onFailure()
    }

    fun getListDataFile(callBack: GetListFileCallBack) {
        googleDriveService!!.queryFiles()
                .addOnSuccessListener { fileList: FileList? -> callBack.onReceive(fileList) }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(AuthenticatorApp.APP_TAG, "Unable to query files.", exception)
                    callBack.onFailure()
                }
    }

    interface GetCodesCallBack {
        fun onReceive(codes: List<AuthCode?>?)
        fun onFailure()
    }

    fun getCodesViaFileId(id: String?, callBack: GetCodesCallBack) {
        googleDriveService!!.readFile(id)
                .addOnSuccessListener { codes: List<AuthCode?>? -> callBack.onReceive(codes) }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(AuthenticatorApp.APP_TAG, "Couldn't read file.", exception)
                    callBack.onFailure()
                }
    }

    interface CreateFileCallBack {
        fun onSuccess()
        fun onFailure()
    }

    fun createDataFile(data: String?, callBack: CreateFileCallBack) {
        googleDriveService!!.createFile(data)
                .addOnSuccessListener { id: String? -> callBack.onSuccess() }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(AuthenticatorApp.APP_TAG, "Couldn't create file.", exception)
                    callBack.onFailure()
                }
    }

    interface GetTimeCallBack {
        fun onResponse(unixTime: Int)
        fun onFailure()
    }

    fun getTime(callBack: GetTimeCallBack) {
        timeRetrofitService.timeViaPublicIp.enqueue(object : Callback<ResponseTime?> {
            override fun onResponse(call: Call<ResponseTime?>,
                                    response: Response<ResponseTime?>) {
                val time = response.body()
                if (time != null) callBack.onResponse(time.unixTime)
            }

            override fun onFailure(call: Call<ResponseTime?>,
                                   t: Throwable) {
                Log.d("QUANG", t.message ?: "")
                callBack.onFailure()
            }
        })
    }
}