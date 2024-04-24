package com.wrlus.app.sandbox.utils;

import android.content.Context;
import java.lang.reflect.Method;

public class Env {
    public static Context getSystemContext() {
        try {
            Method method = Class.forName("android.app.ContextImpl")
                    .getDeclaredMethod("createSystemContext", Class.forName("android.app.ActivityThread"));
            method.setAccessible(true);
            return (Context) method.invoke(null, getOrCreateActivityThread());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object getOrCreateActivityThread() throws ReflectiveOperationException{
        Object at = currentActivityThread();
        if (at == null) {
            at = Class.forName("android.app.ActivityThread").getDeclaredMethod("systemMain").invoke(null);
        }
        return at;
    }
    public static Object currentActivityThread() throws ReflectiveOperationException{
        return Class.forName("android.app.ActivityThread").getDeclaredMethod("currentActivityThread").invoke(null);
    }
}