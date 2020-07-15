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
import com.za.androidauthenticator.di.MyApplication;
import com.za.androidauthenticator.viewmodel.AuthenticatorViewModel;
import com.za.androidauthenticator.view.base.BaseActivity;

import java.util.ArrayList;

public class AuthenticatorActivity extends BaseActivity {

    private AuthenticatorViewModel viewModel;
    private ActivityAuthenticatorBinding binding;

    private AuthCodeAdapter adapter;
    private boolean firstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(AuthenticatorViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        Log.d(MyApplication.APP_TAG, viewModel.hashCode() + "");

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        firstLoad = true;
        prepareRecyclerView();
        setOnClickItem();
        setAdapterSubscribeUI();
        viewModel.updateListCodes();
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
            if (firstLoad) {
                firstLoad = false;
                // Animation
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                        AuthenticatorActivity.this, R.anim.layout_animation_from_bottom);
                binding.recyclerView.setLayoutAnimation(animation);
            }
        });
    }

    private void prepareRecyclerView() {
        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);

        adapter = new AuthCodeAdapter(this, R.layout.content_authcodes_row, new ArrayList<>());
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
