package com.nhq.authenticator.data.contract;

import java.util.ArrayList;
import java.util.List;

public class SitesAvailable {
    public static final List<String> sites;

    static {
        sites = new ArrayList<>();
        sites.add("Google");
        sites.add("Microsoft");
        sites.add("Facebook");
        sites.add("Twitter");
    }

    public static List<String> getListSites() {
        return sites;
    }
}