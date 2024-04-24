package com.wrlus.app.sandbox.service;

import com.wrlus.app.sandbox.entity.BinderData;
import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.storage.dao.BinderDao;

import java.io.IOException;

public class BinderHookService extends BaseHookService {
    private static final String TAG = "BinderHookService";
    private BinderDao binderDao;

    @Override
    public void onCreate() {
        binderDao = initDatabase().binderDao();
        listenThread = new BinderDataListenThread();
        super.onCreate();
    }

    class BinderDataListenThread extends ListenThread {
        public BinderDataListenThread() {
            super("AppSandbox_Binder", "binder");
        }

        @Override
        public BaseHookService.HandlerTask createHandlerTask() {
            return new BinderDataHandlerTask();
        }

    }

    class BinderDataHandlerTask extends HandlerTask {

        @Override
        public void run() {
            try {
                BinderData binderData = BinderData.openStream(socket.getInputStream());
                socket.close();
                binderDao.insertBinderData(binderData);
            } catch (IOException e) {
                Debug.e(TAG, e);
            }
        }
    }
}