package com.nhq.authenticator.data.repository;

import android.app.Activity;
import android.content.Intent;

public interface SignInManager {
    Intent getSignInIntent(Activity activity);
}