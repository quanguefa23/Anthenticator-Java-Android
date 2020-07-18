package com.za.androidauthenticator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.contract.SiteIconContract;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;
import com.za.androidauthenticator.util.calculator.TimeBasedOneTimePasswordUtil;

public class DetailCodeViewModel extends ViewModel {

    public static final int UPDATE_SUCCESS = 0;
    public static final int UPDATE_SITE_NAME_ERROR = 1;
    public static final int UPDATE_ACCOUNT_NAME_ERROR = 2;

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

    public void updateInfoDataToView() {
        if (authCode == null) return;

        this.siteName.setValue(authCode.siteName);
        this.accountName.setValue(authCode.accountName);
        this.siteIcon.setValue(SiteIconContract.getIconId(authCode.siteName));
    }

    public void updateCodeDataToView() {
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

    public int updateCodeInfo(String siteName, String accountName) {
        // validation data
        if (siteName.isEmpty() || siteName.length() > 15)
            return UPDATE_SITE_NAME_ERROR;
        if (accountName.isEmpty() || accountName.length() > 30)
            return UPDATE_ACCOUNT_NAME_ERROR;

        authCode.siteName = siteName;
        authCode.accountName = accountName;

        updateInfoDataToView();
        userRepository.getUserLocalDataSource().updateCode(authCode);
        return UPDATE_SUCCESS;
    }
}