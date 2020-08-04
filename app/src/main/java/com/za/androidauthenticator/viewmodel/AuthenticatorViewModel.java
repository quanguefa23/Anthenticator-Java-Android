package com.za.androidauthenticator.viewmodel;

import android.view.MenuItem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.AuthRepository;
import com.za.androidauthenticator.data.repository.remote.AuthRemoteDataSource;
import com.za.androidauthenticator.util.FormatStringUtil;
import com.za.androidauthenticator.util.calculator.CalTaskByHandlerThread;
import com.za.androidauthenticator.util.calculator.CalTaskByTimerTask;
import com.za.androidauthenticator.util.calculator.CalculationTask;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatorViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private LiveData<List<AuthCode>> listCodes;
    MutableLiveData<Boolean> triggerUpdateTime = new MutableLiveData<>();
    MutableLiveData<Boolean> triggerUpdateCode = new MutableLiveData<>();
    private CalculationTask calculationTask;

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
        CalculationTask.OnUpdateTimeRemaining updateTimeCallback = time -> {
            for (AuthCode item : listCodes)
                item.reTime = time;
            triggerUpdateTime.postValue(true);
        };

        CalculationTask.OnUpdateCode updateCodeCallback = codes -> {
            for (int i = 0; i < listCodes.size(); i++)
                listCodes.get(i).currentCode = FormatStringUtil.formatCodeToStringWithSpace(codes.get(i));
            triggerUpdateCode.postValue(true);
        };

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < listCodes.size(); i++)
            keys.add(listCodes.get(i).key);

        calculationTask = new CalTaskByHandlerThread(keys, updateTimeCallback, updateCodeCallback);
        calculationTask.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculationTask != null)
            calculationTask.stopCalculate();
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
