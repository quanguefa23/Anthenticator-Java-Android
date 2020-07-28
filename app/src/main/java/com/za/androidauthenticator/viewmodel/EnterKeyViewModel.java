package com.za.androidauthenticator.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.contract.ListSitesAvailable;
import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.di.AuthenticatorApp;
import com.za.androidauthenticator.util.calculator.TimeBasedOTPUtil;

import java.util.ArrayList;
import java.util.List;

public class EnterKeyViewModel extends ViewModel {

    public static final int INSERT_SUCCESS = 0;
    public static final int INSERT_SITE_NAME_ERROR = 1;
    public static final int INSERT_ACCOUNT_NAME_ERROR = 2;
    public static final int INSERT_KEY_ERROR = 3;

    UserRepository userRepository;
    List<String> dataDropdownMenu;

    public EnterKeyViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<String> getDataDropdownMenu() {
        if (dataDropdownMenu == null) {
            dataDropdownMenu = new ArrayList<>(ListSitesAvailable.getListSites());
            dataDropdownMenu.add(AuthenticatorApp.getInstance().getString(R.string.others));
        }
        return dataDropdownMenu;
    }

    public int insertNewCode(@NonNull String key,
                             @NonNull String siteName,
                             @NonNull String accountName) {
        // validation data
        if (siteName.length() > 15)
            return INSERT_SITE_NAME_ERROR;
        if (accountName.length() > 30)
            return INSERT_ACCOUNT_NAME_ERROR;
        if (!TimeBasedOTPUtil.isValidKey(key))
            return INSERT_KEY_ERROR;

        userRepository.getUserLocalDataSource().insertNewCode(key, siteName, accountName);
        return INSERT_SUCCESS;
    }
}
