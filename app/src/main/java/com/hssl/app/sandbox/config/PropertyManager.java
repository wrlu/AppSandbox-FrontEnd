package com.hssl.app.sandbox.config;

import android.annotation.SuppressLint;

public class PropertyManager {
    public static final String PROPERTY_BINDER_WATCHED_UID = "sandbox.binder.watched.uid";

    public static int getBinderWatchedUid() {
        String targetUidStr = get(PROPERTY_BINDER_WATCHED_UID);
        if (targetUidStr != null) {
            try {
                return Integer.parseInt(targetUidStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static void setBinderWatchedUid(int val) {
        set(PROPERTY_BINDER_WATCHED_UID, String.valueOf(val));
    }

    @SuppressLint("PrivateApi")
    public static String get(String key) {
        try {
            return (String) Class.forName("android.os.SystemProperties")
                    .getDeclaredMethod("get", String.class)
                    .invoke(null, key);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    public static void set(String key, String val) {
        try {
            Class.forName("android.os.SystemProperties")
                    .getDeclaredMethod("set", String.class, String.class)
                    .invoke(null, key, val);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
