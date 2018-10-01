package com.dev.portay.macave.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dev.portay.macave.db.entity.Dish;

import java.util.List;

@Dao
public interface DishDao
{
    @Insert
    void insert(Dish pDish);

    @Query("SELECT * FROM dish_table WHERE wine_id=:pWineId")
    LiveData<List<Dish>> getDishesByWineId(int pWineId);

    @Delete
    void deleteDish(Dish pDish);
}
