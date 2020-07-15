package com.za.androidauthenticator.view.activities;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.za.androidauthenticator.databinding.ActivityScanQrBinding;
import com.za.androidauthenticator.di.MyApplication;
import com.za.androidauthenticator.viewmodel.ScanQrViewModel;
import com.za.androidauthenticator.view.base.BaseActivity;

public class ScanQrActivity extends BaseActivity {

    private ScanQrViewModel viewModel;
    private ActivityScanQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ScanQrViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        Log.d(MyApplication.APP_TAG, viewModel.hashCode() + "");

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);
    }

    @Override
    public View bindingView() {
        binding = ActivityScanQrBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}