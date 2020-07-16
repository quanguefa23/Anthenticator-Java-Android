package com.za.androidauthenticator.data.repository.local;

import androidx.lifecycle.LiveData;

import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.roomdb.AppDatabase;
import com.za.androidauthenticator.data.roomdb.AuthCodeDao;
import com.za.androidauthenticator.util.SingleTaskExecutor;

import java.util.List;

import javax.inject.Inject;

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
//        List<AuthCode> list = new ArrayList<>();
//        String key = "DJ5W4EBMOXJMYCO3E3KCW4G6CL53VXQ6";
//        // Dummy implement
//        for (int i = 0; i < 5; i++) {
//            list.add(new AuthCode(key, "Google", "nhqnhq" + i + "@gmail.com"));
//        }
        return authCodeDao.loadAllCodes();
    }
}