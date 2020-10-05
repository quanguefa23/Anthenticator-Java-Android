package com.nhq.authenticator.data.repository.remote

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.nhq.authenticator.data.repository.SignInManager

class GoogleSignInManager : SignInManager {
    override fun getSignInIntent(activity: Activity): Intent {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .build()
        val client = GoogleSignIn.getClient(activity, signInOptions)
        return client.signInIntent
    }
}