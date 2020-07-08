package com.za.androidauthenticator.views.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.utils.TimeBasedOneTimePasswordUtil;

import java.security.GeneralSecurityException;

public class DetailCodeActivity extends AppCompatActivity {

//    @Inject
//    DetailCodeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_code);

//        // Make Dagger2 instantiate @Inject fields in DetailCodeActivity
//        ((MyApplication) getApplicationContext()).appGraph.inject(this);
//        // Now viewModel is available


        thread = new MyThread();
        thread.start();
    }

    MyThread thread;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.stopThread();
    }

    static class MyThread extends Thread {
        boolean play = true;

        public void stopThread() {
            play = false;
        }

        @Override
        public void run() {
            int DEFAULT_TIME_STEP_SECONDS = 30;
            String base32Secret = "DJ5W4EBMOXJMYCO3E3KCW4G6CL53VXQ6";

            int code = 0;

            while (play) {
                long diff = DEFAULT_TIME_STEP_SECONDS - ((System.currentTimeMillis() / 1000) % DEFAULT_TIME_STEP_SECONDS);
                try {
                    code = TimeBasedOneTimePasswordUtil.generateNumber(base32Secret, System.currentTimeMillis(),
                            DEFAULT_TIME_STEP_SECONDS);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                Log.d("QUANG", "Secret code = " + code + ", change in " + diff + " seconds");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}