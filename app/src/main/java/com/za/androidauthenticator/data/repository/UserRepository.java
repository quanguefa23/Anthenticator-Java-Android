package com.za.androidauthenticator.data.repository;

import com.za.androidauthenticator.data.repository.local.UserLocalDataSource;
import com.za.androidauthenticator.data.repository.remote.UserRemoteDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {

    private final UserLocalDataSource userLocalDataSource;
    private final UserRemoteDataSource userRemoteDataSource;

    @Inject
    public UserRepository(UserLocalDataSource userLocalDataSource, UserRemoteDataSource userRemoteDataSource) {
        this.userLocalDataSource = userLocalDataSource;
        this.userRemoteDataSource = userRemoteDataSource;
    }

    public UserLocalDataSource getUserLocalDataSource() {
        return userLocalDataSource;
    }

    public UserRemoteDataSource getUserRemoteDataSource() {
        return userRemoteDataSource;
    }
}