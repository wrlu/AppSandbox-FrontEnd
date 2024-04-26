package com.wrlus.app.sandbox.service;

import com.wrlus.app.sandbox.MainApplication;
import com.wrlus.app.sandbox.entity.BinderData;
import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.storage.dao.BinderDao;
import com.wrlus.app.sandbox.storage.db.MainDatabase;
import com.wrlus.app.sandbox.utils.Constant;

import java.io.IOException;
import java.io.InputStream;

public class BinderHookService extends BaseHookService {
    private static final String TAG = "BinderHookService";
    private BinderDao binderDao;

    @Override
    public void onCreate() {
        MainDatabase mainDb = MainApplication.getMainDatabase();
        if (mainDb != null) {
            binderDao = mainDb.binderDao();
            listenThread = new BinderDataListenThread();
        } else {
            Debug.e(TAG, "MainDatabase is null, cannot start service.");
        }
        super.onCreate();
    }

    class BinderDataListenThread extends ListenThread {
        public BinderDataListenThread() {
            super(Constant.UDS_NAME_BINDER, Constant.FEATURE_BINDER, true);
        }

        @Override
        public BaseHookService.HandlerTask createHandlerTask() {
            return new BinderDataHandlerTask();
        }
    }

    class BinderDataHandlerTask extends HandlerTask {

        @Override
        public void run() {
            BinderData binderData;
            if (isUseNative) {
                binderData = BinderData.openStreamNative(clientFd);
                BaseHookService.closeFdNative(clientFd);
            } else {
                InputStream is;
                try {
                    is = socket.getInputStream();
                } catch (IOException e) {
                    Debug.e(TAG, e);
                    return;
                }
                binderData = BinderData.openStream(is);
                try {
                    socket.close();
                } catch (IOException e) {
                    Debug.e(TAG, e);
                }
            }
            if (binderData != null) {
                binderDao.insertBinderData(binderData);
            }
        }
    }
}