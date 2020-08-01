package com.za.androidauthenticator.appcomponent.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.appcomponent.service.PinCodeService;

import static com.za.androidauthenticator.appcomponent.service.PinCodeService.ACTION_CANCEL;
import static com.za.androidauthenticator.appcomponent.service.PinCodeService.ACTION_COPY;

public class PinCodeActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (action.equals(ACTION_CANCEL)) {
            cancelAction(context);
        }
        else if (action.equals(ACTION_COPY)) {
            copyClipboardAction(context);
        }

        // Close system dialogs
        Intent cancelIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(cancelIntent);
    }

    public void cancelAction(Context context) {
        Log.d("QUANG", "cancel action");
        // Stop pin code service
        Intent serviceIntent = new Intent(context, PinCodeService.class);
        context.stopService(serviceIntent);
    }

    public void copyClipboardAction(Context context){
        Toast.makeText(context, R.string.copy_code_clipboard, Toast.LENGTH_SHORT).show();
    }
}