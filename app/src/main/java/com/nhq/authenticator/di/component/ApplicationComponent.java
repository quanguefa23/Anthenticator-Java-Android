package com.nhq.authenticator.di.component;

import com.nhq.authenticator.di.module.DatabaseModule;
import com.nhq.authenticator.di.module.NetworkModule;
import com.nhq.authenticator.di.module.SignInModule;
import com.nhq.authenticator.di.module.ViewModelModule;
import com.nhq.authenticator.view.base.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

// Definition of the Application graph
@Singleton
@Component(modules = {DatabaseModule.class, NetworkModule.class,
        ViewModelModule.class, SignInModule.class})
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);
}