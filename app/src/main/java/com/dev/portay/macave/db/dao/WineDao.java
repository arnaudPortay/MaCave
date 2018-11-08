package com.dev.portay.macave.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.portay.macave.db.entity.Wine;

import java.util.List;

@Dao
public interface WineDao
{

    @Insert
    long insert(Wine pWine);

    @Query("DELETE FROM wine_table")
    void deleteAll();

    @Delete
    void deleteWine(Wine pWine);

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

    @Query("SELECT name FROM wine_table")
    LiveData<List<String>> getAllWineNames();

    @Query("SELECT producer FROM wine_table")
    LiveData<List<String>> getAllWineProducers();

    @Query("SELECT origin FROM wine_table")
    LiveData<List<String>> getAllWineOrigins();

    @Query("SELECT * FROM wine_table WHERE consumption_date <= :pConsumptionDate AND bottle_number != 0")
    LiveData<List<Wine>> getWinesToDrink(int pConsumptionDate);
}
