package com.dev.portay.macave.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.portay.macave.db.entity.Wine;

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

    @Query("SELECT * FROM wine_table WHERE mId=:pId")
    LiveData<List<Wine>> getWineById(int pId);

    @Query("UPDATE wine_table SET bottle_number = :pNumber WHERE mId = :pId")
    void updateBottleNumber(int pNumber, int pId);

    @Query("SELECT * FROM wine_table WHERE bottle_number != 0")
    LiveData<List<Wine>> getWinesWithBottles();

    @Query("UPDATE wine_table SET rebuy = :pRebuy WHERE mId = :pId")
    void updateRebuy(boolean pRebuy, int pId);

    @Query("SELECT * FROM wine_table WHERE rebuy = 1")
    LiveData<List<Wine>> getWinesToBuy();
}
