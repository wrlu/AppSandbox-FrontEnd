package com.wrlus.app.sandbox.storage.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.wrlus.app.sandbox.entity.BinderData;
import com.wrlus.app.sandbox.entity.DexFileData;
import com.wrlus.app.sandbox.storage.dao.BinderDao;
import com.wrlus.app.sandbox.storage.dao.DexFileDao;

@Database(entities = {
        BinderData.class,
        DexFileData.class
}, version = 1, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {
    public abstract BinderDao binderDao();
    public abstract DexFileDao dexFileDao();
}