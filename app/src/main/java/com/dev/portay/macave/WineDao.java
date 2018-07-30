package com.dev.portay.macave;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WineDao
{

    @Insert
    void insert(Wine pWine);

    @Query("DELETE FROM wine_table")
    void deleteAll();

    @Query("SELECT * FROM wine_table")
    LiveData<List<Wine>> getAllWines();
}
