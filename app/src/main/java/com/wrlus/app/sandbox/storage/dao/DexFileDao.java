package com.wrlus.app.sandbox.storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wrlus.app.sandbox.entity.DexFileData;

import java.util.List;

@Dao
public interface DexFileDao {
    @Query("select * from dexfile_data order by id")
    List<DexFileData> getDexFileAll();
    @Query("select * from dexfile_data where uid = :uid order by id")
    List<DexFileData> getDexFileByUid(int uid);
    @Query("select * from dexfile_data where pid = :pid order by id")
    List<DexFileData> getDexFileByPid(int pid);
    @Query("select * from dexfile_data where package_name = :packageName order by id")
    List<DexFileData> getDexFileByPackageName(String packageName);
    @Query("select * from dexfile_data where id = :id")
    List<DexFileData> getDexFileById(int id);
    @Query("select count(id) from dexfile_data")
    long getDexFileCount();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDexFile(DexFileData... data);
    @Update()
    void updateDexFile(DexFileData... data);
    @Delete
    void deleteDexFile(DexFileData... data);
}
