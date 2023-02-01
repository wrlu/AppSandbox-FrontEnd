package com.hssl.app.sandbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hssl.app.sandbox.preference.Debug;

public class SystemReceiver extends BroadcastReceiver {
    private static final String TAG = "SystemReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                Debug.d(TAG, "onReceive() BOOT_COMPLETED");
            }
        }
    }
}