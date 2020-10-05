package com.nhq.authenticator.data.repository

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.nhq.authenticator.data.entity.AuthCode
import com.nhq.authenticator.data.repository.local.LocalDataSource
import com.nhq.authenticator.data.repository.remote.RemoteDataSource
import com.nhq.authenticator.data.repository.remote.RemoteDataSource.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val localDataSource: LocalDataSource,
                                     private val remoteDataSource: RemoteDataSource) {

    fun deleteCode(authCode: AuthCode?) {
        localDataSource.deleteCode(authCode)
    }

    fun saveShowCodePref(value: Boolean?) {
        localDataSource.saveShowCodePref(value)
    }

    val showCodePref: Boolean
        get() = localDataSource.showCodePref

    val listCodesLocal: LiveData<List<AuthCode>>
        get() = localDataSource.listCodesLocal

    fun insertNewCode(key: String?, siteName: String?, accountName: String?) {
        localDataSource.insertNewCode(AuthCode(key!!, siteName!!, accountName!!))
    }

    fun updateCode(authCode: AuthCode?) {
        localDataSource.updateCode(authCode)
    }

    fun getListDataFile(callBack: GetListFileCallBack) {
        remoteDataSource.getListDataFile(callBack)
    }

    fun insertCodes(newCodes: List<AuthCode?>?) {
        localDataSource.insertCodes(newCodes)
    }

    fun getCodesViaFileId(id: String?, callBack: GetCodesCallBack) {
        remoteDataSource.getCodesViaFileId(id, callBack)
    }

    fun createDataFile(exportString: String?, callBack: CreateFileCallBack) {
        remoteDataSource.createDataFile(exportString, callBack)
    }

    fun getTime(callBack: GetTimeCallBack) {
        remoteDataSource.getTime(callBack)
    }

    fun handleSignInResult(result: Intent?, context: Context?, callback: SignInCallback) {
        remoteDataSource.handleSignInResult(result, context, callback)
    }
}