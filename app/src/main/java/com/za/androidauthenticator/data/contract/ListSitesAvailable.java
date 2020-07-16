package com.za.androidauthenticator.data.contract;

import java.util.ArrayList;
import java.util.List;

public class ListSitesAvailable {
    public static final List<String> sites;

    static {
        sites = new ArrayList<>();
        sites.add("Google");
        sites.add("Facebook");
        sites.add("Twitter");
    }

    public static List<String> getListSites() {
        return sites;
    }
}