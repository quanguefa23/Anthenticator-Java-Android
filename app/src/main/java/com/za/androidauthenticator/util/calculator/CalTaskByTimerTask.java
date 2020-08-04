package com.za.androidauthenticator.util.calculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation of CalculationTask.
 * It uses Java native classes called Timer and TimerTask to repeating periodic tasks. Tasks are
 * executed in a background thread created by the Timer.
 */
public class CalTaskByTimerTask extends CalculationTask {

    private Timer timer;

    public CalTaskByTimerTask(@NonNull List<String> keys,
                           @Nullable OnUpdateTimeRemaining updateTimeCallback,
                           @NonNull OnUpdateCode updateCodeCallback) {
        super(keys, updateTimeCallback, updateCodeCallback);
    }

    public CalTaskByTimerTask(@NonNull String key,
                           @Nullable OnUpdateTimeRemaining updateTimeCallback,
                           @NonNull OnUpdateCode updateCodeCallback) {
        super(Collections.singletonList(key), updateTimeCallback, updateCodeCallback);
    }

    @Override
    public void stopCalculate() {
        if (timer != null)
            timer.cancel();
    }

    public void startCalculate() {
        int rTimeFirstLoad = initialTask();

        // Repeat infinitely until call stopCalculate (run in background thread)
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
            rTime = task(rTime);
        }
    }
}
