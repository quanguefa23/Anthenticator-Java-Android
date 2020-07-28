package com.za.androidauthenticator.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.adapters.AuthCodeAdapter;
import com.za.androidauthenticator.databinding.ActivityAuthenticatorBinding;
import com.za.androidauthenticator.view.base.BaseActivity;
import com.za.androidauthenticator.viewmodel.AuthenticatorViewModel;

public class AuthenticatorActivity extends BaseActivity {

    private AuthenticatorViewModel viewModel;
    private ActivityAuthenticatorBinding binding;

    private final AuthCodeAdapter adapter = new AuthCodeAdapter();
    private boolean showCodesFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(AuthenticatorViewModel.class);

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        setShowCodesFlag();
        prepareRecyclerView();
        setOnClickItem();
        setAdapterSubscribeUI();
    }

    private void setShowCodesFlag() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "AuthenticatorState", MODE_PRIVATE);
        showCodesFlag = sharedPreferences.getBoolean("showCode", true);
        adapter.setShowCodesFlag(showCodesFlag);
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

    private void setOnClickItem() {
        adapter.setOnItemClickListener((itemView, position) -> {
            Intent intent = new Intent(AuthenticatorActivity.this, DetailCodeActivity.class);
            intent.putExtra("authCode", adapter.getListCodes().get(position));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        viewModel.setTitleShowHideItem(menu.findItem(R.id.show_hide), showCodesFlag);
        return super.onCreateOptionsMenu(menu);
    }

    //config option item selection in options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_hide: {
                configShowHideOption(item);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void configShowHideOption(MenuItem item) {
        showCodesFlag = !showCodesFlag;
        new Handler().postDelayed(() ->
                viewModel.setTitleShowHideItem(item, showCodesFlag), 350);

        //remember selection
        SharedPreferences sharedPreferences = getSharedPreferences(
                "AuthenticatorState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("showCode", showCodesFlag);
        editor.apply();

        adapter.setShowCodesFlag(showCodesFlag);
        adapter.notifyDataSetChanged();
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
}
