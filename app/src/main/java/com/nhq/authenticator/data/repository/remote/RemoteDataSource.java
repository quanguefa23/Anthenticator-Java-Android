package com.nhq.authenticator.data.repository.remote;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.data.entity.ResponseTime;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteDataSource {
    private final TimeRetrofitService timeRetrofitService;
    private GoogleDriveService googleDriveService;

    @Inject
    public RemoteDataSource(TimeRetrofitService timeRetrofitService) {
        this.timeRetrofitService = timeRetrofitService;
    }

    public interface SignInCallback {
        void onSuccess();
    }

    public void handleSignInResult(Intent result, Context context, RemoteDataSource.SignInCallback callback) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(AuthenticatorApp.APP_TAG, "Signed in as " + googleAccount.getEmail());
                    createGoogleDriveService(context, googleAccount);
                    callback.onSuccess();
                })
                .addOnFailureListener(exception ->
                        Log.e(AuthenticatorApp.APP_TAG, "Unable to sign in", exception));
    }

    public void createGoogleDriveService(Context context, GoogleSignInAccount googleAccount) {
        // Use the authenticated account to sign in to the Drive service.
        GoogleAccountCredential credential = GoogleAccountCredential
                .usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
        credential.setSelectedAccount(googleAccount.getAccount());
        Drive ggDrive = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(), credential)
                .setApplicationName("Drive API Migration")
                .build();

        googleDriveService = new GoogleDriveService(ggDrive);
    }

    public interface GetListFileCallBack {
        void onReceive(FileList fileList);
        void onFailure();
    }

    public void getListDataFile(GetListFileCallBack callBack) {
        googleDriveService.queryFiles()
                .addOnSuccessListener(callBack::onReceive)
                .addOnFailureListener(exception -> {
                    Log.e(AuthenticatorApp.APP_TAG, "Unable to query files.", exception);
                    callBack.onFailure();
                });
    }

    public interface GetCodesCallBack {
        void onReceive(List<AuthCode> codes);
        void onFailure();
    }

    public void getCodesViaFileId(String id, GetCodesCallBack callBack) {
        googleDriveService.readFile(id)
                .addOnSuccessListener(codes -> {
                    callBack.onReceive(codes);
                })
                .addOnFailureListener(exception -> {
                    Log.e(AuthenticatorApp.APP_TAG, "Couldn't read file.", exception);
                    callBack.onFailure();
                });
    }

    public interface CreateFileCallBack {
        void onSuccess();
        void onFailure();
    }

    public void createDataFile(String data, CreateFileCallBack callBack) {
        googleDriveService.createFile(data)
                .addOnSuccessListener(id -> callBack.onSuccess())
                .addOnFailureListener(exception -> {
                    Log.e(AuthenticatorApp.APP_TAG, "Couldn't create file.", exception);
                    callBack.onFailure();
                });
    }

    public interface GetTimeCallBack {
        void onResponse(int unixTime);
        void onFailure();
    }

    public void getTime(GetTimeCallBack callBack) {
        timeRetrofitService.getTimeViaPublicIp().enqueue(new Callback<ResponseTime>() {
            @Override
            public void onResponse(@NonNull Call<ResponseTime> call,
                                   @NonNull Response<ResponseTime> response) {
                ResponseTime time = response.body();
                if (time != null)
                    callBack.onResponse(time.getUnixTime());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseTime> call,
                                  @NonNull Throwable t) {
                Log.d("QUANG", t.getMessage());
                callBack.onFailure();
            }
        });
    }
}