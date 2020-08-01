package com.za.androidauthenticator.appcomponent.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.appcomponent.AuthenticatorApp;
import com.za.androidauthenticator.appcomponent.broadcastreceiver.PinCodeActionReceiver;
import com.za.androidauthenticator.data.entity.AuthCode;
import com.za.androidauthenticator.util.FormatStringUtil;
import com.za.androidauthenticator.util.calculator.CalculateCodeUtil;
import com.za.androidauthenticator.view.activities.DetailCodeActivity;

public class PinCodeService extends Service {

    public static final String ACTION_CANCEL = "cancel_action";
    public static final String ACTION_COPY = "copy_action";

    private final int CANCEL_REQUEST_CODE = 124;
    private final int COPY_REQUEST_CODE = 125;
    private final int ONGOING_NOTIFICATION_ID = 232;
    private final String CODE_CHANNEL_ID = "OTP-Code";

    private CalculateCodeUtil calculateCodeUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get AuthCode instance
        AuthCode authCode = (AuthCode) intent.getSerializableExtra("authCode");
        if (authCode == null)
            return START_REDELIVER_INTENT;

        // Create an explicit intent for tap action
        Intent contentIntent = new Intent(this, DetailCodeActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contentIntent.putExtra("authCode", authCode);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, contentIntent, 0);

        // Create notification (without content (code), we will set content later)
        NotificationCompat.Builder builder = createBuilder(authCode, contentPendingIntent);

        // Add intent for cancel action
        Intent cancelIntent = new Intent(this, PinCodeActionReceiver.class);
        cancelIntent.setAction(ACTION_CANCEL);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this,
                CANCEL_REQUEST_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_cancel, getString(R.string.cancel), cancelPendingIntent);

        // Add intent for copy action
        Intent copyIntent = new Intent(this, PinCodeActionReceiver.class);
        copyIntent.setAction(ACTION_COPY);
        PendingIntent copyPendingIntent = PendingIntent.getBroadcast(this,
                COPY_REQUEST_CODE, copyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_add, getString(R.string.copy), copyPendingIntent);

        // Register callback to update UI (code)
        CalculateCodeUtil.OnUpdateCode updateCodeCallback = codes -> {
            // set content for notification
            builder.setContentText(FormatStringUtil.formatCodeToString(codes.get(0)));
            startForeground(ONGOING_NOTIFICATION_ID, builder.build());
        };

        // stop old calculator if exist
        if (calculateCodeUtil != null)
            calculateCodeUtil.stopCalculate();

        // start new calculator
        calculateCodeUtil = new CalculateCodeUtil(authCode.key, null, updateCodeCallback);
        calculateCodeUtil.startCalculate();

        return START_REDELIVER_INTENT;
    }

    private NotificationCompat.Builder createBuilder(AuthCode authCode, PendingIntent contentPendingIntent) {
        String title = authCode.siteName + " (" + authCode.accountName + ")";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CODE_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_app)
                        .setContentTitle(title)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(contentPendingIntent);

        return builder;
    }

    @Override
    public void onDestroy() {
        Log.d(AuthenticatorApp.APP_TAG, "destroy PinCodeService");
        super.onDestroy();
        if (calculateCodeUtil != null)
            calculateCodeUtil.stopCalculate();
    }
}
