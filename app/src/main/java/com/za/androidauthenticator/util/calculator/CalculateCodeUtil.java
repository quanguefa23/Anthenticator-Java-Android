package com.za.androidauthenticator.util.calculator;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CalculateCodeUtil {

    public interface OnUpdateCode {
        void onUpdateCode(int codeNumber);
    }

    public interface OnUpdateTimeRemaining {
        void onUpdateTimeRemaining(int time);
    }

    private static final int DEFAULT_TIME_STEP_SECONDS = 30;

    private int numberOfKeys;
    private List<String> keys;
    private List<CalculateCodeUtil.OnUpdateTimeRemaining> updateTimeCallbacks;
    private List<CalculateCodeUtil.OnUpdateCode> updateCodeCallbacks;
    private Timer timer;

    public CalculateCodeUtil(List<String> keys,
                             List<CalculateCodeUtil.OnUpdateTimeRemaining> updateTimeCallbacks,
                             List<CalculateCodeUtil.OnUpdateCode> updateCodeCallbacks) {
        this.keys = keys;
        this.updateTimeCallbacks = updateTimeCallbacks;
        this.updateCodeCallbacks = updateCodeCallbacks;
        this.numberOfKeys = keys.size();
    }

    public CalculateCodeUtil(String key,
                             CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback,
                             CalculateCodeUtil.OnUpdateCode updateCodeCallback) {
        this.keys = Collections.singletonList(key);
        this.updateTimeCallbacks = Collections.singletonList(updateTimeCallback);
        this.updateCodeCallbacks = Collections.singletonList(updateCodeCallback);
        this.numberOfKeys = 1;
    }

    public void stopCalculate() {
        if (timer != null)
            timer.cancel();
    }

    public void startCalculate() {
        // Calculate remaining time
        int rTimeFirstLoad = calculateRemainingTime();

        // Update UI
        for (CalculateCodeUtil.OnUpdateTimeRemaining callback : updateTimeCallbacks)
            callback.onUpdateTimeRemaining(rTimeFirstLoad);

        calculateCodeAndUpdateUI();

        // Repeat infinitely until activity destroyed
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
            for (CalculateCodeUtil.OnUpdateTimeRemaining callback : updateTimeCallbacks)
                callback.onUpdateTimeRemaining(rTime);

            if (rTime == 30) {
                calculateCodeAndUpdateUI();
            }
        }
    }

    private int calculateRemainingTime() {
        return (int) (DEFAULT_TIME_STEP_SECONDS -
                ((System.currentTimeMillis() / 1000) % DEFAULT_TIME_STEP_SECONDS));
    }

    private void calculateCodeAndUpdateUI() {
        for (int i = 0; i < numberOfKeys; i++) {
            String key = keys.get(i);

            // Calculate code via key and system time
            int tempCode = 0;
            try {
                tempCode = TimeBasedOneTimePasswordUtil.generateNumber(key,
                        System.currentTimeMillis(), DEFAULT_TIME_STEP_SECONDS);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

            // Update UI
            updateCodeCallbacks.get(i).onUpdateCode(tempCode);
        }
    }
}
