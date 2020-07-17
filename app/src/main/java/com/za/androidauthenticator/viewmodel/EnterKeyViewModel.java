package com.za.androidauthenticator.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.contract.ListSitesAvailable;
import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.di.MyApplication;
import com.za.androidauthenticator.util.calculator.TimeBasedOneTimePasswordUtil;

import java.util.ArrayList;
import java.util.List;

public class EnterKeyViewModel extends ViewModel {

    public static final int INSERT_SUCCESS = 0;
    public static final int INSERT_SITE_NAME_ERROR = 1;
    public static final int INSERT_ACCOUNT_NAME_ERROR = 2;
    public static final int INSERT_KEY_ERROR = 3;

    UserRepository userRepository;
    List<String> dataDropdownMenu;

    public List<String> getDataDropdownMenu() {
        if (dataDropdownMenu == null) {
            dataDropdownMenu = new ArrayList<>(ListSitesAvailable.getListSites());
            dataDropdownMenu.add(MyApplication.getInstance().getString(R.string.others));
        }
        return dataDropdownMenu;
    }

    public EnterKeyViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int insertNewCode(String key, String siteName, String accountName) {
        // validation data
        if (siteName.isEmpty() || siteName.length() > 15)
            return INSERT_SITE_NAME_ERROR;
        if (accountName.isEmpty() || accountName.length() > 30)
            return INSERT_ACCOUNT_NAME_ERROR;
        if ((key.length() != 16 && key.length() != 32) ||
                !TimeBasedOneTimePasswordUtil.isValidKey(key))
            return INSERT_KEY_ERROR;

        userRepository.getUserLocalDataSource().insertNewCode(key, siteName, accountName);
        return INSERT_SUCCESS;
    }
}
