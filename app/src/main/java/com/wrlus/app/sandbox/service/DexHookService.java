package com.wrlus.app.sandbox.service;

import android.content.pm.PackageManager;
import android.os.Process;

import com.wrlus.app.sandbox.MainApplication;
import com.wrlus.app.sandbox.entity.DexFileData;
import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.storage.dao.DexFileDao;
import com.wrlus.app.sandbox.storage.db.MainDatabase;
import com.wrlus.app.sandbox.utils.Constant;
import com.wrlus.app.sandbox.utils.Hash;

import java.io.File;
import java.util.UUID;

public class DexHookService extends BaseHookService {
    private static final String TAG = "DexHookService";
    private DexFileDao dexFileDao;
    private static final Object FILE_LOCK = new Object();

    @Override
    public void onCreate() {
        MainDatabase mainDb = MainApplication.getMainDatabase();
        if (mainDb != null) {
            dexFileDao = mainDb.dexFileDao();
            listenThread = new DexFileDataListenThread();
        } else {
            Debug.e(TAG, "MainDatabase is null, cannot start service.");
        }
        super.onCreate();
    }

    class DexFileDataListenThread extends ListenThread {
        public DexFileDataListenThread() {
            super(Constant.UDS_NAME_DEX, Constant.FEATURE_DEX);
        }

        @Override
        public BaseHookService.HandlerTask createHandlerTask() {
            return new DexFileDataHandlerTask();
        }
    }

    class DexFileDataHandlerTask extends HandlerTask {

        @Override
        public void run() {
            // This is only temp file dest, must call `renameDexFile` to fix it.
            File dexSaveFile = new File(subDataDir, UUID.randomUUID().toString() +
                    Constant.APK_FILE_SUFFIX);
            DexFileData dexFileData = DexFileData.openStreamNative(clientFd,
                    dexSaveFile.getAbsolutePath());
            BaseHookService.closeFdNative(clientFd);
            if (dexFileData != null) {
                fixPackageName(dexFileData);
                if (renameDexFile(dexFileData)) {
                    Debug.w(TAG, "Same hash dex file exists.");
                }
                dexFileDao.insertDexFile(dexFileData);
            }
        }

        private void fixPackageName(DexFileData dexFileData) {
            PackageManager pm = getPackageManager();
            int uid = dexFileData.getUid();
            if (Process.isApplicationUid(uid)) {
                String[] pkgNames = pm.getPackagesForUid(uid);
                // Only fix uid with single package name.
                if (pkgNames != null && pkgNames.length == 1) {
                    dexFileData.setPackageName(pkgNames[0]);
                }
            }
        }

        private boolean renameDexFile(DexFileData dexFileData) {
            File dexSaveFile = new File(dexFileData.getDexSaveFile());
            String hash = Hash.getFileHash(dexSaveFile.getAbsolutePath(), "SHA-256");

            File dexSaveFileUseHash = new File(subDataDir, hash + Constant.APK_FILE_SUFFIX);
            dexFileData.setDexSaveFile(dexSaveFileUseHash.getAbsolutePath());

            // Lock here for multi-thread handler may receive same hash files.
            synchronized (FILE_LOCK) {
                boolean isFileExists = dexSaveFileUseHash.exists();
                if (isFileExists) {
                    if (!dexSaveFile.delete()) {
                        Debug.e(TAG, "Delete file " + dexSaveFile.getAbsolutePath() + " failed.");
                    }
                } else {
                    if (!dexSaveFile.renameTo(dexSaveFileUseHash)) {
                        Debug.e(TAG, "Rename file " + dexSaveFile.getAbsolutePath() + " failed.");
                    }
                }
                return isFileExists;
            }
        }

    }
}