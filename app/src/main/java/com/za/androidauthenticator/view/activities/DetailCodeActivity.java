package com.za.androidauthenticator.view.activities;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.databinding.ActivityDetailCodeBinding;
import com.za.androidauthenticator.di.MyApplication;
import com.za.androidauthenticator.view.base.BaseActivity;
import com.za.androidauthenticator.viewmodel.DetailCodeViewModel;

public class DetailCodeActivity extends BaseActivity {

    private DetailCodeViewModel viewModel;
    private ActivityDetailCodeBinding binding;

    private AuthCode authCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(DetailCodeViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        Log.d(MyApplication.APP_TAG, viewModel.hashCode() + "");

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        // Update data
        authCode = (AuthCode) getIntent().getSerializableExtra("authCode");
        if (authCode != null) {
            viewModel.setAuthCode(authCode);
            viewModel.updateInfoData();
            viewModel.updateCodeData();
        }
    }

    @Override
    public View bindingView() {
        binding = ActivityDetailCodeBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stopCalculateCode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (authCode != null) {
            viewModel.updateCodeData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopCalculateCode();
    }

    public void onCopyCodeToClipBoard(String code) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("2fa",code.substring(0, 3) + code.substring(4));
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.copy_code_clipboard, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteCode() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailCodeActivity.this);
        alertDialog.setTitle(R.string.remove_account_confirm);
        alertDialog.setMessage(R.string.warning_remove_account);

        alertDialog.setPositiveButton(R.string.remove_account, (dialogInterface, i) -> {
            viewModel.deleteThisAuthCode();
            Toast.makeText(this, R.string.delete_code_success, Toast.LENGTH_SHORT).show();
            finish();
        });

        alertDialog.setNegativeButton("Cancel", null);
        alertDialog.show();
    }

    public void editCodeInfo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_code);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}