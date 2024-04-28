package com.wrlus.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.wrlus.app.sandbox.config.PropertyManager;
import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.utils.Constant;
import com.wrlus.app.sandbox.utils.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Entity(tableName = "dexfile_data")
public class DexFileData extends BaseData {
    private static final String TAG = "DexFileData";

    @ColumnInfo(name = "package_name")
    String packageName;
    @ColumnInfo(name = "originDexPath")
    String originDexPath;
    @ColumnInfo(name = "dex")
    String dexSaveFile;

    static {
        System.loadLibrary("sandbox");
    }

    public static native DexFileData openStreamNative(int clientFd, String dexSaveFile);

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getOriginDexPath() {
        return originDexPath;
    }

    public void setOriginDexPath(String originDexPath) {
        this.originDexPath = originDexPath;
    }

    public String getDexSaveFile() {
        return dexSaveFile;
    }

    public void setDexSaveFile(String dexSaveFile) {
        this.dexSaveFile = dexSaveFile;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() +
                ", DexFileData{" +
                "packageName='" + packageName + '\'' +
                ", originDexPath='" + originDexPath + '\'' +
                ", dexSaveFile='" + dexSaveFile + '\'' +
                '}';
    }
}
