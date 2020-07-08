package com.za.androidauthenticator.data.model;

import java.io.Serializable;
import java.util.Objects;

public class AuthCode implements Serializable {
    private String id;
    private String key;
    private String siteName;
    private String accountName;
    private String code;

    public AuthCode(String key, String siteName, String accountName) {
        this.key = key;
        this.siteName = siteName;
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthCode authCode = (AuthCode) o;
        return Objects.equals(key, authCode.key) &&
                Objects.equals(siteName, authCode.siteName) &&
                Objects.equals(accountName, authCode.accountName) &&
                Objects.equals(code, authCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, siteName, accountName, code);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
