package com.nhq.authenticator.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.nhq.authenticator.R;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.appcomponent.service.PinCodeService;
import com.nhq.authenticator.databinding.ActivityDetailCodeBinding;
import com.nhq.authenticator.databinding.DialogUpdateCodeBinding;
import com.nhq.authenticator.util.ClipBoardUtil;
import com.nhq.authenticator.view.base.BaseActivity;
import com.nhq.authenticator.viewmodel.DetailCodeViewModel;

public class DetailCodeActivity extends BaseActivity {

    private DetailCodeViewModel viewModel;
    private ActivityDetailCodeBinding binding;

    private Dialog dialog;
    private DialogUpdateCodeBinding dialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).
                get(DetailCodeViewModel.class);

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        viewModel.setAuthCodeAndUpdateData(getIntent());
    }

    public void pinCodeToNotification() {
        Intent serviceIntent = new Intent(this, PinCodeService.class);
        serviceIntent.putExtra("authCode", viewModel.getAuthCode());
        ContextCompat.startForegroundService(this, serviceIntent);
        Toast.makeText(this, "Your code was pinned to notification drawer",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public View bindingView() {
        binding = ActivityDetailCodeBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void onStop() {
        Log.d(AuthenticatorApp.APP_TAG, "stop DetailActivity");
        super.onStop();
        viewModel.stopCalculateCode();
    }

    @Override
    protected void onStart() {
        Log.d(AuthenticatorApp.APP_TAG, "start DetailActivity");
        super.onStart();
        viewModel.updateCodeDataToView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(AuthenticatorApp.APP_TAG, "destroy DetailActivity");
    }

    public void onCopyCodeToClipBoard(String code) {
        if (ClipBoardUtil.copyStringToClipBoard("2fa",
                code.substring(0, 3) + code.substring(4), this))
            Toast.makeText(this, R.string.copy_code_clipboard, Toast.LENGTH_SHORT).show();
    }

    public void deleteCode() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailCodeActivity.this,
                R.style.AlertDialogTheme);
        alertDialog.setTitle(R.string.remove_account_confirm);
        alertDialog.setMessage(R.string.warning_remove_account);

        alertDialog.setPositiveButton(R.string.remove_account, (dialogInterface, i) -> {
            viewModel.deleteThisAuthCode();
            Toast.makeText(this, R.string.delete_code_success, Toast.LENGTH_LONG).show();
            finish();
        });

        alertDialog.setNegativeButton("Cancel", null);
        alertDialog.show();
    }

    public void editCodeInfo() {
        if (dialog == null) {
            // create view binding instance
            dialogBinding = DialogUpdateCodeBinding.inflate(getLayoutInflater());
            dialogBinding.setMyController(this);
            dialogBinding.setMyViewModel(viewModel);

            dialogBinding.siteName.setText(viewModel.getSiteName().getValue());
            dialogBinding.accountName.setText(viewModel.getAccountName().getValue());

            // create dialog
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogBinding.getRoot());
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    public void onClickCancelDialog() {
        dialog.cancel();
    }

    public void onClickUpdateDialog() {
        if (dialogBinding == null) return;

        String siteName = dialogBinding.siteName.getText().toString();
        String accountName = dialogBinding.accountName.getText().toString();
        viewModel.updateCodeInfo(siteName, accountName, dialog);
    }

    public void exportCode() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailCodeActivity.this,
                R.style.AlertDialogTheme);
        alertDialog.setTitle(R.string.export_account);
        alertDialog.setMessage(R.string.export_code_warning);

        alertDialog.setPositiveButton("Export account", (dialogInterface, i) -> {
            Intent intent = new Intent(DetailCodeActivity.this, ExportCodeActivity.class);
            intent.putExtra("data", viewModel.exportCodeToString());
            startActivity(intent);
        });
        alertDialog.setNegativeButton("Cancel", null);

        alertDialog.show();
    }
}