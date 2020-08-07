package com.za.androidauthenticator.util.calculator;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.util.SingleTaskExecutor;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Purpose of this abstract class is to calculate code each second.
 * Implementations of the class use different technique to repeating periodic tasks.
 */
public abstract class CalculationTask {

    public interface OnUpdateCode {
        void onUpdateCode(List<Integer> codeNumber);
    }

    public interface OnUpdateTimeRemaining {
        void onUpdateTimeRemaining(int time);
    }

    protected static final int DEFAULT_TIME_STEP_SECONDS = 30;
    protected int numberOfKeys;
    protected List<String> keys;
    protected OnUpdateTimeRemaining updateTimeCallback;
    protected OnUpdateCode updateCodeCallback;

    public CalculationTask(@NonNull List<String> keys,
                           @Nullable OnUpdateTimeRemaining updateTimeCallback,
                           @NonNull OnUpdateCode updateCodeCallback) {
        this.keys = keys;
        this.updateTimeCallback = updateTimeCallback;
        this.updateCodeCallback = updateCodeCallback;
        this.numberOfKeys = keys.size();
    }

    public abstract void stopCalculate();

    public abstract void startCalculate();

    public static int getCorrectTimeSecond() {
        return (int) System.currentTimeMillis() / 1000 + AuthenticatorApp.DIFF_TIME_SECOND;
    }

    protected int CODE_DEBUG = 0;
    protected int calculateRemainingTime() {
        return DEFAULT_TIME_STEP_SECONDS - (getCorrectTimeSecond() % DEFAULT_TIME_STEP_SECONDS);
    }

    protected void calculateCodeAndUpdateUI() {
        List<Integer> codes = new ArrayList<>();
        for (int i = 0; i < numberOfKeys; i++) {
            String key = keys.get(i);

            // Calculate code via key and system time
            int tempCode = 0;
            try {
                tempCode = TimeBasedOTPUtil.generateNumber(key,
                        System.currentTimeMillis() + 1000 * AuthenticatorApp.DIFF_TIME_SECOND,
                        DEFAULT_TIME_STEP_SECONDS);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

            CODE_DEBUG = tempCode;
            codes.add(tempCode);
        }
        // Update UI
        updateCodeCallback.onUpdateCode(codes);
    }

    protected int initialTask() {
        // Calculate remaining time
        int rTimeFirstLoad = calculateRemainingTime();

        SingleTaskExecutor.queueRunnable(() -> {
            if (updateTimeCallback != null)
                updateTimeCallback.onUpdateTimeRemaining(rTimeFirstLoad);
            calculateCodeAndUpdateUI();
        });

        return rTimeFirstLoad;
    }

    protected int task(int rTime) {
        int oldTime = rTime;

        // Calculate remaining time
        rTime = calculateRemainingTime();

        // Update UI
        if (updateTimeCallback != null)
            updateTimeCallback.onUpdateTimeRemaining(rTime);

        if (rTime > oldTime)
            calculateCodeAndUpdateUI();

        Log.d("QUANG", CODE_DEBUG + ": " + rTime);

        return rTime;
    }
}