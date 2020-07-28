package com.za.androidauthenticator.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class CopyToClipBoard {
    public static boolean copyStringToClipBoard(String label, String content, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label,content);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            return true;
        }
        return false;
    }
}
