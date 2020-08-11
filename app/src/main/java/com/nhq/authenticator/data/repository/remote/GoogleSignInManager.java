package com.nhq.authenticator.data.repository.remote;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.nhq.authenticator.data.repository.SignInManager;

public class GoogleSignInManager implements SignInManager {
    @Override
    public Intent getSignInIntent(Activity activity) {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(activity, signInOptions);
        return client.getSignInIntent();
    }
}