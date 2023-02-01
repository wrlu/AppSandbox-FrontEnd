package com.hssl.app.sandbox.service;

import com.hssl.app.sandbox.entity.IntentData;
import com.hssl.app.sandbox.preference.Debug;
import com.hssl.app.sandbox.storage.dao.IntentDao;

import java.io.IOException;

public class IntentHookService extends BaseHookService {
    private static final String TAG = "IntentHookService";
    private IntentDao intentDao;

    @Override
    public void onCreate() {
        intentDao = initDatabase().intentDao();
        listenThread = new IntentDataListenThread();
        super.onCreate();
    }

    class IntentDataListenThread extends ListenThread {
        public IntentDataListenThread() {
            super("AppSandbox_Intent", "intent");
        }

        @Override
        public BaseHookService.HandlerTask createHandlerTask() {
            return new IntentDataHandlerTask();
        }
    }

    class IntentDataHandlerTask extends HandlerTask {

        @Override
        public void run() {
            try {
                IntentData intentData = IntentData.openStream(socket.getInputStream());
                socket.close();
                intentDao.insertIntentData(intentData);
            } catch (IOException e) {
                Debug.e(TAG, e);
            }
        }
    }
}