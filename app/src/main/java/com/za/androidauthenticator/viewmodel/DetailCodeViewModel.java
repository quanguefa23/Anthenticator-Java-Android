package com.za.androidauthenticator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.contract.SitesIconContract;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.AuthRepository;
import com.za.androidauthenticator.util.FormatStringUtil;
import com.za.androidauthenticator.util.calculator.CalTaskByHandlerThread;
import com.za.androidauthenticator.util.calculator.CalculationTask;

public class DetailCodeViewModel extends ViewModel {

    public static final int UPDATE_SUCCESS = 0;
    public static final int UPDATE_SITE_NAME_ERROR = 1;
    public static final int UPDATE_ACCOUNT_NAME_ERROR = 2;

    private final AuthRepository authRepository;
    private CalculationTask calculationTask;
    private AuthCode authCode;

    private MutableLiveData<Integer> reTimeNumber = new MutableLiveData<>();
    private MutableLiveData<String> reTimeString  = new MutableLiveData<>();
    private MutableLiveData<String> codeString = new MutableLiveData<>();
    private MutableLiveData<String> siteName = new MutableLiveData<>();
    private MutableLiveData<String> accountName = new MutableLiveData<>();
    private MutableLiveData<Integer> siteIcon = new MutableLiveData<>();

    public DetailCodeViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
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
        this.siteIcon.setValue(SitesIconContract.getIconId(authCode.siteName));
    }

    public void updateCodeDataToView() {
        if (authCode == null) return;

        // Register callback to update UI (time)
        CalculationTask.OnUpdateTimeRemaining updateTimeCallback = time -> {
            reTimeString.postValue(Integer.toString(time));
            reTimeNumber.postValue(time);
        };

        // Register callback to update UI (code)
        CalculationTask.OnUpdateCode updateCodeCallback = codes ->
                this.codeString.postValue(FormatStringUtil.formatCodeToStringWithSpace(codes.get(0)));

        calculationTask = new CalTaskByHandlerThread(authCode.key, updateTimeCallback, updateCodeCallback);
        calculationTask.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculationTask != null)
            calculationTask.stopCalculate();
    }

    public void setAuthCode(AuthCode authCode) {
        this.authCode = authCode;
    }

    public void deleteThisAuthCode() {
        authRepository.getAuthLocalDataSource().deleteCode(authCode);
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
        authRepository.getAuthLocalDataSource().updateCode(authCode);
        return UPDATE_SUCCESS;
    }

    public String exportCodeToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("otpauth://totp/");
        stringBuilder.append(authCode.accountName).append("?");
        stringBuilder.append("secret=").append(authCode.key);

        if (!authCode.siteName.equals("Unknown"))
            stringBuilder.append("&issuer=").append(authCode.siteName);

        return stringBuilder.toString();
    }
}