package com.za.androidauthenticator.di.graph;

import com.za.androidauthenticator.di.module.NetworkModule;
import com.za.androidauthenticator.di.module.ViewModelModule;
import com.za.androidauthenticator.viewmodels.AuthenticatorViewModel;
import com.za.androidauthenticator.views.activities.AuthenticatorActivity;

import javax.inject.Singleton;

import dagger.Component;

// Definition of the Application graph
@Singleton
@Component(modules = {NetworkModule.class, ViewModelModule.class})
public interface ApplicationGraph {
    // This tells Dagger that LoginActivity requests injection so the graph needs to
    // satisfy all the dependencies of the fields that LoginActivity is injecting.
    void inject(AuthenticatorActivity authenticatorActivity);
}

