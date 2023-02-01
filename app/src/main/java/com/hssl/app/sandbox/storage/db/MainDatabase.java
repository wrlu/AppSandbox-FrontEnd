package com.hssl.app.sandbox.storage.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.hssl.app.sandbox.entity.BinderData;
import com.hssl.app.sandbox.entity.DexFileData;
import com.hssl.app.sandbox.entity.HttpData;
import com.hssl.app.sandbox.entity.IntentData;
import com.hssl.app.sandbox.storage.dao.BinderDao;
import com.hssl.app.sandbox.storage.dao.HttpDao;
import com.hssl.app.sandbox.storage.dao.DexFileDao;
import com.hssl.app.sandbox.storage.dao.IntentDao;

@Database(entities = {
        BinderData.class,
        DexFileData.class,
        HttpData.class,
        IntentData.class
}, version = 1, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {
    public abstract BinderDao binderDao();
    public abstract DexFileDao dexFileDao();
    public abstract HttpDao httpDao();
    public abstract IntentDao intentDao();
}