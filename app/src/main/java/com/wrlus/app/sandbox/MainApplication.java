package com.wrlus.app.sandbox;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.room.Room;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.service.BinderHookService;
import com.wrlus.app.sandbox.service.DexHookService;
import com.wrlus.app.sandbox.storage.db.MainDatabase;
import com.wrlus.app.sandbox.utils.Constant;

import java.io.File;
import java.lang.reflect.Method;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    public static String databaseFile;
    private static MainDatabase mainDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
        initService();
    }

    private void initDatabase() {
        databaseFile = new File(getExternalFilesDir(null),
                Constant.MAIN_DB_NAME).getAbsolutePath();
        mainDatabase = Room.databaseBuilder(this, MainDatabase.class, databaseFile)
                .enableMultiInstanceInvalidation()
                .build();
    }

    public static MainDatabase getMainDatabase() {
        return mainDatabase;
    }

    private void initService() {
        startLocalService(DexHookService.class);
        startLocalService(BinderHookService.class);
    }

    private void startLocalService(Class<?> serviceClass) {
        Intent intent = new Intent(this, serviceClass);
        startService(intent);
    }

    public long getDexDataCount() {
        if (mainDatabase != null) {
            return mainDatabase.dexFileDao().getDexFileCount();
        }
        return 0;
    }

    public long getArtMethodDataCount() {
        // TODO: ArtMethod Dao Impl.
        return 0;
    }
    public long getBinderDataCount() {
        if (mainDatabase != null) {
            return mainDatabase.binderDao().getBinderDataCount();
        }
        return 0;
    }

    public void clearData() {
        File dbFile = new File(databaseFile);
        if (!dbFile.exists()) {
            return;
        }
        File dbShmFile = new File(databaseFile + "-shm");
        File dbWalFile = new File(databaseFile + "-wal");

        boolean isDeletedDb = dbFile.delete() && dbShmFile.delete() && dbWalFile.delete();
        boolean isDeletedDir = new File(getExternalFilesDir(null),
                Constant.MAIN_DATA_DIR_NAME).delete();
        Debug.d(TAG, "clearData() result: isDeletedDir = "+isDeletedDir+
                ", isDeletedDb = " + isDeletedDb);
        Toast.makeText(this, R.string.clear_success, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(this::exitApp, 1000);
    }

    public void exitApp() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getOpPackageName());
        System.exit(0);
    }
}
