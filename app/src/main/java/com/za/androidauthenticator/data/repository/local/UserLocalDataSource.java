package com.za.androidauthenticator.data.repository.local;

import com.za.androidauthenticator.data.model.AuthCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class UserLocalDataSource {
    @Inject
    public UserLocalDataSource() {}

    public List<AuthCode> getListCodesLocal() {
        List<AuthCode> list = new ArrayList<>();
        String key = "DJ5W4EBMOXJMYCO3E3KCW4G6CL53VXQ6";
        // Dummy implement
        for (int i = 0; i < 5; i++) {
            list.add(new AuthCode(key, "Google", "nhqnhq" + i + "@gmail.com"));
        }
        for (int i = 0; i < 5; i++) {
            list.add(new AuthCode(key, "Facebook", "nhqnhq" + i + "@gmail.com"));
        }
        for (int i = 0; i < 5; i++) {
            list.add(new AuthCode(key, "Twitter", "nhqnhq" + i + "@gmail.com"));
        }
        for (int i = 0; i < 5; i++) {
            list.add(new AuthCode(key, "Binance", "nhqnhq" + i + "@gmail.com"));
        }
        Collections.shuffle(list);

        return list;
    }
}