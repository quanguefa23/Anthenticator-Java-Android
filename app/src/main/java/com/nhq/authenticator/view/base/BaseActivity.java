package com.nhq.authenticator.view.base;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    protected ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindingView());

        // Make Dagger instantiate @Inject fields in Activity
        ((AuthenticatorApp) getApplicationContext()).appGraph.inject(this);
        // Now viewModelFactory is available
    }

    public abstract View bindingView();
}
