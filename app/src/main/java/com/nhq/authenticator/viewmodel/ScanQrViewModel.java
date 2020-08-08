package com.nhq.authenticator.viewmodel;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.nhq.authenticator.R;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.repository.Repository;
import com.nhq.authenticator.util.calculator.TimeBasedOTPUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ScanQrViewModel extends ViewModel {

    Repository repository;

    public ScanQrViewModel(Repository repository) {
        this.repository = repository;
    }

    public void insertNewCode(@NonNull String rawString) {
        if (!isValidUri(rawString)) {
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.invalid_qr_code,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse(rawString);

        String key = uri.getQueryParameter("secret");
        if (key == null || !TimeBasedOTPUtil.isValidKey(key)) {
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.invalid_key_error,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String siteName = getSiteName(uri);

        String utf8String = "";
        try {
            utf8String = URLDecoder.decode(rawString, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accountName = getAccountName(utf8String);

        repository.getLocalDataSource().insertNewCode(key, siteName, accountName);
        Toast.makeText(AuthenticatorApp.getInstance(), R.string.scan_qr_success, Toast.LENGTH_SHORT).show();
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
