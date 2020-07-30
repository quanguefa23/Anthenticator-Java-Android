package com.za.androidauthenticator.data.repository.remote;

import android.os.Handler;
import android.util.Log;

import com.za.androidauthenticator.data.entity.ResponseTime;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRemoteDataSource {

    private final TimeRetrofitService timeRetrofitService;

    @Inject
    public AuthRemoteDataSource(TimeRetrofitService timeRetrofitService) {
        this.timeRetrofitService = timeRetrofitService;
    }

    public void getTime(GetTimeCallBack callBack) {
        timeRetrofitService.getTimeViaPublicIp().enqueue(new Callback<ResponseTime>() {
            @Override
            public void onResponse(Call<ResponseTime> call, Response<ResponseTime> response) {
                ResponseTime time = response.body();
                if (time != null)
                    callBack.onResponse(time.getUnixTime());
            }

            @Override
            public void onFailure(Call<ResponseTime> call, Throwable t) {
                Log.d("QUANG", t.getMessage());
                callBack.onFailure();
            }
        });
    }

    public interface GetTimeCallBack {
        void onResponse(int unixTime);
        void onFailure();
    }
}