package com.za.androidauthenticator.data.repository.remote;

import com.za.androidauthenticator.data.entity.ResponseTime;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TimeRetrofitService {
    @GET("api/ip")
    Call<ResponseTime> getTimeViaPublicIp();
}
