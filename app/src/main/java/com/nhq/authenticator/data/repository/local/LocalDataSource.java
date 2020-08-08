package com.nhq.authenticator.data.repository.local;

import androidx.lifecycle.LiveData;

import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.data.roomdb.AppDatabase;
import com.nhq.authenticator.data.roomdb.AuthCodeDao;
import com.nhq.authenticator.util.SingleTaskExecutor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalDataSource {

    AppDatabase appDatabase;
    AuthCodeDao authCodeDao;

    @Inject
    public LocalDataSource(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        this.authCodeDao = this.appDatabase.authCodeDao();
    }

    public void insertNewCode(String key, String siteName, String accountName) {
        SingleTaskExecutor.queueRunnable(() ->
            authCodeDao.insertCode(new AuthCode(key, siteName, accountName)));
    }

    public LiveData<List<AuthCode>> getListCodesLocal() {
        return authCodeDao.loadAllCodes();
    }

    public void deleteCode(AuthCode authCode) {
        SingleTaskExecutor.queueRunnable(() -> authCodeDao.deleteCode(authCode));
    }

    public void insertCodes(List<AuthCode> codes) {
        SingleTaskExecutor.queueRunnable(() -> authCodeDao.insertCodes(codes));
    }

    public void updateCode(AuthCode authCode) {
        SingleTaskExecutor.queueRunnable(() -> authCodeDao.updateCode(authCode));
    }
}