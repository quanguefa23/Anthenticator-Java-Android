package com.nhq.authenticator.data.repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.data.repository.local.LocalDataSource;
import com.nhq.authenticator.data.repository.remote.RemoteDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {

    private final LocalDataSource localDataSource;
    private final RemoteDataSource remoteDataSource;

    @Inject
    public Repository(LocalDataSource localDataSource,
                      RemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public void deleteCode(AuthCode authCode) {
        localDataSource.deleteCode(authCode);
    }

    public void saveShowCodePref(Boolean value) {
        localDataSource.saveShowCodePref(value);
    }

    public boolean getShowCodePref() {
        return localDataSource.getShowCodePref();
    }

    public LiveData<List<AuthCode>> getListCodesLocal() {
        return localDataSource.getListCodesLocal();
    }

    public void insertNewCode(String key, String siteName, String accountName) {
        localDataSource.insertNewCode(new AuthCode(key, siteName, accountName));
    }

    public void updateCode(AuthCode authCode) {
        localDataSource.updateCode(authCode);
    }

    public void getListDataFile(RemoteDataSource.GetListFileCallBack callBack) {
        remoteDataSource.getListDataFile(callBack);
    }

    public void insertCodes(List<AuthCode> newCodes) {
        localDataSource.insertCodes(newCodes);
    }

    public void getCodesViaFileId(String id, RemoteDataSource.GetCodesCallBack callBack) {
        remoteDataSource.getCodesViaFileId(id, callBack);
    }

    public void createDataFile(String exportString, RemoteDataSource.CreateFileCallBack callBack) {
        remoteDataSource.createDataFile(exportString, callBack);
    }

    public void getTime(RemoteDataSource.GetTimeCallBack callBack) {
        remoteDataSource.getTime(callBack);
    }

    public void handleSignInResult(Intent result, Context context, RemoteDataSource.SignInCallback callback) {
        remoteDataSource.handleSignInResult(result, context, callback);
    }
}