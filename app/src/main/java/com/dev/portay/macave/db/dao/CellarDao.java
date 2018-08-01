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

    //@Query("SELECT * FROM cellar_table, wine_table WHERE cellar_table.wine_id=wine_table.mId")
    // TODO: Check the above query validity and replace the query below with it
    @Query("SELECT * FROM cellar_table")
    LiveData<List<CellarItem>> getCellarBottles();
}
