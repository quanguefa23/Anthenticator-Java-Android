package com.nhq.authenticator.data.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.nhq.authenticator.data.entity.AuthCode;

@Database(entities = {AuthCode.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AuthCodeDao authCodeDao();
}