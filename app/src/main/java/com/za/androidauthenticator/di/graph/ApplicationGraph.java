package com.za.androidauthenticator.di.graph;

import com.za.androidauthenticator.di.module.DatabaseModule;
import com.za.androidauthenticator.di.module.NetworkModule;
import com.za.androidauthenticator.di.module.ViewModelModule;
import com.za.androidauthenticator.view.base.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

// Definition of the Application graph
@Singleton
@Component(modules = {DatabaseModule.class, NetworkModule.class, ViewModelModule.class})
public interface ApplicationGraph {
    void inject(BaseActivity baseActivity);
}