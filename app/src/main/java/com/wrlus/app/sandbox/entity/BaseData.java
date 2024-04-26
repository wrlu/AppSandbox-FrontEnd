package com.wrlus.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.wrlus.app.sandbox.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public abstract class BaseData {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    long id;
    @ColumnInfo(name = "uid")
    int uid;
    @ColumnInfo(name = "pid")
    int pid;
    @ColumnInfo(name = "timestamp")
    long timestamp;

    static void openStream(InputStream is, BaseData baseData) throws IOException {
        if (is == null) return;

        baseData.uid = Integer.parseInt(StringUtils.readLine(is));
        baseData.pid = Integer.parseInt(StringUtils.readLine(is));
        baseData.timestamp = Long.parseLong(StringUtils.readLine(is));
    }

    public static native void openStreamNative(int client_fd, BaseData baseData);

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @NonNull
    @Override
    public String toString() {
        return "BaseData{" +
                "id=" + id +
                ", uid=" + uid +
                ", pid=" + pid +
                ", timestamp=" + timestamp +
                '}';
    }
}
