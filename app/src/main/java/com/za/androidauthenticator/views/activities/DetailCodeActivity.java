package com.za.androidauthenticator.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.za.androidauthenticator.data.model.AuthCode;
import com.za.androidauthenticator.databinding.ActivityDetailCodeBinding;
import com.za.androidauthenticator.di.MyApplication;
import com.za.androidauthenticator.viewmodels.DetailCodeViewModel;
import com.za.androidauthenticator.viewmodels.ViewModelFactory;
import com.za.androidauthenticator.views.base.BaseActivity;

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
        binding.setMyController(this);

        // Update data
        AuthCode authCode = (AuthCode) getIntent().getSerializableExtra("authCode");
        if (authCode != null) {
            viewModel.updateInfoData(authCode.getSiteName(), authCode.getAccountName());
            viewModel.updateCodeData(authCode.getKey());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopCalculateCode();
    }

    @Override
    public View bindingView() {
        binding = ActivityDetailCodeBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}