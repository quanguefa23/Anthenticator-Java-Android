package com.za.androidauthenticator.appcomponent.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.appcomponent.service.PinCodeService;
import com.za.androidauthenticator.util.ClipBoardUtil;

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
            copyClipboardAction(context, intent);
        }

        // Close system dialogs
        Intent cancelIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(cancelIntent);
    }

    public void cancelAction(Context context) {
        // Stop pin code service
        Intent serviceIntent = new Intent(context, PinCodeService.class);
        context.stopService(serviceIntent);
    }

    public void copyClipboardAction(Context context, Intent intent){
        String code = intent.getStringExtra("code");
        if (ClipBoardUtil.copyStringToClipBoard("2fa", code, context))
            Toast.makeText(context, R.string.copy_code_clipboard, Toast.LENGTH_SHORT).show();
    }
}