package com.za.androidauthenticator.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "auth_code")
public class AuthCode implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int codeId;

    public String key;

    public String siteName;

    public String accountName;

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

    @Override
    public int hashCode() {
        return Objects.hash(codeId, key, siteName, accountName);
    }
}
