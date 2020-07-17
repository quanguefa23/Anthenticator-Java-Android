package com.za.androidauthenticator.data.repository.local;

import androidx.lifecycle.LiveData;

import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.roomdb.AppDatabase;
import com.za.androidauthenticator.data.roomdb.AuthCodeDao;
import com.za.androidauthenticator.util.SingleTaskExecutor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserLocalDataSource {

    AppDatabase appDatabase;
    AuthCodeDao authCodeDao;

    @Inject
    public UserLocalDataSource(AppDatabase appDatabase) {
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
}


// String key = "DJ5W4EBMOXJMYCO3E3KCW4G6CL53VXQ6";