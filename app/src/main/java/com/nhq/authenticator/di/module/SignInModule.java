package com.nhq.authenticator.di.module;

import com.nhq.authenticator.data.repository.SignInManager;
import com.nhq.authenticator.data.repository.remote.GoogleSignInManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SignInModule {
    @Singleton
    @Provides
    public SignInManager provideSignInManager() {
        return new GoogleSignInManager();
    }
}
