package com.za.androidauthenticator.di.module;

import androidx.room.Room;

import com.za.androidauthenticator.data.roomdb.AppDatabase;
import com.za.androidauthenticator.di.AuthenticatorApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Singleton
    @Provides
    public AppDatabase provideAppDatabase() {
        // Whenever Dagger needs to provide an instance of type AppDatabase,
        // this code (the one inside the @Provides method) is run.
        return Room.databaseBuilder(AuthenticatorApp.getInstance(),
                AppDatabase.class, "authenticator").build();
    }
}
