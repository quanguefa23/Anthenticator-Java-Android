package com.za.androidauthenticator.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.model.AuthCode;
import com.za.androidauthenticator.data.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;

public class AuthenticatorViewModel extends ViewModel {

    private final UserRepository userRepository;
    private MutableLiveData<List<AuthCode>> listCodes;

    public AuthenticatorViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        listCodes = new MutableLiveData<>();
    }

    public void updateListCodes() {
        listCodes.setValue(userRepository.getUserLocalDataSource().getListCodesLocal());
    }

    public LiveData<List<AuthCode>> getListCodes() {
        return listCodes;
    }
}
