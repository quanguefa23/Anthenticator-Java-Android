package com.za.androidauthenticator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.contract.SiteIconContract;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;

public class DetailCodeViewModel extends ViewModel {

    private CalculateCodeUtil calculateCodeUtil;

    private MutableLiveData<Integer> reTimeNumber;
    private MutableLiveData<String> reTimeString;
    private MutableLiveData<String> codeString;
    private MutableLiveData<String> siteName;
    private MutableLiveData<String> accountName;
    private MutableLiveData<Integer> siteIcon;


    public DetailCodeViewModel() {
        reTimeString = new MutableLiveData<>();
        reTimeNumber = new MutableLiveData<>();
        codeString = new MutableLiveData<>();
        siteName = new MutableLiveData<>();
        accountName = new MutableLiveData<>();
        siteIcon = new MutableLiveData<>();
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

    public void updateInfoData(String siteName, String accountName) {
        this.siteName.setValue(siteName);
        this.accountName.setValue(accountName);
        this.siteIcon.setValue(SiteIconContract.getIconId(siteName));
    }

    public void updateCodeData(String key) {
        // Register callback to update UI (time)
        CalculateCodeUtil.OnUpdateTimeRemaining updateTimeCallback = time -> {
            reTimeString.postValue(Integer.toString(time));
            reTimeNumber.postValue(time);
        };

        // Register callback to update UI (code)
        CalculateCodeUtil.OnUpdateCode updateCodeCallback = code ->
                this.codeString.postValue(formatToString(code));

        calculateCodeUtil = new CalculateCodeUtil(key, updateTimeCallback, updateCodeCallback);
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
}