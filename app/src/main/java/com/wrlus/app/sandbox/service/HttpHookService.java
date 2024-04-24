package com.wrlus.app.sandbox.service;

import com.wrlus.app.sandbox.entity.HttpData;
import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.storage.dao.HttpDao;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HttpHookService extends BaseHookService {
    private static final String TAG = "HttpHookService";
    private HttpDao httpDao;

    @Override
    public void onCreate() {
        httpDao = initDatabase().httpDao();
        listenThread = new HttpDataListenThread();
        super.onCreate();
    }

    class HttpDataListenThread extends ListenThread {
        public HttpDataListenThread() {
            super("AppSandbox_Http", "http");
        }

        @Override
        public BaseHookService.HandlerTask createHandlerTask() {
            return new HttpDataHandlerTask();
        }
    }

    class HttpDataHandlerTask extends HandlerTask {

        @Override
        public void run() {
            try {
                File bodySaveFile = new File(subDataDir, UUID.randomUUID().toString() +
                        BIN_DATA_FILE_SUFFIX);
                HttpData httpData = HttpData.openStream(socket.getInputStream(),
                        bodySaveFile.getAbsolutePath());
                socket.close();
                httpDao.insertHttpData(httpData);
            } catch (IOException e) {
                Debug.e(TAG, e);
            }
        }
    }
}