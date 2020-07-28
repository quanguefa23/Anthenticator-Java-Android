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
    MutableLiveData<Boolean> triggerUpdateCode = new MutableLiveData<>();
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
                listCodes.get(i).currentCode = StringUtil.formatCodeToString(codes.get(i));
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
}
