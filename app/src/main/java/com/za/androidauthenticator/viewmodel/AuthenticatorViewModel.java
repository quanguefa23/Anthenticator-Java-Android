package com.za.androidauthenticator.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.za.androidauthenticator.R;
import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.AuthRepository;
import com.za.androidauthenticator.data.repository.remote.AuthRemoteDataSource;
import com.za.androidauthenticator.util.FormatStringUtil;
import com.za.androidauthenticator.util.calculator.CalTaskByHandlerThread;
import com.za.androidauthenticator.util.calculator.CalculationTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticatorViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private LiveData<List<AuthCode>> listCodes;
    MutableLiveData<Boolean> triggerUpdateTime = new MutableLiveData<>();
    MutableLiveData<Boolean> triggerUpdateCode = new MutableLiveData<>();
    private CalculationTask calculationTask;

    public AuthenticatorViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        listCodes = this.authRepository.getAuthLocalDataSource().getListCodesLocal();
    }

    public LiveData<List<AuthCode>> getListCodes() {
        return listCodes;
    }

    public MutableLiveData<Boolean> getTriggerUpdateTime() {
        return triggerUpdateTime;
    }

    public MutableLiveData<Boolean> getTriggerUpdateCode() {
        return triggerUpdateCode;
    }

    public void updateDataCodeAllRows(List<AuthCode> listCodes) {
        if (listCodes.isEmpty()) return;

        CalculationTask.OnUpdateTimeRemaining updateTimeCallback = time -> {
            for (AuthCode item : listCodes)
                item.reTime = time;
            triggerUpdateTime.postValue(true);
        };

        CalculationTask.OnUpdateCode updateCodeCallback = codes -> {
            for (int i = 0; i < listCodes.size(); i++)
                listCodes.get(i).currentCode = FormatStringUtil.formatCodeToStringWithSpace(codes.get(i));
            triggerUpdateCode.postValue(true);
        };

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < listCodes.size(); i++)
            keys.add(listCodes.get(i).key);

        calculationTask = new CalTaskByHandlerThread(keys, updateTimeCallback, updateCodeCallback);
        calculationTask.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculationTask != null)
            calculationTask.stopCalculate();
    }

    public void setTitleShowHideItem(MenuItem showHide, boolean showCode) {
        if (showCode)
            showHide.setTitle(R.string.hide_codes);
        else
            showHide.setTitle(R.string.show_codes);
    }

    public void handleSignInResult(Intent result, Context context, SignInCallBack callBack) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(AuthenticatorApp.APP_TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential = GoogleAccountCredential
                            .usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive ggDrive = new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(), credential)
                            .setApplicationName("Drive API Migration")
                            .build();

                    authRepository.getAuthRemoteDataSource().createGoogleDriveService(ggDrive);
                    callBack.onSuccess();
                    //restoreDataFromGgDrive();
                })
                .addOnFailureListener(exception ->
                        Log.e(AuthenticatorApp.APP_TAG, "Unable to sign in", exception));
    }

    private void restoreDataFromGgDrive() {
        authRepository.getAuthRemoteDataSource().getListDataFile(new AuthRemoteDataSource.GetDataCallBack() {
            @Override
            public void onReceive(FileList fileList) {
                List<File> listFile = fileList.getFiles();
                if (listFile.isEmpty()) {
                    Log.d(AuthenticatorApp.APP_TAG, "Data is empty");
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public interface SyncTimeCallBack {
        void onSuccess();
        void onFailure();
    }

    public interface SignInCallBack {
        void onSuccess();
    }

    public void syncTime(SyncTimeCallBack callBack) {
        authRepository.getAuthRemoteDataSource().
                getTime(new AuthRemoteDataSource.GetTimeCallBack() {
            @Override
            public void onResponse(int unixTime) {
                AuthenticatorApp.DIFF_TIME_SECOND =
                        (int) (unixTime - System.currentTimeMillis() / 1000);
                callBack.onSuccess();
            }

            @Override
            public void onFailure() {
                callBack.onFailure();
            }
        });
    }
}
