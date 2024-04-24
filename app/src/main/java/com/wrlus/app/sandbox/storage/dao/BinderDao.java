package com.wrlus.app.sandbox.storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wrlus.app.sandbox.entity.BinderData;

import java.util.List;

@Dao
public interface BinderDao {
    @Query("select * from binder_data order by id")
    List<BinderData> getBinderDataAll();
    @Query("select * from binder_data where uid = :uid order by id")
    List<BinderData> getBinderDataByUid(int uid);
    @Query("select * from binder_data where pid = :pid order by id")
    List<BinderData> getBinderDataByPid(int pid);
    @Query("select * from binder_data where interface_token = :interfaceToken order by id")
    List<BinderData> getBinderDataByInterfaceToken(String interfaceToken);
    @Query("select * from binder_data where id = :id")
    List<BinderData> getBinderDataById(int id);
    @Query("select count(id) from binder_data")
    long getBinderDataCount();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBinderData(BinderData... data);
    @Update()
    void updateBinderData(BinderData... data);
    @Delete
    void deleteBinderData(BinderData... data);
}
