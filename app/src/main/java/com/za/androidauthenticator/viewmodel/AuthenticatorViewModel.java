package com.za.androidauthenticator.viewmodel;

import android.view.MenuItem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.AuthRepository;
import com.za.androidauthenticator.data.repository.remote.AuthRemoteDataSource;
import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.util.FormatStringUtil;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatorViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private LiveData<List<AuthCode>> listCodes;
    MutableLiveData<Boolean> triggerUpdateTime = new MutableLiveData<>();
    MutableLiveData<Boolean> triggerUpdateCode = new MutableLiveData<>();
    private CalculateCodeUtil calculateCodeUtil;

    public AuthenticatorViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        listCodes = this.authRepository.getAuthLocalDataSource().getListCodesLocal();
    }

    public LiveData<List<AuthCode>> getListCodes() {
        return listCodes;
    }

    public MutableLiveData<Boolean> getTriggerUpdateTime() {
        return triggerUpdateTime;
    }

    public MutableLiveData<Boolean> getTriggerUpdateCode() {
        return triggerUpdateCode;
    }

    public void updateDataCodeAllRows(List<AuthCode> listCodes) {
        CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback = time -> {
            for (AuthCode item : listCodes)
                item.reTime = time;
            triggerUpdateTime.postValue(true);
        };

        CalculateCodeUtil.OnUpdateCode updateCodeCallback = codes -> {
            for (int i = 0; i < listCodes.size(); i++)
                listCodes.get(i).currentCode = FormatStringUtil.formatCodeToString(codes.get(i));
            triggerUpdateCode.postValue(true);
        };

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < listCodes.size(); i++)
            keys.add(listCodes.get(i).key);

        calculateCodeUtil = new CalculateCodeUtil(keys, updateTimeCallback, updateCodeCallback);
        calculateCodeUtil.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculateCodeUtil != null)
            calculateCodeUtil.stopCalculate();
    }

    public void setTitleShowHideItem(MenuItem showHide, boolean showCode) {
        if (showCode)
            showHide.setTitle(R.string.hide_codes);
        else
            showHide.setTitle(R.string.show_codes);
    }

    public interface SyncTimeCallBack {
        void onSuccess();
        void onFailure();
    }

    public void syncTime(SyncTimeCallBack callBack) {
        authRepository.getAuthRemoteDataSource().
                getTime(new AuthRemoteDataSource.GetTimeCallBack() {
            @Override
            public void onResponse(int unixTime) {
                AuthenticatorApp.DIFF_TIME_SECOND =
                        (int) (unixTime - System.currentTimeMillis() / 1000);
                callBack.onSuccess();
            }

            @Override
            public void onFailure() {
                callBack.onFailure();
            }
        });
    }
}
