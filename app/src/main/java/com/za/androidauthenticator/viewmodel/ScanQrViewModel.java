package com.za.androidauthenticator.viewmodel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.repository.AuthRepository;
import com.za.androidauthenticator.util.calculator.TimeBasedOTPUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ScanQrViewModel extends ViewModel {

    public static final int INSERT_SUCCESS = 0;
    public static final int INSERT_INVALID_URL_ERROR = 1;
    public static final int INSERT_KEY_ERROR = 2;

    AuthRepository authRepository;

    public ScanQrViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public int insertNewCode(@NonNull String rawString) {
        if (!isValidUri(rawString))
            return INSERT_INVALID_URL_ERROR;

        Uri uri = Uri.parse(rawString);

        String key = uri.getQueryParameter("secret");
        if (key == null || !TimeBasedOTPUtil.isValidKey(key))
            return INSERT_KEY_ERROR;

        String siteName = getSiteName(uri);

        String utf8String = "";
        try {
            utf8String = URLDecoder.decode(rawString, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accountName = getAccountName(utf8String);

        authRepository.getAuthLocalDataSource().insertNewCode(key, siteName, accountName);
        return INSERT_SUCCESS;
    }

    private String getAccountName(@NonNull String string) {
        if (string.length() < 15)
            return "";

        // Eg: string == "otpauth://totp/Google:nhqnhq1@gmail.com?secret=abcxyz...";

        // Get "Google:nhqnhq1@gmail.com?secret=abcxyz..."
        String sub = string.substring(15);

        // Get "Google:nhqnhq1@gmail.com"
        String[] arr = sub.split("\\?");
        String accountName = arr[0];

        // Get "nhqnhq1@gmail.com"
        String[] subArr = accountName.split(":");
        if (subArr.length > 1)
            accountName = subArr[1];

        // Ellipsize
        if (accountName.length() > 30)
            accountName = accountName.substring(0, 30);

        return accountName;
    }

    private String getSiteName(Uri uri) {
        String siteName = uri.getQueryParameter("issuer");
        if (siteName == null)
            siteName = "Unknown";
        else {
            if (siteName.length() > 15)
                siteName = siteName.substring(0, 15);
        }
        return siteName;
    }

    private boolean isValidUri(String rawString) {
        if (rawString.length() > 15) {
            String sub = rawString.substring(0, 15);
            return sub.equals("otpauth://totp/");
        }
        return false;
    }
}
