package com.nhq.authenticator.viewmodel;

import android.app.Dialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nhq.authenticator.R;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.contract.SitesIconContract;
import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.data.repository.Repository;
import com.nhq.authenticator.util.FormatStringUtil;
import com.nhq.authenticator.util.calculator.CalTaskByHandlerThread;
import com.nhq.authenticator.util.calculator.CalculationTask;

public class DetailCodeViewModel extends ViewModel {

    private final Repository repository;
    private CalculationTask calculationTask;
    private AuthCode authCode;

    private MutableLiveData<Integer> reTimeNumber = new MutableLiveData<>();
    private MutableLiveData<String> reTimeString  = new MutableLiveData<>();
    private MutableLiveData<String> codeString = new MutableLiveData<>();
    private MutableLiveData<String> siteName = new MutableLiveData<>();
    private MutableLiveData<String> accountName = new MutableLiveData<>();
    private MutableLiveData<Integer> siteIcon = new MutableLiveData<>();

    public DetailCodeViewModel(Repository repository) {
        this.repository = repository;
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

    public AuthCode getAuthCode() {
        return authCode;
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

    public void deleteThisAuthCode() {
        repository.getLocalDataSource().deleteCode(authCode);
    }

    public void updateCodeInfo(String siteName, String accountName, Dialog dialog) {
        // validate data
        if (siteName.isEmpty() || siteName.length() > 15) {
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.invalid_site_name_error,
                    Toast.LENGTH_SHORT).show();
        }
        else if (accountName.isEmpty() || accountName.length() > 30) {
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.invalid_account_name_error,
                    Toast.LENGTH_SHORT).show();
        }
        else {
            authCode.siteName = siteName;
            authCode.accountName = accountName;

            updateInfoDataToView();
            repository.getLocalDataSource().updateCode(authCode);

            Toast.makeText(AuthenticatorApp.getInstance(), R.string.update_success_notification,
                    Toast.LENGTH_SHORT).show();
            dialog.cancel();
        }
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

    public void setAuthCodeAndUpdateData(Intent intent) {
        authCode = (AuthCode) intent.getSerializableExtra("authCode");
        updateInfoDataToView();
    }
}