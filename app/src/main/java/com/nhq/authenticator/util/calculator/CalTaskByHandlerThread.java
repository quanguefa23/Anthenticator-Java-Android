package com.nhq.authenticator.util.calculator;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of CalculationTask.
 * It utilizes a handler with looper from a handlerThread. Tasks are executed via handler and looper
 * in a background thread created by the handlerThread.
 */
public class CalTaskByHandlerThread extends CalculationTask {

    private static final int MESSAGE_RECALCULATING = 1;
    private static final int RUNNING = 2;
    private static final int FINISHED = 3;
    private static final int PENDING = 4;
    private static int calId = 0;

    private HandlerThread calculatorThread;
    private CalculatorHandler calculatorHandler;
    private int state = PENDING;

    public CalTaskByHandlerThread(@NonNull List<String> keys,
                              @Nullable OnUpdateTimeRemaining updateTimeCallback,
                              @NonNull OnUpdateCode updateCodeCallback) {
        super(keys, updateTimeCallback, updateCodeCallback);
    }

    public CalTaskByHandlerThread(@NonNull String key,
                              @Nullable OnUpdateTimeRemaining updateTimeCallback,
                              @NonNull OnUpdateCode updateCodeCallback) {
        super(Collections.singletonList(key), updateTimeCallback, updateCodeCallback);
    }

    @Override
    public void stopCalculate() {
        // release handler
        if (calculatorHandler != null) {
            calculatorHandler.removeMessages(MESSAGE_RECALCULATING);
            calculatorHandler.getLooper().quitSafely();  // quit looper
            calculatorHandler = null;
        }

        // release thread
        if (calculatorThread != null) {
            calculatorThread.quitSafely();
            calculatorThread = null;
        }

        state = FINISHED;
    }

    @Override
    public void startCalculate() {
        int rTimeFirstLoad = initialTask();

        calId++;
        calculatorThread = new HandlerThread("calculator" + calId);
        calculatorThread.start();
        calculatorHandler = new CalculatorHandler(calculatorThread.getLooper(), rTimeFirstLoad);

        state = RUNNING;
        repeatTask();
    }

    private class CalculatorHandler extends Handler {
        int rTime;

        public CalculatorHandler(@NonNull Looper looper, int rTime) {
            super(looper);
            this.rTime = rTime;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MESSAGE_RECALCULATING) {
                rTime = task(rTime);
                // loop calculation task
                if(state == RUNNING)
                    repeatTask();
            }
        }
    }

    private void repeatTask() {
        if (calculatorHandler == null)
            return;

        if (state == RUNNING)
            calculatorHandler.sendEmptyMessageDelayed(MESSAGE_RECALCULATING, 1000);
    }
}
