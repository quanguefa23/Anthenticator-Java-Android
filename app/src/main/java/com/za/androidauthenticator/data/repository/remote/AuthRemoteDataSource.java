package com.za.androidauthenticator.data.repository.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.data.entity.ResponseTime;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRemoteDataSource {
    private final TimeRetrofitService timeRetrofitService;
    private GoogleDriveService googleDriveService;

    @Inject
    public AuthRemoteDataSource(TimeRetrofitService timeRetrofitService) {
        this.timeRetrofitService = timeRetrofitService;
    }

    public void createGoogleDriveService(Drive driveService) {
        googleDriveService = new GoogleDriveService(driveService);
    }

    public void getListDataFile(GetDataCallBack callBack) {
        googleDriveService.queryFiles()
                .addOnSuccessListener(callBack::onReceive)
                .addOnFailureListener(exception -> {
                    Log.e(AuthenticatorApp.APP_TAG, "Unable to query files.", exception);
                    callBack.onFailure();
                });
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

    public interface GetDataCallBack {
        void onReceive(FileList fileList);
        void onFailure();
    }

    public interface GetTimeCallBack {
        void onResponse(int unixTime);
        void onFailure();
    }
}