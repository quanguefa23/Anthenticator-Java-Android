package com.nhq.authenticator.view.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.nhq.authenticator.databinding.ActivityExportCodeBinding;
import com.nhq.authenticator.view.base.BaseActivity;
import com.nhq.authenticator.viewmodel.ExportCodeViewModel;

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