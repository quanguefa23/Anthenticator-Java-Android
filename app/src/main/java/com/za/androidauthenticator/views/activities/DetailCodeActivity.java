package com.za.androidauthenticator.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.model.AuthCode;
import com.za.androidauthenticator.databinding.ActivityAuthenticatorBinding;
import com.za.androidauthenticator.databinding.ActivityDetailCodeBinding;
import com.za.androidauthenticator.di.MyApplication;
import com.za.androidauthenticator.utils.TimeBasedOneTimePasswordUtil;
import com.za.androidauthenticator.viewmodels.AuthenticatorViewModel;
import com.za.androidauthenticator.viewmodels.DetailCodeViewModel;
import com.za.androidauthenticator.viewmodels.ViewModelFactory;
import com.za.androidauthenticator.views.base.BaseActivity;

import java.security.GeneralSecurityException;

import javax.inject.Inject;

public class DetailCodeActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private DetailCodeViewModel viewModel;
    private ActivityDetailCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindingView());

        // Make Dagger instantiate @Inject fields in DetailCodeActivity
        ((MyApplication) getApplicationContext()).appGraph.inject(this);
        // Now viewModelFactory is available

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(DetailCodeViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        Log.d(MyApplication.APP_TAG, viewModel.hashCode() + "");

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);

        AuthCode authCode = (AuthCode) getIntent().getSerializableExtra("authCode");
        if (authCode != null) {
            viewModel.updateInfoData(authCode.getSiteName(), authCode.getAccountName());
        }




        thread = new MyThread();
        //thread.start();
    }

    MyThread thread;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.stopThread();
    }

    @Override
    public View bindingView() {
        binding = ActivityDetailCodeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
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