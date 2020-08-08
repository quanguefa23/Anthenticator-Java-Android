package com.nhq.authenticator.data.repository.remote;

import com.nhq.authenticator.data.entity.ResponseTime;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TimeRetrofitService {
    @GET("api/ip")
    Call<ResponseTime> getTimeViaPublicIp();
}
