package com.za.androidauthenticator.data.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.za.androidauthenticator.data.entity.AuthCode;

@Database(entities = {AuthCode.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AuthCodeDao authCodeDao();
}