package com.hssl.app.sandbox.service;

import android.content.Intent;
import android.os.IBinder;

import com.hssl.app.sandbox.entity.DexFileData;
import com.hssl.app.sandbox.preference.Debug;
import com.hssl.app.sandbox.storage.dao.DexFileDao;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DexHookService extends BaseHookService {
    private static final String TAG = "DexHookService";
    private DexFileDao dexFileDao;

    @Override
    public void onCreate() {
        dexFileDao = initDatabase().dexFileDao();
        listenThread = new DexFileDataListenThread();
        super.onCreate();
    }

    class DexFileDataListenThread extends ListenThread {
        public DexFileDataListenThread() {
            super("AppSandbox_Dex", "dex");
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
                        APK_FILE_SUFFIX);
                DexFileData dexFileData = DexFileData.openStream(socket.getInputStream(),
                        dexSaveFile.getAbsolutePath());
                socket.close();
                dexFileDao.insertDexFile(dexFileData);
            } catch (IOException e) {
                Debug.e(TAG, e);
            }
        }
    }
}