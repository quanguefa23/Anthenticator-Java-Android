package com.za.androidauthenticator.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.contract.SiteIconContract;

public class DetailCodeViewModel extends ViewModel {
    private MutableLiveData<String> timeRemaining;
    private MutableLiveData<String> code;
    private MutableLiveData<String> siteName;
    private MutableLiveData<String> accountName;
    private MutableLiveData<Integer> siteIcon;

    public DetailCodeViewModel() {
        timeRemaining = new MutableLiveData<>();
        code = new MutableLiveData<>();
        siteName = new MutableLiveData<>();
        accountName = new MutableLiveData<>();
        siteIcon = new MutableLiveData<>();
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

        // Set icon site
        this.siteIcon.setValue(SiteIconContract.getIconId(siteName));
    }
}