package com.za.androidauthenticator.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.data.repository.UserRepository;
import com.za.androidauthenticator.util.SingleTaskExecutor;

import java.util.List;

public class AuthenticatorViewModel extends ViewModel {

    private final UserRepository userRepository;
    private LiveData<List<AuthCode>> listCodes;

    public AuthenticatorViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        listCodes = this.userRepository.getUserLocalDataSource().getListCodesLocal();
    }

    public LiveData<List<AuthCode>> getListCodes() {
        return listCodes;
    }

    public int insertNewCode(String key, String siteName, String accountName) {
        // validation data

        userRepository.getUserLocalDataSource().insertNewCode(key, siteName, accountName);

        return 0; // code error
    }
}
