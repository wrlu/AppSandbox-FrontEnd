package com.wrlus.app.sandbox.storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wrlus.app.sandbox.entity.HttpData;

import java.util.List;

@Dao
public interface HttpDao {
    @Query("select * from http_data order by id")
    List<HttpData> getHttpDataAll();
    @Query("select * from http_data where uid = :uid order by id")
    List<HttpData> getHttpDataByUid(int uid);
    @Query("select * from http_data where pid = :pid order by id")
    List<HttpData> getHttpDataByPid(int pid);
    @Query("select * from http_data where package_name = :packageName order by id")
    List<HttpData> getHttpDataByPackageName(String packageName);
    @Query("select * from http_data where id = :id")
    List<HttpData> getHttpDataById(int id);
    @Query("select count(id) from http_data")
    long getHttpDataCount();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHttpData(HttpData... data);
    @Update()
    void updateHttpData(HttpData... data);
    @Delete
    void deleteHttpData(HttpData... data);
}
