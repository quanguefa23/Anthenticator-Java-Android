package com.nhq.authenticator.data.repository;

import com.nhq.authenticator.data.repository.local.LocalDataSource;
import com.nhq.authenticator.data.repository.remote.RemoteDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {

    private final LocalDataSource localDataSource;
    private final RemoteDataSource remoteDataSource;

    @Inject
    public Repository(LocalDataSource localDataSource,
                      RemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public LocalDataSource getLocalDataSource() {
        return localDataSource;
    }

    public RemoteDataSource getRemoteDataSource() {
        return remoteDataSource;
    }
}