package com.za.androidauthenticator.view.activities;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.contract.ListSitesAvailable;
import com.za.androidauthenticator.databinding.ActivityEnterKeyBinding;
import com.za.androidauthenticator.di.MyApplication;
import com.za.androidauthenticator.util.SetUIHideKeyboard;
import com.za.androidauthenticator.viewmodel.EnterKeyViewModel;
import com.za.androidauthenticator.view.base.BaseActivity;

import java.util.List;

public class EnterKeyActivity extends BaseActivity {

    private EnterKeyViewModel viewModel;
    private ActivityEnterKeyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewModel instance
        viewModel = new ViewModelProvider(this, viewModelFactory).get(EnterKeyViewModel.class);

        // Test configuration change -> must not change hashcode of viewModel every rotation
        Log.d(MyApplication.APP_TAG, viewModel.hashCode() + "");

        // hide keyboard when touch outside
        new SetUIHideKeyboard(EnterKeyActivity.this,
                binding.getRoot()).setupUI();

        // Binding viewModel variable to layout
        binding.setMyViewModel(viewModel);
        binding.setMyController(this);

        configDropdownMenu();
    }

    private void configDropdownMenu() {
        List<String> items = ListSitesAvailable.getListSites();
        items.add(getString(R.string.others));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        ((AutoCompleteTextView) binding.siteNameLayout.getEditText()).setAdapter(adapter);

        // Log.d(MyApplication.APP_TAG, adapter.getItem(0));

        binding.siteNameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(getString(R.string.others)))
                    binding.otherSiteLayout.setVisibility(View.VISIBLE);
                else
                    binding.otherSiteLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public View bindingView() {
        binding = ActivityEnterKeyBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}