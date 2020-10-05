package com.nhq.authenticator.viewmodel;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.nhq.authenticator.R;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.contract.SitesAvailable;
import com.nhq.authenticator.data.repository.Repository;
import com.nhq.authenticator.util.calculator.TimeBasedOTPUtil;

import java.util.ArrayList;
import java.util.List;

public class EnterKeyViewModel extends ViewModel {

    Repository repository;
    List<String> dataDropdownMenu;

    public EnterKeyViewModel(Repository repository) {
        this.repository = repository;
    }

    public List<String> getDataDropdownMenu() {
        if (dataDropdownMenu == null) {
            dataDropdownMenu = new ArrayList<>(SitesAvailable.getSites());
            dataDropdownMenu.add(AuthenticatorApp.getInstance().getString(R.string.others));
        }
        return dataDropdownMenu;
    }

    public boolean insertNewCode(@NonNull String key,
                             @NonNull String siteName,
                             @NonNull String accountName) {
        // validate data
        if (siteName.length() > 15) {
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.invalid_site_name_error,
                    Toast.LENGTH_SHORT).show();
        }
        else if (accountName.length() > 30) {
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.invalid_account_name_error,
                    Toast.LENGTH_SHORT).show();
        }
        else if (!TimeBasedOTPUtil.isValidKey(key)) {
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.invalid_key_error,
                    Toast.LENGTH_SHORT).show();
        }
        else {
            repository.insertNewCode(key, siteName, accountName);
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.enter_key_success,
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}
