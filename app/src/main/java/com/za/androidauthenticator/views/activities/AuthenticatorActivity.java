package com.za.androidauthenticator.views.activities;

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
import com.za.androidauthenticator.viewmodels.AuthenticatorViewModel;
import com.za.androidauthenticator.viewmodels.ViewModelFactory;
import com.za.androidauthenticator.views.base.BaseActivity;

import java.util.ArrayList;

import javax.inject.Inject;

public class AuthenticatorActivity extends BaseActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private AuthenticatorViewModel viewModel;
    private ActivityAuthenticatorBinding binding; // view binding
    private AuthCodeAdapter adapter;
    private boolean firstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindingView());

        // Make Dagger instantiate @Inject fields in AuthenticatorActivity
        ((MyApplication) getApplicationContext()).appGraph.inject(this);
        // Now viewModelFactory is available

        viewModel = new ViewModelProvider(this, viewModelFactory).get(AuthenticatorViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        Log.d(MyApplication.APP_TAG, viewModel.hashCode() + "");

        firstLoad = true;
        prepareRecyclerView();
        setOnClickItem();
        setAdapterSubscribeUI();
        viewModel.updateListCodes();
    }

    @Override
    public View bindingView() {
        binding = ActivityAuthenticatorBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    private void setAdapterSubscribeUI() {
        viewModel.getListCodes().observe(this, list -> {
            adapter.updateDataAndNotify(list);

            if (list == null || list.isEmpty()) {
                binding.emptyView.setVisibility(View.VISIBLE);
            }
            else {
                // Hide empty view
                binding.emptyView.setVisibility(View.INVISIBLE);
                if (firstLoad) {
                    firstLoad = false;
                    // Animation
                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                            AuthenticatorActivity.this, R.anim.layout_animation_from_bottom);
                    binding.recyclerView.setLayoutAnimation(animation);
                }
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
}
