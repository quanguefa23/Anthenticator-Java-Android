package com.nhq.authenticator.data.repository.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.data.entity.ResponseTime;

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

    public void createGoogleDriveService(Drive driveService) {
        googleDriveService = new GoogleDriveService(driveService);
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