package com.nhq.authenticator.data.entity;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.util.calculator.TimeBasedOTPUtil;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "auth_code")
public class AuthCode implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int codeId;

    public String key;

    public String siteName;

    public String accountName;

    @Ignore
    public int reTime = 30;

    @Ignore
    public String currentCode = "000 000";

    public AuthCode(String key, String siteName, String accountName) {
        this.key = key;
        this.siteName = siteName;
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AuthCode authCode = (AuthCode) obj;
        return  Objects.equals(key, authCode.key) &&
                Objects.equals(siteName, authCode.siteName) &&
                Objects.equals(accountName, authCode.accountName) &&
                Objects.equals(codeId, authCode.codeId);
    }

    public boolean equalInContent(AuthCode other) {
        if (this == other) return true;
        if (other == null) return false;
        return  Objects.equals(key, other.key) &&
                Objects.equals(siteName, other.siteName) &&
                Objects.equals(accountName, other.accountName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeId, key, siteName, accountName);
    }

    @NonNull
    @Override
    public String toString() {
        return codeId + "|" + key + "|" + siteName + "|" + accountName;
    }

    public static AuthCode parseAuthCode(String content) {
        AuthCode res = null;
        String[] params = content.split("\\|");
        try {
            String key = params[1];
            if (!TimeBasedOTPUtil.isValidKey(key)) {
                throw new Exception("Invalid key: " + key);
            }
            String siteName = params[2];
            String accountName = params[3];

            res = new AuthCode(key, siteName, accountName);
        }
        catch (Exception e) {
            Log.e(AuthenticatorApp.APP_TAG, e.getMessage());
        }
        return res;
    }
}