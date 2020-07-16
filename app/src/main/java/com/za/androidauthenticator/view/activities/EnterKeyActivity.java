package com.za.androidauthenticator.view.activities;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
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

        setVisibilityOtherSiteLayout();
        restoreSelectionDropDownMenu(savedInstanceState);
        configDropdownMenu();
    }

    private void setVisibilityOtherSiteLayout() {
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

    private void restoreSelectionDropDownMenu(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String siteName = savedInstanceState.getString("siteName");
            if (siteName != null)
                binding.siteNameLayout.getEditText().setText(siteName);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("siteName", binding.siteName.getText().toString());
    }

    private void configDropdownMenu() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item,
                viewModel.getDataDropdownMenu());
        binding.siteNameLayout.getEditText().setSaveEnabled(false);
        ((AutoCompleteTextView) binding.siteNameLayout.getEditText()).setAdapter(adapter);
    }

    @Override
    public View bindingView() {
        binding = ActivityEnterKeyBinding.inflate(getLayoutInflater());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}