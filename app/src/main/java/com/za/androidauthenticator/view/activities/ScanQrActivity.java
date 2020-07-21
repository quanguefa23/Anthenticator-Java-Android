package com.za.androidauthenticator.view.activities;

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
import com.za.androidauthenticator.R;
import com.za.androidauthenticator.databinding.ActivityScanQrBinding;
import com.za.androidauthenticator.di.AuthenticatorApp;
import com.za.androidauthenticator.view.base.BaseActivity;
import com.za.androidauthenticator.viewmodel.ScanQrViewModel;

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
                Toast.makeText(this, R.string.permisstion_denied, Toast.LENGTH_SHORT).show();
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
        switch (viewModel.insertNewCode(rawResult.getText())) {
            case ScanQrViewModel.INSERT_INVALID_URL_ERROR: {
                Toast.makeText(this, R.string.invalid_qr_code, Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
            case ScanQrViewModel.INSERT_KEY_ERROR: {
                Toast.makeText(this, R.string.invalid_key_error, Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
            default: {
                Toast.makeText(this, R.string.scan_qr_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}