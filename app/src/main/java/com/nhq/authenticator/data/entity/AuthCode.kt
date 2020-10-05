package com.nhq.authenticator.data.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nhq.authenticator.appcomponent.AuthenticatorApp
import com.nhq.authenticator.data.contract.SitesAvailable
import com.nhq.authenticator.util.calculator.TimeBasedOTPUtil
import java.io.Serializable
import java.util.*

@Entity(tableName = "auth_code")
class AuthCode(@JvmField var key: String,
               @JvmField var siteName: String,
               @JvmField var accountName: String) : Serializable {

    @JvmField
    @PrimaryKey(autoGenerate = true)
    var codeId = 0

    @JvmField
    @Ignore
    var reTime = 30

    @JvmField
    @Ignore
    var currentCode = "000 000"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val authCode = other as AuthCode
        return key == authCode.key &&
                siteName == authCode.siteName &&
                accountName == authCode.accountName &&
                codeId == authCode.codeId
    }

    fun equalInContent(other: AuthCode?): Boolean {
        if (this === other) return true
        return if (other == null) false
            else key == other.key &&
                siteName == other.siteName &&
                accountName == other.accountName
    }

    override fun hashCode(): Int {
        return Objects.hash(codeId, key, siteName, accountName)
    }

    override fun toString(): String {
        return "$codeId|$key|$siteName|$accountName"
    }

    companion object {
        @JvmStatic
        fun parseAuthCode(content: String): AuthCode? {
            var res: AuthCode? = null
            val params = content.split("\\|".toRegex()).toTypedArray()
            try {
                val key = params[1]
                if (!TimeBasedOTPUtil.isValidKey(key)) {
                    throw Exception("Invalid key: $key")
                }
                val siteName = params[2]
                val accountName = params[3]
                res = AuthCode(key, siteName, accountName)
            } catch (e: Exception) {
                Log.e(AuthenticatorApp.APP_TAG, e.message)
            }
            return res
        }
    }

}