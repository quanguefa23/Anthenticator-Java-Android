package com.za.androidauthenticator.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.contract.ListSitesAvailable;
import com.za.androidauthenticator.di.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class EnterKeyViewModel extends ViewModel {
    List<String> dataDropdownMenu;

    public List<String> getDataDropdownMenu() {
        if (dataDropdownMenu == null) {
            dataDropdownMenu = new ArrayList<>(ListSitesAvailable.getListSites());
            dataDropdownMenu.add(MyApplication.getInstance().getString(R.string.others));
        }
        return dataDropdownMenu;
    }
}
