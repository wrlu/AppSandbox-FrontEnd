package com.wrlus.app.sandbox.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.utils.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Entity(tableName = "dexfile_data")
public class DexFileData {
    private static final String TAG = "DexFileData";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "uid")
    private int uid;
    @ColumnInfo(name = "pid")
    private int pid;
    @ColumnInfo(name = "package_name")
    private String packageName;
    @ColumnInfo(name = "timestamp")
    private long timestamp;
    @ColumnInfo(name = "originDexPath")
    private String originDexPath;
    @ColumnInfo(name = "dex")
    private String dexSaveFile;

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
        byte[] buffer = new byte[1024];
        int len;
        while ( (len = is.read(buffer)) != -1 ) {
            fos.write(buffer, 0, len);
            fos.flush();
        }
        fos.close();
        Debug.d(TAG, "Received " + dexFileData);
        return dexFileData;
    }

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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "DexFileData{" +
                "id=" + id +
                ", uid=" + uid +
                ", pid=" + pid +
                ", packageName='" + packageName + '\'' +
                ", timestamp=" + timestamp +
                ", originDexPath='" + originDexPath + '\'' +
                ", dexSaveFile='" + dexSaveFile + '\'' +
                '}';
    }
}
