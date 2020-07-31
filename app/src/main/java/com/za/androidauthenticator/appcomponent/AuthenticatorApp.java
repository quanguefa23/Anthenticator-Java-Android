package com.za.androidauthenticator.appcomponent;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.za.androidauthenticator.di.component.ApplicationComponent;
import com.za.androidauthenticator.di.component.DaggerApplicationComponent;

// appGraph lives in the Application class to share its lifecycle
public class AuthenticatorApp extends Application {

    public static final String APP_TAG = "QUANG";
    public static int DIFF_TIME_SECOND = 0;
    public static String CODE_CHANNEL_ID = "OTP-Code";

    // Reference to the application graph that is used across the whole app
    public ApplicationComponent appGraph = DaggerApplicationComponent.create();

    private static AuthenticatorApp instance;

    public static synchronized AuthenticatorApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "OTP Code";
            String description = "OTP Code pin notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CODE_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}