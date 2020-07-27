package com.za.androidauthenticator.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.util.StringUtil;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatorViewModel extends ViewModel {

    private final UserRepository userRepository;
    private LiveData<List<AuthCode>> listCodes;
    MutableLiveData<Boolean> triggerUpdateTime = new MutableLiveData<>();
    MutableLiveData<Integer> triggerUpdateCode = new MutableLiveData<>();
    private CalculateCodeUtil calculateCodeUtil;

    public AuthenticatorViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        listCodes = this.userRepository.getUserLocalDataSource().getListCodesLocal();
    }

    public LiveData<List<AuthCode>> getListCodes() {
        return listCodes;
    }

    public MutableLiveData<Boolean> getTriggerUpdateTime() {
        return triggerUpdateTime;
    }

    public MutableLiveData<Integer> getTriggerUpdateCode() {
        return triggerUpdateCode;
    }

    public void updateDataCodeAllRows(List<AuthCode> listCodes) {
        CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback = time -> {
            for (AuthCode item : listCodes) {
                item.reTime = time;
            }
            triggerUpdateTime.postValue(true);
        };

        List<CalculateCodeUtil.OnUpdateCode> updateCodeCallbacks = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < listCodes.size(); i++) {
            final int pos = i;
            AuthCode item = listCodes.get(pos);

            updateCodeCallbacks.add(code -> {
                item.currentCode = StringUtil.formatCodeToString(code);
                triggerUpdateCode.postValue(pos);
            });

            keys.add(item.key);
        }

        calculateCodeUtil = new CalculateCodeUtil(keys, updateTimeCallback, updateCodeCallbacks);
        calculateCodeUtil.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculateCodeUtil != null)
            calculateCodeUtil.stopCalculate();
    }
}
