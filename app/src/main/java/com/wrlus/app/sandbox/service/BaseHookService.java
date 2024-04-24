package com.wrlus.app.sandbox.service;

import android.app.Service;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.IBinder;

import androidx.room.Room;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.storage.db.MainDatabase;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BaseHookService extends Service {
    private static final String TAG = "BaseHookService";
    public static final String MAIN_DATA_DIR_NAME = "main_data";
    public static final String MAIN_DB_NAME = "main_record.db";
    public static final String BIN_DATA_FILE_SUFFIX = ".bin";
    public static final String APK_FILE_SUFFIX = ".apk";

    protected ListenThread listenThread;

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

    public MainDatabase initDatabase() {
        return Room.databaseBuilder(this, MainDatabase.class, MAIN_DB_NAME)
                .enableMultiInstanceInvalidation()
                .build();
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
            File dataDirFile = new File(getExternalFilesDir(null), MAIN_DATA_DIR_NAME);
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
         * We use cached thread pool by default.
         * @return a cached thread pool executor service
         */
        public ThreadPoolExecutor createExecutorService() {
            return (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }

        public abstract HandlerTask createHandlerTask();
    }

    abstract static class HandlerTask implements Runnable {
        protected LocalSocket socket;
        protected String subDataDir;

        public void setSocket(LocalSocket socket) {
            this.socket = socket;
        }

        public void setSubDataDir(String subDataDir) {
            this.subDataDir = subDataDir;
        }
    }
}
