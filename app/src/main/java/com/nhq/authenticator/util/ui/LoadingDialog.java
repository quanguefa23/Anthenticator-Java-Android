package com.nhq.authenticator.util.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.ProgressBar;

import com.nhq.authenticator.R;


public class LoadingDialog {
    Context context;
    Dialog loadingDialog;
    CountDownTimer timer;

    public LoadingDialog(Context context) {
        this.context = context;
    }

    public void start() {
        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final ProgressBar circle = loadingDialog.findViewById(R.id.circle_progress);

        timer = new CountDownTimer(50, 1000) {
            @Override
            public void onTick(long l) {
                int progress = circle.getProgress() % 100 + 5;
                circle.setProgress(progress);

                float rotation = circle.getRotation() % 360 + 9;
                circle.setRotation(rotation);
            }

            @Override
            public void onFinish() {
                this.start();
            }
        };

        timer.start();
        loadingDialog.show();
    }

    public void stop() {
        if (timer != null)
            timer.cancel();
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }
}
