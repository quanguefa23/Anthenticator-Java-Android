package com.nhq.authenticator.data.repository

import android.app.Activity
import android.content.Intent

interface SignInManager {
    fun getSignInIntent(activity: Activity): Intent
}