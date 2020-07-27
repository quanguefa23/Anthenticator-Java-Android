package com.za.androidauthenticator.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.adapters.AuthCodeAdapter;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.util.SingleTaskExecutor;
import com.za.androidauthenticator.util.StringUtil;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatorViewModel extends ViewModel {

    private final UserRepository userRepository;
    private LiveData<List<AuthCode>> listCodes;
    MutableLiveData<Integer> posUpdateTime = new MutableLiveData<>();
    MutableLiveData<Integer> posUpdateCode = new MutableLiveData<>();
    private CalculateCodeUtil calculateCodeUtil;

    public AuthenticatorViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        listCodes = this.userRepository.getUserLocalDataSource().getListCodesLocal();
    }

    public LiveData<List<AuthCode>> getListCodes() {
        return listCodes;
    }

    public MutableLiveData<Integer> getPosUpdateTime() {
        return posUpdateTime;
    }

    public MutableLiveData<Integer> getPosUpdateCode() {
        return posUpdateCode;
    }

    public void updateDataCodeAllRows(List<AuthCode> listCodes) {
        List<CalculateCodeUtil.OnUpdateTimeRemaining> updateTimeCallbacks = new ArrayList<>();
        List<CalculateCodeUtil.OnUpdateCode> updateCodeCallbacks = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < listCodes.size(); i++) {
            final int pos = i;
            AuthCode item = listCodes.get(pos);

            updateTimeCallbacks.add(time -> {
                item.reTime = time;
                posUpdateTime.postValue(pos);
            });

            updateCodeCallbacks.add(code -> {
                item.currentCode = StringUtil.formatCodeToString(code);
                posUpdateCode.postValue(pos);
            });

            keys.add(item.key);
        }

        calculateCodeUtil = new CalculateCodeUtil(keys, updateTimeCallbacks, updateCodeCallbacks);
        calculateCodeUtil.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculateCodeUtil != null)
            calculateCodeUtil.stopCalculate();
    }
}
