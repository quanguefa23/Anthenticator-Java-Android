package com.za.androidauthenticator.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.adapters.AuthCodeAdapter;
import com.za.androidauthenticator.databinding.ActivityAuthenticatorBinding;
import com.za.androidauthenticator.di.AuthenticatorApp;
import com.za.androidauthenticator.view.base.BaseActivity;
import com.za.androidauthenticator.viewmodel.AuthenticatorViewModel;

public class AuthenticatorActivity extends BaseActivity {

    private AuthenticatorViewModel viewModel;
    private ActivityAuthenticatorBinding binding;

    private final AuthCodeAdapter adapter = new AuthCodeAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(AuthenticatorApp.APP_TAG, "create");
        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(AuthenticatorViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        //Log.d(AuthenticatorApp.APP_TAG, viewModel.hashCode() + "");

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        prepareRecyclerView();
        setOnClickItem();
        setAdapterSubscribeUI();
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

        viewModel.getTriggerUpdateCode().observe(this, pos ->
                adapter.notifyItemChanged(pos, AuthCodeAdapter.PAYLOAD_CODE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(AuthenticatorApp.APP_TAG, "pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(AuthenticatorApp.APP_TAG, "resume");

        adapter.notifyItemRangeChanged(0, adapter.getItemCount(),
                AuthCodeAdapter.PAYLOAD_TIME);
        adapter.notifyItemRangeChanged(0, adapter.getItemCount(),
                AuthCodeAdapter.PAYLOAD_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(AuthenticatorApp.APP_TAG, "start");
    }

    @Override
    protected void onRestart() {
        Log.d(AuthenticatorApp.APP_TAG, "restart");
        super.onRestart();
        viewModel.updateDataCodeAllRows(adapter.getListCodes());
    }

    @Override
    protected void onDestroy() {
        Log.d(AuthenticatorApp.APP_TAG, "destroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(AuthenticatorApp.APP_TAG, "stop");
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
        return super.onCreateOptionsMenu(menu);
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
