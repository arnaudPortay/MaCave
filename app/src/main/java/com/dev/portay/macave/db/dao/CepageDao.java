package com.dev.portay.macave.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.portay.macave.db.entity.Cepage;

import java.util.List;

@Dao
public interface CepageDao
{
    @Insert
    void insert(Cepage pCepage);

    @Query("SELECT * FROM cepage_table WHERE wine_id = :pId")
    LiveData<List<Cepage>> getCepageByWineId(int pId);

    @Query("SELECT cepage_name FROM cepage_table")
    LiveData<List<String>> getAllCepageNames();

    @Delete
    void deleteCepage(Cepage pCepage);
}
