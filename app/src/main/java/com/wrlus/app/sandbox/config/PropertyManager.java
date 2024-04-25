package com.wrlus.app.sandbox.config;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.utils.Constant;

public class PropertyManager {
    private static final String TAG = "PropertyManager";

    static {
        System.loadLibrary("sandbox");
    }

    public static int getWatchedUid(String what) {
        String targetUidStr = get(String.format(Constant.FMT_PROPERTY_WATCHED_UID, what));
        if (targetUidStr != null && !targetUidStr.isEmpty()) {
            try {
                return Integer.parseInt(targetUidStr);
            } catch (NumberFormatException e) {
                Debug.e(TAG, e);
            }
        }
        return -1;
    }

    public static void setWatchedUid(String what, int val) {
        set(String.format(Constant.FMT_PROPERTY_WATCHED_UID, what), String.valueOf(val));
    }

    public static native String get(String key);
    public static native void set(String key, String value);
}
