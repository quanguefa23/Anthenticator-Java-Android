package com.za.androidauthenticator.util;

public class FormatStringUtil {
    public static String formatCodeToString(int tempCode) {
        String res = Integer.toString(tempCode);
        while (res.length() < 6) {
            res = "0" + res;
        }
        return res.substring(0, 3) + ' ' + res.substring(3);
    }
}