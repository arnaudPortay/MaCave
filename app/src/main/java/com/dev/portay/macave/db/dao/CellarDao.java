package com.dev.portay.macave.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.portay.macave.db.entity.CellarItem;

import java.util.List;

@Dao
public interface CellarDao
{
    @Insert
    void insert(CellarItem pBottle);

    @Query("DELETE FROM cellar_table")
    void deleteAll();

    @Query("SELECT * FROM cellar_table")
    LiveData<List<CellarItem>> getCellarBottles();

    @Query("SELECT * FROM cellar_table WHERE id=:pId")
    LiveData<List<CellarItem>> getItemById(int pId);

    @Query("UPDATE cellar_table SET bottle_number = :pNumber WHERE id = :pId")
    void updateBottleNumber(int pNumber, int pId);
}
