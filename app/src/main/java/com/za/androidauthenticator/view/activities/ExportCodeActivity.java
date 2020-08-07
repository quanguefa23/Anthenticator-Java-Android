package com.za.androidauthenticator.view.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.za.androidauthenticator.databinding.ActivityExportCodeBinding;
import com.za.androidauthenticator.view.base.BaseActivity;
import com.za.androidauthenticator.viewmodel.ExportCodeViewModel;

public class ExportCodeActivity extends BaseActivity {

    private ExportCodeViewModel viewModel;
    private ActivityExportCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ExportCodeViewModel.class);

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        Bitmap qrImage = viewModel.convertStringToQrCode(getIntent().getStringExtra("data"));
        binding.contentQrCode.setImageBitmap(qrImage);
    }

    @Override
    public View bindingView() {
        binding = ActivityExportCodeBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}