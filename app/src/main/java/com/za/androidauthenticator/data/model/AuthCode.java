package com.za.androidauthenticator.data.model;

import java.util.Objects;

public class AuthCode {
    private String id;
    private String key;
    private String site;
    private String email;
    private String code;

    public AuthCode(String key, String site, String email) {
        this.key = key;
        this.site = site;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthCode authCode = (AuthCode) o;
        return Objects.equals(key, authCode.key) &&
                Objects.equals(site, authCode.site) &&
                Objects.equals(email, authCode.email) &&
                Objects.equals(code, authCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, site, email, code);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
