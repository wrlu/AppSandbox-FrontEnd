package com.wrlus.app.sandbox.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.wrlus.app.sandbox.preference.Debug;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "http_data")
@TypeConverters(HttpData.Converter.class)
public class HttpData {
    private static final String TAG = "HttpData";

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
    @ColumnInfo(name = "requestOrStatusLine")
    private String requestOrStatusLine;
    @ColumnInfo(name = "headerLines")
    private List<String> headerLines;
    @ColumnInfo(name = "body")
    private String bodySaveFile;

    public HttpData() {}

    public static HttpData openStream(InputStream is, String bodySaveFile) throws IOException {
        if (is == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        HttpData httpData = new HttpData();

        String uidString = reader.readLine();
        if (uidString != null && !uidString.equals("")) {
            httpData.uid = Integer.parseInt(uidString);
        } else {
            Debug.w(TAG, "uid is null");
        }

        String pidString = reader.readLine();
        if (pidString != null && !pidString.equals("")) {
            httpData.pid = Integer.parseInt(pidString);
        } else {
            Debug.w(TAG, "pid is null");
        }

        httpData.packageName = reader.readLine();

        String timeStampString = reader.readLine();
        if (timeStampString != null && !timeStampString.equals("")) {
            httpData.timestamp = Long.parseLong(timeStampString);
        } else {
            Debug.w(TAG, "timestamp is null");
        }
//        Start reading headers
        httpData.requestOrStatusLine = reader.readLine();
        httpData.headerLines = new ArrayList<>();
        String headerLine = reader.readLine();
        while ( !(headerLine == null || headerLine.equals("")) ) {
            httpData.headerLines.add(headerLine);
            headerLine = reader.readLine();
        }
//        Start reading body
        byte[] buffer = new byte[10240];
        int readLen = is.read(buffer);
//        No body we want to early return
        if (readLen == -1) {
            Debug.d(TAG, "Received "+ httpData);
            return httpData;
        }
        httpData.bodySaveFile = bodySaveFile;
        FileOutputStream fos = new FileOutputStream(bodySaveFile);
        while ( readLen != -1 ) {
            fos.write(buffer, 0, readLen);
            fos.flush();
            readLen = is.read(buffer);
        }
        fos.close();
        Debug.d(TAG, "Received " + httpData);
        return httpData;
    }

    public static final class Converter {
        @TypeConverter
        public static String saveListString(List<String> in) {
            StringBuilder builder = new StringBuilder();
            for (String str : in) {
                builder.append(str);
                builder.append("\r\n");
            }
            return builder.toString();
        }

        @TypeConverter
        public static List<String> restoreListString(String in) {
            String[] strings = in.split("\\r\\n");
            return new ArrayList<>(Arrays.asList(strings));
        }
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

    public String getRequestOrStatusLine() {
        return requestOrStatusLine;
    }

    public void setRequestOrStatusLine(String requestOrStatusLine) {
        this.requestOrStatusLine = requestOrStatusLine;
    }

    public List<String> getHeaderLines() {
        return headerLines;
    }

    public void setHeaderLines(List<String> headerLines) {
        this.headerLines = headerLines;
    }

    public String getBodySaveFile() {
        return bodySaveFile;
    }

    public void setBodySaveFile(String bodySaveFile) {
        this.bodySaveFile = bodySaveFile;
    }

    @NonNull
    @Override
    public String toString() {
        return "HttpData{" +
                "id=" + id +
                ", uid=" + uid +
                ", pid=" + pid +
                ", packageName='" + packageName + '\'' +
                ", timestamp=" + timestamp +
                ", requestOrStatusLine='" + requestOrStatusLine + '\'' +
                ", headerLines=" + headerLines +
                ", bodySaveFile='" + bodySaveFile + '\'' +
                '}';
    }
}
