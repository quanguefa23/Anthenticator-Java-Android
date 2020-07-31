package com.za.androidauthenticator.view.activities;

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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.databinding.ActivityDetailCodeBinding;
import com.za.androidauthenticator.databinding.DialogUpdateCodeBinding;
import com.za.androidauthenticator.di.AuthenticatorApp;
import com.za.androidauthenticator.util.ClipBoardUtil;
import com.za.androidauthenticator.view.base.BaseActivity;
import com.za.androidauthenticator.viewmodel.DetailCodeViewModel;

public class DetailCodeActivity extends BaseActivity {

    private DetailCodeViewModel viewModel;
    private ActivityDetailCodeBinding binding;

    private Dialog dialog;
    private DialogUpdateCodeBinding dialogBinding;
    private AuthCode authCode;
    private static final int NOTIFICATION_ID = 123;
    private Observer<String> pinCodeObserver;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).
                get(DetailCodeViewModel.class);

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        // Update data
        authCode = (AuthCode) getIntent().getSerializableExtra("authCode");
        if (authCode != null) {
            viewModel.setAuthCode(authCode);
            viewModel.updateInfoDataToView();
        }

        // register code pin to notification drawer
        //registerCodePinNotification();
    }

    private void registerCodePinNotification() {
        pinCodeObserver = code -> {
            String title = authCode.siteName + " (" + authCode.accountName + ")";

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, AuthenticatorApp.CODE_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_app)
                            .setContentTitle(title)
                            .setContentText(code)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if (notificationManager == null)
                notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        };

        viewModel.getCodeString().observeForever(pinCodeObserver);
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
        if (authCode != null) {
            viewModel.updateCodeDataToView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(AuthenticatorApp.APP_TAG, "destroy DetailActivity");

//        if (notificationManager == null)
//            notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.cancel(NOTIFICATION_ID);
//        viewModel.stopCalculateCode();
//        viewModel.getCodeString().removeObserver(pinCodeObserver);
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
        if (dialogBinding == null)
            return;

        String siteName = dialogBinding.siteName.getText().toString();
        String accountName = dialogBinding.accountName.getText().toString();

        switch (viewModel.updateCodeInfo(siteName, accountName)) {
            case DetailCodeViewModel.UPDATE_SITE_NAME_ERROR: {
                Toast.makeText(this, R.string.invalid_site_name_error, Toast.LENGTH_SHORT).show();
                break;
            }
            case DetailCodeViewModel.UPDATE_ACCOUNT_NAME_ERROR: {
                Toast.makeText(this, R.string.invalid_account_name_error, Toast.LENGTH_SHORT).show();
                break;
            }
            default: {
                Toast.makeText(this, R.string.update_success_notification, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        }
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