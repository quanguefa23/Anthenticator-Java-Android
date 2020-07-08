package com.za.androidauthenticator.views.base;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.za.androidauthenticator.databinding.ActivityAuthenticatorBinding;

public abstract class BaseActivity extends AppCompatActivity {
    public abstract View bindingView();
}
