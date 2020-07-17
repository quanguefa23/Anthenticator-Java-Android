package com.za.androidauthenticator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.contract.SiteIconContract;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;

public class DetailCodeViewModel extends ViewModel {

    private final UserRepository userRepository;
    private CalculateCodeUtil calculateCodeUtil;
    private AuthCode authCode;

    private MutableLiveData<Integer> reTimeNumber = new MutableLiveData<>();
    private MutableLiveData<String> reTimeString  = new MutableLiveData<>();
    private MutableLiveData<String> codeString = new MutableLiveData<>();
    private MutableLiveData<String> siteName = new MutableLiveData<>();
    private MutableLiveData<String> accountName = new MutableLiveData<>();
    private MutableLiveData<Integer> siteIcon = new MutableLiveData<>();


    public DetailCodeViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<Integer> getReTimeNumber() {
        return reTimeNumber;
    }

    public MutableLiveData<String> getSiteName() {
        return siteName;
    }

    public MutableLiveData<String> getAccountName() {
        return accountName;
    }

    public MutableLiveData<Integer> getSiteIcon() {
        return siteIcon;
    }

    public MutableLiveData<String> getReTimeString() {
        return reTimeString;
    }

    public MutableLiveData<String> getCodeString() {
        return codeString;
    }

    public void updateInfoData() {
        if (authCode == null) return;

        this.siteName.setValue(authCode.siteName);
        this.accountName.setValue(authCode.accountName);
        this.siteIcon.setValue(SiteIconContract.getIconId(authCode.siteName));
    }

    public void updateCodeData() {
        if (authCode == null) return;

        // Register callback to update UI (time)
        CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback = time -> {
            reTimeString.postValue(Integer.toString(time));
            reTimeNumber.postValue(time);
        };

        // Register callback to update UI (code)
        CalculateCodeUtil.OnUpdateCode updateCodeCallback = code ->
                this.codeString.postValue(formatToString(code));

        calculateCodeUtil = new CalculateCodeUtil(authCode.key, updateTimeCallback, updateCodeCallback);
        calculateCodeUtil.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculateCodeUtil != null)
            calculateCodeUtil.stopCalculate();
    }

    private String formatToString(int tempCode) {
        String res = Integer.toString(tempCode);
        while (res.length() < 6) {
            res = "0" + res;
        }
        return res.substring(0, 3) + ' ' + res.substring(3);
    }

    public void setAuthCode(AuthCode authCode) {
        this.authCode = authCode;
    }

    public void deleteThisAuthCode() {
        userRepository.getUserLocalDataSource().deleteCode(authCode);
    }
}