package com.za.androidauthenticator.data.repository;

import com.za.androidauthenticator.data.repository.local.AuthLocalDataSource;
import com.za.androidauthenticator.data.repository.remote.AuthRemoteDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {

    private final AuthLocalDataSource authLocalDataSource;
    private final AuthRemoteDataSource authRemoteDataSource;

    @Inject
    public AuthRepository(AuthLocalDataSource authLocalDataSource,
                          AuthRemoteDataSource authRemoteDataSource) {
        this.authLocalDataSource = authLocalDataSource;
        this.authRemoteDataSource = authRemoteDataSource;
    }

    public AuthLocalDataSource getAuthLocalDataSource() {
        return authLocalDataSource;
    }

    public AuthRemoteDataSource getAuthRemoteDataSource() {
        return authRemoteDataSource;
    }
}