package com.za.androidauthenticator.util.calculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.util.SingleTaskExecutor;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CalculateCodeUtil {

    public interface OnUpdateCode {
        void onUpdateCode(List<Integer> codeNumber);
    }

    public interface OnUpdateTimeRemaining {
        void onUpdateTimeRemaining(int time);
    }

    private static final int DEFAULT_TIME_STEP_SECONDS = 30;

    private int numberOfKeys;
    private List<String> keys;
    private CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback;
    private CalculateCodeUtil.OnUpdateCode updateCodeCallback;
    private Timer timer;

    public CalculateCodeUtil(@NonNull List<String> keys,
                             @Nullable CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback,
                             @NonNull CalculateCodeUtil.OnUpdateCode updateCodeCallback) {
        this.keys = keys;
        this.updateTimeCallback = updateTimeCallback;
        this.updateCodeCallback = updateCodeCallback;
        this.numberOfKeys = keys.size();
    }

    public CalculateCodeUtil(@NonNull String key,
                             @Nullable CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback,
                             @NonNull CalculateCodeUtil.OnUpdateCode updateCodeCallback) {
        this(Collections.singletonList(key), updateTimeCallback, updateCodeCallback);
    }

    public void stopCalculate() {
        if (timer != null)
            timer.cancel();
    }

    public void startCalculate() {
        // Calculate remaining time
        int rTimeFirstLoad = calculateRemainingTime();

        SingleTaskExecutor.queueRunnable(() -> {
            if (updateTimeCallback != null)
                updateTimeCallback.onUpdateTimeRemaining(rTimeFirstLoad);
            calculateCodeAndUpdateUI();
        });

        // Repeat infinitely until activity stopped (run in background thread)
        timer = new Timer();
        TimerTask task = new CalculatorTimerTask(rTimeFirstLoad);
        timer.schedule(task, 1000, 1000);
    }

    class CalculatorTimerTask extends TimerTask {
        int rTime;

        public CalculatorTimerTask(int rTime) {
            super();
            this.rTime = rTime;
        }

        @Override
        public void run() {
            // Calculate remaining time
            rTime--;
            if (rTime == 0)
                rTime = 30;

            // Update UI
            if (updateTimeCallback != null)
                updateTimeCallback.onUpdateTimeRemaining(rTime);

            if (rTime == 30)
                calculateCodeAndUpdateUI();
        }
    }

    private int calculateRemainingTime() {
        return (int) (DEFAULT_TIME_STEP_SECONDS -
                ((System.currentTimeMillis() / 1000 + AuthenticatorApp.DIFF_TIME_SECOND)
                        % DEFAULT_TIME_STEP_SECONDS));
    }

    private void calculateCodeAndUpdateUI() {
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

            codes.add(tempCode);
        }
        // Update UI
        updateCodeCallback.onUpdateCode(codes);
    }
}
