package com.nhq.authenticator.data.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.api.services.drive.model.User;
import com.nhq.authenticator.data.entity.AuthCode;

import java.util.List;

@Dao
public interface AuthCodeDao {
    /*** QUERY */
    @Query("SELECT * FROM auth_code")
    LiveData<List<AuthCode>> loadAllCodes();

    /*** INSERT */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // replace if conflict
    void insertCode(AuthCode code); // return rowId for new user

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCodes(List<AuthCode> codes); // return list of new rowIds for the inserted items

    /*** DELETE */
    @Delete
    void deleteCode(AuthCode code);

    /*** UPDATE */
    @Update
    void updateCode(AuthCode code);
}
