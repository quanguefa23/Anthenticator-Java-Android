package com.nhq.authenticator.data.repository.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.nhq.authenticator.appcomponent.AuthenticatorApp
import com.nhq.authenticator.data.entity.AuthCode
import com.nhq.authenticator.data.roomdb.AppDatabase
import com.nhq.authenticator.data.roomdb.AuthCodeDao
import com.nhq.authenticator.util.SingleTaskExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(appDatabase: AppDatabase) {

    companion object {
        private const val SHOW_CODE_PREFERENCES_KEY = "isShowing"
        private const val PREFERENCES_STATE_NAME = "AuthenticatorState"
    }

    private var authCodeDao = appDatabase.authCodeDao()

    fun insertNewCode(code: AuthCode?) {
        SingleTaskExecutor.queueRunnable { authCodeDao.insertCode(code) }
    }

    val listCodesLocal: LiveData<List<AuthCode>>
        get() = authCodeDao.loadAllCodes()

    fun deleteCode(authCode: AuthCode?) {
        SingleTaskExecutor.queueRunnable { authCodeDao.deleteCode(authCode) }
    }

    fun insertCodes(codes: List<AuthCode?>?) {
        SingleTaskExecutor.queueRunnable { authCodeDao.insertCodes(codes) }
    }

    fun updateCode(authCode: AuthCode?) {
        SingleTaskExecutor.queueRunnable { authCodeDao.updateCode(authCode) }
    }

    fun saveShowCodePref(value: Boolean?) {
        val sharedPreferences = AuthenticatorApp.getInstance().getSharedPreferences(
                PREFERENCES_STATE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(SHOW_CODE_PREFERENCES_KEY, value!!)
        editor.apply()
    }

    val showCodePref: Boolean
        get() {
            val sharedPreferences = AuthenticatorApp.getInstance().getSharedPreferences(
                    PREFERENCES_STATE_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(SHOW_CODE_PREFERENCES_KEY, true)
        }
}