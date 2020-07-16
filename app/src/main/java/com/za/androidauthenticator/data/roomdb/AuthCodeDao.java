package com.za.androidauthenticator.data.roomdb;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.za.androidauthenticator.data.entity.AuthCode;

import java.util.List;

@Dao
public interface AuthCodeDao {
    /*** QUERY */
    @Query("SELECT * FROM auth_code")
    public LiveData<List<AuthCode>> loadAllCodes();

    /*** INSERT */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // replace if conflict
    public Long insertCode(AuthCode code); // return rowId for new user

    @Insert
    public List<Long> insertUsers(AuthCode... codes); // return list of new rowIds for the inserted items
}