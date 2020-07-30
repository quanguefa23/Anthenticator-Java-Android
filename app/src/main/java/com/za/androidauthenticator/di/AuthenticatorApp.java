package com.za.androidauthenticator.di;

import android.app.Application;

import com.za.androidauthenticator.di.component.ApplicationComponent;
import com.za.androidauthenticator.di.component.DaggerApplicationComponent;

// appGraph lives in the Application class to share its lifecycle
public class AuthenticatorApp extends Application {

    public static final String APP_TAG = "QUANG";

    public static int DIFF_TIME_SECOND = 0;

    // Reference to the application graph that is used across the whole app
    public ApplicationComponent appGraph = DaggerApplicationComponent.create();

    private static AuthenticatorApp instance;

    public static synchronized AuthenticatorApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}