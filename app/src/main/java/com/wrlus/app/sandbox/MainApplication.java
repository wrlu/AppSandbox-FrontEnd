package com.wrlus.app.sandbox;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.room.Room;

import com.wrlus.app.sandbox.preference.Debug;
import com.wrlus.app.sandbox.service.BinderHookService;
import com.wrlus.app.sandbox.service.DexHookService;
import com.wrlus.app.sandbox.storage.db.MainDatabase;
import com.wrlus.app.sandbox.utils.Constant;

import java.io.File;

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
        boolean isDeletedDir = new File(getExternalFilesDir(null),
                Constant.MAIN_DATA_DIR_NAME).delete();
        boolean isDeletedDb = dbFile.delete();
        Debug.d(TAG, "clearData() result: isDeletedDir = "+isDeletedDir+
                ", isDeletedDb="+isDeletedDb);
        Toast.makeText(this, R.string.clear_success, Toast.LENGTH_SHORT).show();
    }
}
