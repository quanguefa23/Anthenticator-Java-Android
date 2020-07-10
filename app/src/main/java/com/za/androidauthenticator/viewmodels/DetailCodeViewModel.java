package com.za.androidauthenticator.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.contract.SiteIconContract;
import com.za.androidauthenticator.utils.calculator.CalculateCodeUtil;

public class DetailCodeViewModel extends ViewModel {

    private CalculateCodeUtil calculateCodeUtil;

    private MutableLiveData<String> timeRemaining;
    private MutableLiveData<Integer> timeNumber;
    private MutableLiveData<String> code;
    private MutableLiveData<String> siteName;
    private MutableLiveData<String> accountName;
    private MutableLiveData<Integer> siteIcon;

    public DetailCodeViewModel() {
        timeRemaining = new MutableLiveData<>();
        timeNumber = new MutableLiveData<>();
        code = new MutableLiveData<>();
        siteName = new MutableLiveData<>();
        accountName = new MutableLiveData<>();
        siteIcon = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getTimeNumber() {
        return timeNumber;
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

    public MutableLiveData<String> getTimeRemaining() {
        return timeRemaining;
    }

    public MutableLiveData<String> getCode() {
        return code;
    }

    public void updateInfoData(String siteName, String accountName) {
        this.siteName.setValue(siteName);
        this.accountName.setValue(accountName);
        this.siteIcon.setValue(SiteIconContract.getIconId(siteName));
    }

    public void updateCodeData(String key) {
        // Register callback to update UI (time)
        CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback = time -> {
            timeRemaining.postValue(Integer.toString(time));
            timeNumber.postValue(time);
        };

        // Register callback to update UI (code)
        CalculateCodeUtil.OnUpdateCode updateCodeCallback = codeString ->
                code.postValue(codeString);

        calculateCodeUtil = new CalculateCodeUtil(key, updateTimeCallback, updateCodeCallback);
        calculateCodeUtil.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculateCodeUtil != null)
            calculateCodeUtil.stopCalculate();
    }
}