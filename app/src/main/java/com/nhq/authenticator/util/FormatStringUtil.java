package com.nhq.authenticator.util;

public class FormatStringUtil {
    public static String formatCodeToStringWithSpace(int tempCode) {
        String res = formatCodeToString(tempCode);
        return res.substring(0, 3) + ' ' + res.substring(3);
    }

    public static String formatCodeToString(int tempCode) {
        String res = Integer.toString(tempCode);
        while (res.length() < 6) {
            res = "0" + res;
        }
        return res;
    }
}