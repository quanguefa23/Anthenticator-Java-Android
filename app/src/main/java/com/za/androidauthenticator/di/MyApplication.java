package com.za.androidauthenticator.di;

import android.app.Application;

import com.za.androidauthenticator.di.graph.ApplicationGraph;
import com.za.androidauthenticator.di.graph.DaggerApplicationGraph;

// appComponent lives in the Application class to share its lifecycle
public class MyApplication extends Application {
    public static final String APP_TAG = "QUANG";

    // Reference to the application graph that is used across the whole app
    public ApplicationGraph appGraph = DaggerApplicationGraph.create();
}