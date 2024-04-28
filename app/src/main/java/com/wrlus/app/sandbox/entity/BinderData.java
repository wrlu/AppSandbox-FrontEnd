package com.wrlus.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "binder_data")
public class BinderData extends BaseData {
    @ColumnInfo(name = "interface_token")
    String interfaceToken;
    @ColumnInfo(name = "operation")
    int operation;
    @ColumnInfo(name = "code")
    int code;
    @ColumnInfo(name = "data")
    byte[] data;

    static {
        System.loadLibrary("sandbox");
    }

    public static native BinderData openStreamNative(int clientFd);

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
