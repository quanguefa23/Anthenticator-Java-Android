package com.za.androidauthenticator.appcomponent.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;

public class PinCodeService extends Service {

    private CalculateCodeUtil calculateCodeUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Update data
        AuthCode authCode = (AuthCode) intent.getSerializableExtra("authCode");
        if (authCode != null) {
            // Register callback to update UI (time)
            CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback = time -> {
            };

            // Register callback to update UI (code)
            CalculateCodeUtil.OnUpdateCode updateCodeCallback = codes -> {
                Log.d(AuthenticatorApp.APP_TAG, "Code: " + codes.get(0));
            };

            calculateCodeUtil = new CalculateCodeUtil(authCode.key, updateTimeCallback, updateCodeCallback);
            calculateCodeUtil.startCalculate();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        calculateCodeUtil.stopCalculate();
    }
}
