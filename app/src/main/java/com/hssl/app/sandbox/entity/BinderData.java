package com.hssl.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hssl.app.sandbox.preference.Debug;
import com.hssl.app.sandbox.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;

@Entity(tableName = "binder_data")
public class BinderData {
    private static final String TAG = "BinderData";
//    Max binder buffer size is 1MB per process.
    private static final int BINDER_DATA_RECV_BUF_LEN = 1024 * 1024;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "uid")
    private int uid;
    @ColumnInfo(name = "pid")
    private int pid;
    @ColumnInfo(name = "interface_token")
    private String interfaceToken;
    @ColumnInfo(name = "operation")
    private int operation;
    @ColumnInfo(name = "code")
    private int code;
    @ColumnInfo(name = "timestamp")
    private long timestamp;
    @ColumnInfo(name = "data")
    private byte[] data;

    public static BinderData openStream(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        BinderData binderData = new BinderData();

        binderData.uid = Integer.parseInt(StringUtils.readLine(is));
        binderData.pid = Integer.parseInt(StringUtils.readLine(is));
        binderData.code = Integer.parseInt(StringUtils.readLine(is));
        binderData.timestamp = Long.parseLong(StringUtils.readLine(is));

        int dataLen = Integer.parseInt(StringUtils.readLine(is));
        if (dataLen > BINDER_DATA_RECV_BUF_LEN) {
            Debug.e(TAG, "dataLen > BINDER_DATA_RECV_BUF_LEN, excepted " + dataLen +
                    ", uid " + binderData.uid + ", pid " + binderData.pid);
            return binderData;
        }
        binderData.data = new byte[dataLen];
        int readSize = is.read(binderData.data);
        if (readSize != dataLen) {
            Debug.w(TAG, "readSize != dataLen, excepted "+ dataLen +
                    ", found " + readSize + ", uid " + binderData.uid + ", pid " + binderData.pid);
        }
        Debug.d(TAG, "Received " + binderData);
        return binderData;
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

    public String getInterfaceToken() {
        return interfaceToken;
    }

    public void setInterfaceToken(String interfaceToken) {
        this.interfaceToken = interfaceToken;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        int dataLen = data != null ? data.length : 0;
        return "BinderData{" +
                "id=" + id +
                ", uid=" + uid +
                ", pid=" + pid +
                ", interfaceToken='" + interfaceToken + '\'' +
                ", operation=" + operation +
                ", code=" + code +
                ", dataLen=" + dataLen +
                '}';
    }
}
