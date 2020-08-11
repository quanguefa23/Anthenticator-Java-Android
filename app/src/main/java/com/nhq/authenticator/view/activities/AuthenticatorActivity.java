package com.nhq.authenticator.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.nhq.authenticator.R;
import com.nhq.authenticator.adapters.AuthCodeAdapter;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.databinding.ActivityAuthenticatorBinding;
import com.nhq.authenticator.view.base.BaseActivity;
import com.nhq.authenticator.viewmodel.AuthenticatorViewModel;

import java.util.Objects;

public class AuthenticatorActivity extends BaseActivity {

    public static final int REQUEST_CODE_SIGN_IN = 1;
    public static final int REQUEST_CODE_SIGN_IN_RESTORE = 2;
    public static final int REQUEST_CODE_SIGN_IN_BACKUP = 3;

    private AuthenticatorViewModel viewModel;
    private ActivityAuthenticatorBinding binding;
    private final AuthCodeAdapter adapter = new AuthCodeAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(AuthenticatorViewModel.class);

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        observeShowHideView();
        prepareRecyclerView();
        setOnClickItemRecyclerView();
        setAdapterSubscribeUI();
    }

    private void observeShowHideView() {
        adapter.setShowCodesFlag(viewModel.updateShowCodesFlag());
        viewModel.getShowCodesFlag().observe(this, isShowCode -> {
            adapter.setShowCodesFlag(isShowCode);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View bindingView() {
        binding = ActivityAuthenticatorBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private void setAdapterSubscribeUI() {
        viewModel.getListCodes().observe(this, list -> {
            adapter.updateDataAndNotify(list);
            viewModel.stopCalculateCode();
            viewModel.updateDataCodeAllRows(adapter.getListCodes());
        });

        viewModel.getTriggerUpdateTime().observe(this, ignore ->
                adapter.notifyItemRangeChanged(0, adapter.getItemCount(),
                        AuthCodeAdapter.PAYLOAD_TIME));

        viewModel.getTriggerUpdateCode().observe(this, ignore ->
                adapter.notifyItemRangeChanged(0, adapter.getItemCount(),
                        AuthCodeAdapter.PAYLOAD_CODE));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewModel.updateDataCodeAllRows(adapter.getListCodes());
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.stopCalculateCode();
    }

    private void prepareRecyclerView() {
        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setOnClickItemRecyclerView() {
        adapter.setOnItemClickListener((itemView, position) -> {
            Intent intent = new Intent(AuthenticatorActivity.this, DetailCodeActivity.class);
            intent.putExtra("authCode", adapter.getListCodes().get(position));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.accounts);
        viewModel.setTitleShowHideItem(menu.findItem(R.id.show_hide));
        return super.onCreateOptionsMenu(menu);
    }

    //config option item selection in options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_hide:
                configShowHideOption(item);
                return true;
            case R.id.sync_now:
                syncTime();
                return true;
            case R.id.about_sync:
                aboutSync();
                return true;
            case R.id.feedback:
                feedback();
                return true;
            case R.id.how_it_work:
                howItWorks();
                return true;
            case R.id.backup_data:
                backupData();
                return true;
            case R.id.restore_data:
                restoreData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void configShowHideOption(MenuItem item) {
        viewModel.configShowHideOption(item);
    }

    private void feedback() {
        Intent intent = viewModel.createFeedBackIntent();
        startActivity(Intent.createChooser(intent, getString(R.string.choose_mail_app)));
    }

    private void aboutSync() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://support.google.com/accounts/answer/2653433"));
        startActivity(browserIntent);
    }

    private void howItWorks() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/landing/2step/#tab=how-it-works"));
        startActivity(browserIntent);
    }

    private void syncTime() {
        viewModel.syncTime(this);
    }

    public void startEnterKeyActivity() {
        binding.motionLayout.setProgress(0);
        Intent intent = new Intent(AuthenticatorActivity.this, EnterKeyActivity.class);
        startActivity(intent);
    }

    public void startScanQrActivity() {
        binding.motionLayout.setProgress(0);
        Intent intent = new Intent(AuthenticatorActivity.this, ScanQrActivity.class);
        startActivity(intent);
    }

    public void revertMotion() {
        if (binding.motionLayout.getProgress() == 1)
            binding.motionLayout.transitionToStart();
    }

    public void requestSignInGoogle() {
        requestSignInGoogle(REQUEST_CODE_SIGN_IN);
    }

    public void requestSignInGoogle(int requestCode) {
        Log.d(AuthenticatorApp.APP_TAG, getString(R.string.request_sign_in));
        if (requestCode == REQUEST_CODE_SIGN_IN)
            Toast.makeText(AuthenticatorActivity.this, R.string.request_sign_in,
                Toast.LENGTH_SHORT).show();

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(viewModel.getSignInIntent(this), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.d(AuthenticatorApp.APP_TAG, "onActivityResult");

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                case REQUEST_CODE_SIGN_IN_BACKUP:
                    case REQUEST_CODE_SIGN_IN_RESTORE: {
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    viewModel.handleSignInResult(resultData, this, requestCode);
                }
                break;
            }
            default: super.onActivityResult(requestCode, resultCode, resultData);
        }
    }

    private void backupData() {
        if (!viewModel.getSignInFlag().getValue()) {
            requestSignInGoogle(REQUEST_CODE_SIGN_IN_BACKUP);
        }
        else {
            viewModel.backupToGgDrive(this);
        }
    }

    private void restoreData() {
        if (!viewModel.getSignInFlag().getValue()) {
            requestSignInGoogle(REQUEST_CODE_SIGN_IN_RESTORE);
        }
        else {
            viewModel.restoreDataFromGgDrive(this);
        }
    }
}
