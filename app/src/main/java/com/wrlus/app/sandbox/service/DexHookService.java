package com.wrlus.app.sandbox.service;

import android.content.pm.PackageManager;
import android.os.Process;

import com.wrlus.app.sandbox.MainApplication;
import com.wrlus.app.sandbox.entity.DexFileData;
import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.storage.dao.DexFileDao;
import com.wrlus.app.sandbox.storage.db.MainDatabase;
import com.wrlus.app.sandbox.utils.Constant;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DexHookService extends BaseHookService {
    private static final String TAG = "DexHookService";
    private DexFileDao dexFileDao;

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
            try {
                File dexSaveFile = new File(subDataDir, UUID.randomUUID().toString() +
                        Constant.APK_FILE_SUFFIX);
                DexFileData dexFileData = DexFileData.openStream(socket.getInputStream(),
                        dexSaveFile.getAbsolutePath());
                socket.close();
                fixDexFileData(dexFileData);
                dexFileDao.insertDexFile(dexFileData);
            } catch (IOException e) {
                Debug.e(TAG, e);
            }
        }

        private void fixDexFileData(DexFileData dexFileData) {
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
    }
}