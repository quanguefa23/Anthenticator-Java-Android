package com.nhq.authenticator.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.Result;
import com.nhq.authenticator.R;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.databinding.ActivityScanQrBinding;
import com.nhq.authenticator.view.base.BaseActivity;
import com.nhq.authenticator.viewmodel.ScanQrViewModel;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQrActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private final int REQUEST_CODE_PERMISSIONS = 10;
    // This is an array of all the permission specified in the manifest.
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private ZXingScannerView scannerView;
    private ScanQrViewModel viewModel;
    private ActivityScanQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ScanQrViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        Log.d(AuthenticatorApp.APP_TAG, viewModel.hashCode() + "");

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        if (allPermissionsGranted()) {
            setupCameraQrScanner();
        }
        else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public View bindingView() {
        binding = ActivityScanQrBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(AuthenticatorApp.APP_TAG, "onRequestResult");
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setupCameraQrScanner();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        Log.d(AuthenticatorApp.APP_TAG, "onResume");
        super.onResume();
        if (scannerView != null) {
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    @Override
    public void onPause() {
        Log.d(AuthenticatorApp.APP_TAG, "onPause");
        super.onPause();
        if (scannerView != null)
            scannerView.stopCamera();
    }

    private void setupCameraQrScanner() {
        scannerView = new ZXingScannerView(this);
        binding.contentCamera.addView(scannerView);
    }

    @Override
    public void handleResult(Result rawResult) {
        viewModel.insertNewCode(rawResult.getText());
        finish();
    }
}