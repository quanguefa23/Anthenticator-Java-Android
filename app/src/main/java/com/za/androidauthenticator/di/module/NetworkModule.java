package com.za.androidauthenticator.di.module;

import com.za.androidauthenticator.data.repository.remote.TimeRetrofitService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// @Module informs Dagger that this class is a Dagger Module
@Module
public class NetworkModule {

    // @Provides tell Dagger how to create instances of the type that this function
    // returns (i.e. LoginRetrofitService).
    // Function parameters are the dependencies of this type.
    @Singleton
    @Provides
    public TimeRetrofitService provideTimeRetrofitService() {
        // Whenever Dagger needs to provide an instance of type LoginRetrofitService,
        // this code (the one inside the @Provides method) is run.
        return new Retrofit.Builder()
                .baseUrl("http://worldtimeapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TimeRetrofitService.class);
    }
}