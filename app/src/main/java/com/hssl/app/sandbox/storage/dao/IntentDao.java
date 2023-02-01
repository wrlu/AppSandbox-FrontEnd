package com.hssl.app.sandbox.storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hssl.app.sandbox.entity.IntentData;

import java.util.List;

@Dao
public interface IntentDao {
    @Query("select * from intent_data order by id")
    List<IntentData> getIntentDataAll();
    @Query("select * from intent_data where uid = :uid order by id")
    List<IntentData> getIntentDataByUid(int uid);
    @Query("select * from intent_data where pid = :pid order by id")
    List<IntentData> getIntentDataByPid(int pid);
    @Query("select * from intent_data where id = :id")
    List<IntentData> getIntentDataById(int id);
    @Query("select * from intent_data where operation = :operation")
    List<IntentData> getIntentDataByOperation(int operation);
    @Query("select count(id) from intent_data")
    long getIntentDataCount();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIntentData(IntentData... data);
    @Update()
    void updateIntentData(IntentData... data);
    @Delete
    void deleteIntentData(IntentData... data);
}
