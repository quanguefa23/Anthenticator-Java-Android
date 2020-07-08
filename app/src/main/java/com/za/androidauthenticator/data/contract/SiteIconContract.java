package com.za.androidauthenticator.data.contract;

import com.za.androidauthenticator.R;

import java.util.HashMap;
import java.util.Map;

public class SiteIconContract {
    private static final Map<String, Integer> map;

    static {
        map = new HashMap<>();
        map.put("google", R.drawable.ic_google);
        map.put("facebook", R.drawable.ic_facebook);
        map.put("twitter", R.drawable.ic_twitter);
    }

    public static Integer getIconId(String siteName) {
        siteName = siteName.toLowerCase();
        return map.containsKey(siteName) ? map.get(siteName) : R.drawable.ic_web;
    }
}
