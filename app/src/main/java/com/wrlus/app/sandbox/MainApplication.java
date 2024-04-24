package com.wrlus.app.sandbox;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.room.Room;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.service.BinderHookService;
import com.wrlus.app.sandbox.service.DexHookService;
import com.wrlus.app.sandbox.service.HttpHookService;
import com.wrlus.app.sandbox.service.IntentHookService;
import com.wrlus.app.sandbox.storage.db.MainDatabase;

import java.io.File;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static final String MAIN_DATA_DIR_NAME = "main_data";
    public static final String MAIN_DB_NAME = "main_record.db";
    public String databaseFile;
    private MainDatabase mainDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
        initService();
    }

    private void initDatabase() {
        databaseFile = new File(getExternalFilesDir(null), MAIN_DB_NAME).getAbsolutePath();
        mainDatabase = Room.databaseBuilder(this, MainDatabase.class, databaseFile)
                .enableMultiInstanceInvalidation()
                .build();
    }

    private void initService() {
        startLocalService(BinderHookService.class);
        startLocalService(DexHookService.class);
        startLocalService(HttpHookService.class);
        startLocalService(IntentHookService.class);
    }

    private void startLocalService(Class<?> serviceClass) {
        Intent intent = new Intent(this, serviceClass);
        startService(intent);
    }

    public long getHttpDataCount() {
        if (mainDatabase != null) {
            return mainDatabase.httpDao().getHttpDataCount();
        }
        return 0;
    }

    public long getBinderDataCount() {
        if (mainDatabase != null) {
            return mainDatabase.binderDao().getBinderDataCount();
        }
        return 0;
    }

    public long getDexDataCount() {
        if (mainDatabase != null) {
            return mainDatabase.dexFileDao().getDexFileCount();
        }
        return 0;
    }

    public long getIntentDataCount() {
        if (mainDatabase != null) {
            return mainDatabase.intentDao().getIntentDataCount();
        }
        return 0;
    }

    public void clearData() {
        File dbFile = new File(databaseFile);
        if (!dbFile.exists()) {
            Toast.makeText(this, R.string.no_such_file, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isDeletedDir = new File(getExternalFilesDir(null), MAIN_DATA_DIR_NAME).delete();
        boolean isDeletedDb = dbFile.delete();
        Debug.d(TAG, "clearData() result: isDeletedDir = "+isDeletedDir+
                ", isDeletedDb="+isDeletedDb);
        Toast.makeText(this, R.string.clear_success, Toast.LENGTH_SHORT).show();
    }
}
