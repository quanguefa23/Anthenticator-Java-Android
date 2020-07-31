package com.za.androidauthenticator.data.contract;

import com.za.androidauthenticator.R;

import java.util.HashMap;
import java.util.Map;

public class SitesIconContract {
    private static final Map<String, Integer> mapSiteNameWithIcon;

    static {
        mapSiteNameWithIcon = new HashMap<>();
        mapSiteNameWithIcon.put("google", R.drawable.ic_google);
        mapSiteNameWithIcon.put("microsoft", R.drawable.ic_microsoft);
        mapSiteNameWithIcon.put("facebook", R.drawable.ic_facebook);
        mapSiteNameWithIcon.put("twitter", R.drawable.ic_twitter);
    }

    public static Integer getIconId(String siteName) {
        siteName = siteName.toLowerCase();
        return mapSiteNameWithIcon.containsKey(siteName) ? mapSiteNameWithIcon.get(siteName) : R.drawable.ic_web;
    }
}