package com.wrlus.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.wrlus.app.sandbox.preference.Debug;
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

    public DexFileData() {}

    public static DexFileData openStream(InputStream is, String dexSaveFile) throws IOException {
        if (is == null){
            return null;
        }
        DexFileData dexFileData = new DexFileData();

        dexFileData.uid = Integer.parseInt(StringUtils.readLine(is));
        dexFileData.pid = Integer.parseInt(StringUtils.readLine(is));
        dexFileData.packageName = StringUtils.readLine(is);
        dexFileData.timestamp = Long.parseLong(StringUtils.readLine(is));
        dexFileData.originDexPath = StringUtils.readLine(is);

//        Start reading dex file
        dexFileData.dexSaveFile = dexSaveFile;

        FileOutputStream fos = new FileOutputStream(dexSaveFile);
        byte[] buffer = new byte[10240];
        int len;
        while ( (len = is.read(buffer)) != -1 ) {
            fos.write(buffer, 0, len);
            fos.flush();
        }
        fos.close();
        Debug.d(TAG, "Received " + dexFileData);
        return dexFileData;
    }

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
