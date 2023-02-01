package com.hssl.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hssl.app.sandbox.preference.Debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Entity(tableName = "intent_data")
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
    private String extra;


    public static IntentData openStream(InputStream is) throws IOException {
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
        intentData.extra = reader.readLine();

        Debug.d(TAG, "Received " + intentData);
        return intentData;
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

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }


    @NonNull
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
                ", extra='" + extra + '\'' +
                '}';
    }
}
