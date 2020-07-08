package com.za.androidauthenticator.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailCodeViewModel extends ViewModel {
    private MutableLiveData<String> timeRemaining;
    private MutableLiveData<String> code;


    public DetailCodeViewModel() {
        timeRemaining = new MutableLiveData<>();
        code = new MutableLiveData<>();
//        timeRemaining.setValue("30");
//        code.setValue("000 000");
    }
}