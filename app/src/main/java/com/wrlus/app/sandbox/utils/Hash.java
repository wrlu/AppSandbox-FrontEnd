package com.wrlus.app.sandbox.utils;

import com.wrlus.app.sandbox.preference.Debug;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class Hash {
    private static final String TAG = "sandbox_hash";
    public static String getFileHash(String path, String alg) {
        try {
            MessageDigest md = MessageDigest.getInstance(alg);
            FileInputStream fis = new FileInputStream(path);
            byte[] buffer = new byte[Constant.FILE_RW_BUF_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            fis.close();
            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            Debug.e(TAG, e);
        }
        return null;
    }
}
