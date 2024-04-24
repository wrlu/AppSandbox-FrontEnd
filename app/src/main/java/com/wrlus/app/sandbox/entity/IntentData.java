package com.wrlus.app.sandbox.entity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.utils.Env;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;


@Entity(tableName = "intent_data")
@TypeConverters(IntentData.MapConverter.class)
public class IntentData {
    private static final String TAG = "IntentData";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "uid")
    private int uid;
    @ColumnInfo(name = "pid")
    private int pid;
    @ColumnInfo(name = "timestamp")
    private long timestamp;
    @ColumnInfo(name = "operation")
    private String operation;
    @ColumnInfo(name = "action")
    private String action;
    @ColumnInfo(name = "data")
    private String data;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "identifier")
    private String identifier;
    @ColumnInfo(name = "package_name")
    private String packageName;
    @ColumnInfo(name = "component_name")
    private String componentName;
    @ColumnInfo(name = "flags")
    private int flags;
    @ColumnInfo(name = "categories")
    private String categories;
    @ColumnInfo(name = "clip_data")
    private String clipData;
    @ColumnInfo(name = "extra")
    private byte[] extra;
    @ColumnInfo(name = "parsed_extra")
    private Map<String, String> parsedExtra;


    public static class MapConverter{
        @TypeConverter
        public static String mapToString(Map<String, String> map){
            Gson gson = new Gson();
            return gson.toJson(map);
        }
        @TypeConverter
        public static Map<String, String> stringToMap(String s){
            Gson gson = new Gson();
            return gson.fromJson(s, HashMap.class);
        }

    }


    public static IntentData openStream(InputStream is) throws IOException, ClassNotFoundException {
        if (is == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        IntentData intentData = new IntentData();

        String uidString = reader.readLine();

        if (uidString != null && !uidString.equals("")) {
            intentData.uid = Integer.parseInt(uidString);
        } else {
            Debug.w(TAG, "uid is null");
        }

        String pidString = reader.readLine();
//        debug
        if(pidString.equals("-2")){
            if (pidString != null && !pidString.equals("")) {
                intentData.pid = Integer.parseInt(pidString);
            } else {
                Debug.w(TAG, "pid is null");
            }

            String timeStampString = reader.readLine();
            if (timeStampString != null && !timeStampString.equals("")) {
                intentData.timestamp = Long.parseLong(timeStampString);
            } else {
                Debug.w(TAG, "timestamp is null");
            }

            intentData.operation = reader.readLine();

            intentData.action = reader.readLine();
            intentData.data = reader.readLine();
            intentData.type = reader.readLine();
            intentData.identifier = reader.readLine();
            intentData.packageName = reader.readLine();
            intentData.componentName = reader.readLine();

            String flagsString = reader.readLine();
            if (flagsString != null && !flagsString.equals("")) {
                if (flagsString.startsWith("0x")) {
                    intentData.flags = Integer.parseInt(flagsString, 16);
                } else {
                    intentData.flags = Integer.parseInt(flagsString);
                }
            } else {
                Debug.w(TAG, "flags is null");
            }

            intentData.categories = reader.readLine();
            intentData.clipData = reader.readLine();
            byte[] extraByte = Base64.getDecoder().decode(reader.readLine());
            intentData.extra = extraByte;
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(extraByte, 0, extraByte.length);
            parcel.setDataPosition(0);
            Map<String, String> map = parseExtra(parcel, intentData);
            intentData.parsedExtra = map;
            parcel.recycle();
            Debug.d(TAG, "Received " + intentData);
        }
        return intentData;
    }
    private static Map<String, String> parseExtra(Parcel extraParcel, IntentData intentData){
        if(tryClassLoader(extraParcel, ClassLoader.getSystemClassLoader())){
            Debug.d(TAG, "Match system classloader!");
            return parseExtraToMap(extraParcel, ClassLoader.getSystemClassLoader());
        }
        Context context = Env.getSystemContext();
        PackageManager pm = context.getPackageManager();
        String[] packageNames = pm.getPackagesForUid(intentData.uid);
        for (String packageName: packageNames){
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                DexClassLoader dexClassLoader = new DexClassLoader(applicationInfo.sourceDir, null, applicationInfo.nativeLibraryDir, ClassLoader.getSystemClassLoader());
                if (tryClassLoader(extraParcel, dexClassLoader)){
                    Debug.d(TAG, "Match classloader in " + packageName);
                    return parseExtraToMap(extraParcel, dexClassLoader);
                }
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
        }
        Debug.e(TAG, "Cannot match classloader");
        return null;
//        todo: 数据库byte[] extras 如果相等，则复用
    }

    private static Map<String, String> parseExtraToMap(Parcel extraParcel, ClassLoader classLoader){
        Map<String, String> map = new HashMap<>();
        Bundle bundle = new Bundle();
        extraParcel.setDataPosition(0);
        bundle.readFromParcel(extraParcel);
        bundle.setClassLoader(classLoader);
        for(String key: bundle.keySet()){
            Object value = bundle.get(key);
            String data;
            String valueClassName = value.getClass().getName();
            Debug.d(TAG, "key = " + key + ", value clz name = " + valueClassName);

            if (isBootstrapClass(valueClassName)) {
                data = value.toString();
            } else {
                try{
                    Class<?> valueClassObj = findClass(valueClassName, classLoader);
                    Field[] valueFields = valueClassObj.getDeclaredFields();
                    Debug.d(TAG, "valueFields length = " + valueFields.length);
                    for (Field field : valueFields) {
                        try {
                            field.setAccessible(true);
                            if (isBootstrapClass(field.getType().getName())) {
                                Object valueFieldValue = field.get(value);
                                Debug.d(TAG, "key = " + key + ", value = " + valueFieldValue);
                            } else {
                                Class<?> fieldClassObj = findClass(field.getType().getName(), classLoader);

                            }

                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                        }
                    }
                    data = (String) valueClassObj.getDeclaredMethod("toString").invoke(value);
                }catch (ReflectiveOperationException exception){
                    exception.printStackTrace();
                    data = valueClassName;
                }
            }
            map.put(key, data);
        }
        return map;
    }

    private static Class<?> findClass(String name, ClassLoader classLoader) {
        try {
            Method findClassMethod = BaseDexClassLoader.class
                    .getDeclaredMethod("findClass", String.class);
            findClassMethod.setAccessible(true);
            return (Class<?>) findClassMethod.invoke(classLoader, name);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isBootstrapClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    private static boolean tryClassLoader(Parcel extraParcel, ClassLoader classLoader){
        Bundle bundle = new Bundle();
        extraParcel.setDataPosition(0);
        bundle.readFromParcel(extraParcel);
        bundle.setClassLoader(classLoader);
        try{
            bundle.keySet();
            return true;
        }catch (Exception e){
            return false;
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getClipData() {
        return clipData;
    }

    public void setClipData(String clipData) {
        this.clipData = clipData;
    }

    public byte[] getExtra() {
        return extra;
    }

    public void setExtra(byte[] extra) {
        this.extra = extra;
    }

    public Map<String, String> getParsedExtra() {
        return parsedExtra;
    }

    public void setParsedExtra(Map<String, String> parsedExtra) {
        this.parsedExtra = parsedExtra;
    }

    @Override
    public String toString() {
        return "IntentData{" +
                "id=" + id +
                ", uid=" + uid +
                ", pid=" + pid +
                ", timestamp=" + timestamp +
                ", operation='" + operation + '\'' +
                ", action='" + action + '\'' +
                ", data='" + data + '\'' +
                ", type='" + type + '\'' +
                ", identifier='" + identifier + '\'' +
                ", packageName='" + packageName + '\'' +
                ", componentName='" + componentName + '\'' +
                ", flags=" + flags +
                ", categories='" + categories + '\'' +
                ", clipData='" + clipData + '\'' +
                ", parsedExtra=" + parsedExtra +
                '}';
    }
}
