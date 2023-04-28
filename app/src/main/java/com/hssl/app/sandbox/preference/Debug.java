package com.hssl.app.sandbox.preference;

import android.os.Bundle;
import android.util.Log;

public class Debug {
    public static final boolean IS_DEBUG = true;
    public static Bundle d(String TAG, String msg) {
        if (IS_DEBUG)
            Log.d(TAG, msg);
        return null;
    }

    public static void w(String TAG, String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String TAG, Exception e) {
        Log.e(TAG, e.getLocalizedMessage());
        if (IS_DEBUG) {
            e.printStackTrace();
        }
    }
}
