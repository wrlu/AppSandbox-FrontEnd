package com.wrlus.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.utils.Constant;
import com.wrlus.app.sandbox.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;

@Entity(tableName = "binder_data")
public class BinderData extends BaseData {
    private static final String TAG = "BinderData";

    @ColumnInfo(name = "interface_token")
    String interfaceToken;
    @ColumnInfo(name = "operation")
    int operation;
    @ColumnInfo(name = "code")
    int code;
    @ColumnInfo(name = "data")
    byte[] data;

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
        if (dataLen > Constant.BINDER_DATA_RECEIVE_BUF_LEN) {
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() +
                ", BinderData{" +
                "interfaceToken='" + interfaceToken + '\'' +
                ", operation=" + operation +
                ", code=" + code +
                '}';
    }
}
