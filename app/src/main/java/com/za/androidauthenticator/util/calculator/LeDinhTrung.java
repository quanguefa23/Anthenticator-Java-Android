package com.za.androidauthenticator.util.calculator;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

public class LeDinhTrung {
    private static final String TAG = "OtherCalculatorTask";

    public static final int MESSAGE_TASK_NEED_RECALCULATING = 1;
    public static final int PENDING = 0;
    public static final int RUNNING = 1;
    public static final int FINISHED = 2;

    // a thì luôn luôn có cái biến state, kiểu int để kiểm soát xem object ntn

    // bấm gì cơ
    private int mState = PENDING;
    public void setState(int state) {
        mState = state;
        notifyTaskChanged();
    }

    // ham nay dam bao chi dc goi mot lan tho
    // co do k vay
    public void start() {
        mCalculatorThread = new HandlerThread("handlerThread"+ View.generateViewId());// viet code tang id len nha, nay xai dai
        mCalculatorThread.start();
        mCalculatorHandler = new CalculatorHandler(mCalculatorThread.getLooper());
        setState(RUNNING);
    }

    public void release() {

        // release handler thread sau khi khong dung nua
        if(mCalculatorHandler != null) {
            mCalculatorHandler.removeMessages(MESSAGE_TASK_NEED_RECALCULATING);
            mCalculatorHandler.getLooper().quitSafely(); // này là bỏ loo
            mCalculatorHandler = null;
        }

        if(mCalculatorThread != null) {
            mCalculatorThread.quitSafely();
            mCalculatorThread = null;
            // xong
            // goi start de no chay
            // goi release de no kill
        }
    }

    private void repeatTask() {
        notifyTaskChanged();
    }

    public void notifyTaskChanged() {
        if(mCalculatorHandler == null) {
            return;
        }

        if(mState == RUNNING)
            mCalculatorHandler.sendEmptyMessageDelayed(MESSAGE_TASK_NEED_RECALCULATING, 2500);
    }

    // em can reference de quan ly vong doi
    // end task thi phai destroy no di
    private HandlerThread mCalculatorThread;
    private CalculatorHandler mCalculatorHandler;

    private class CalculatorHandler extends Handler {
        public CalculatorHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
          // xu ly logic tuy message
            switch (msg.what) {
                case MESSAGE_TASK_NEED_RECALCULATING:
                    // gọi hàm tính toán lại
                    Log.d(TAG, "oh, I need to update otp again !!");

                    // loop lai
                    if(mState == RUNNING)
                        repeatTask();
                    break;
            }
        }
    }
}
