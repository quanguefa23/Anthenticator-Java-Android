package com.za.androidauthenticator.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.view.WindowManager;

import com.za.androidauthenticator.R;

public class ConfigStatusBar {
    public static void setStatusBarGradient(Activity activity) {
        Window window = activity.getWindow();
        Drawable background = activity.getResources().getDrawable(R.drawable.layout_gradient, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent, null));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(background);
    }
}