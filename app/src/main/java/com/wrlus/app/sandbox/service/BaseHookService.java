package com.wrlus.app.sandbox.service;

import android.app.Service;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.IBinder;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.utils.Constant;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class BaseHookService extends Service {
    private static final String TAG = "BaseHookService";

    ListenThread listenThread;

    @Override
    public void onCreate() {
        super.onCreate();
        if (listenThread != null) {
            listenThread.setDaemon(true);
            listenThread.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenThread.interrupt();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    abstract class ListenThread extends Thread {
        private final String localSocketName;
        private final String subDataDir;
        private final ThreadPoolExecutor executor;

        public ListenThread(String localSocketName, String subDataDirChild) {
            this.localSocketName = localSocketName;
            this.subDataDir = createSubDataDir(subDataDirChild);
            this.executor = createExecutorService();
        }

        @Override
        public void run() {
            try {
                LocalServerSocket serverSocket = new LocalServerSocket(localSocketName);
                Debug.d(TAG, "Start listen LocalSocket: "+localSocketName);
                while (!currentThread().isInterrupted()) {
                    LocalSocket socket = serverSocket.accept();
                    HandlerTask handler = createHandlerTask();
                    handler.setSocket(socket);
                    handler.setSubDataDir(subDataDir);
                    executor.execute(handler);
                }
                serverSocket.close();
                executor.shutdown();
            } catch (IOException e) {
                Debug.e(TAG, e);
            }
        }

        public String createSubDataDir(String subDataDir) {
            File dataDirFile = new File(getExternalFilesDir(null),
                    Constant.MAIN_DATA_DIR_NAME);
            if (!dataDirFile.exists()) {
                if (!dataDirFile.mkdir()) {
                    Debug.e(TAG, "mkdir dataDir failed.");
                    return null;
                }
            }
            File subDataDirFile = new File(dataDirFile, subDataDir);
            if (!subDataDirFile.exists()) {
                if (!subDataDirFile.mkdir()) {
                    Debug.e(TAG, "mkdir subDataDir failed.");
                    return null;
                }
            }
            return subDataDirFile.getAbsolutePath();
        }

        /**
         * Create an executor service for handler tasks.
         * We use cached thread pool by default, but subclass can override this method.
         * @return a cached thread pool executor service
         */
        public ThreadPoolExecutor createExecutorService() {
            return (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }

        public abstract HandlerTask createHandlerTask();
    }

    abstract static class HandlerTask implements Runnable {
        LocalSocket socket;
        String subDataDir;

        public void setSocket(LocalSocket socket) {
            this.socket = socket;
        }

        public void setSubDataDir(String subDataDir) {
            this.subDataDir = subDataDir;
        }
    }
}
