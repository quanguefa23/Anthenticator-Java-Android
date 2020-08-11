package com.nhq.authenticator.data.repository.local;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.data.roomdb.AppDatabase;
import com.nhq.authenticator.data.roomdb.AuthCodeDao;
import com.nhq.authenticator.util.SingleTaskExecutor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;

@Singleton
public class LocalDataSource {

    private static final String SHOW_CODE_PREFERENCES_KEY = "isShowing";
    private static final String PREFERENCES_STATE_NAME = "AuthenticatorState";

    AppDatabase appDatabase;
    AuthCodeDao authCodeDao;

    @Inject
    public LocalDataSource(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        this.authCodeDao = this.appDatabase.authCodeDao();
    }

    public void insertNewCode(AuthCode code) {
        SingleTaskExecutor.queueRunnable(() -> authCodeDao.insertCode(code));
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

    public void saveShowCodePref(Boolean value) {
        SharedPreferences sharedPreferences = AuthenticatorApp.getInstance().getSharedPreferences(
                PREFERENCES_STATE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHOW_CODE_PREFERENCES_KEY, value);
        editor.apply();
    }

    public boolean getShowCodePref() {
        SharedPreferences sharedPreferences = AuthenticatorApp.getInstance().getSharedPreferences(
                PREFERENCES_STATE_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(SHOW_CODE_PREFERENCES_KEY, true);
    }
}