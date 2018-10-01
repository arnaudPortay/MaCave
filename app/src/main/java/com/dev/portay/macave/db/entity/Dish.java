package com.dev.portay.macave.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "dish_table",
        foreignKeys = {@ForeignKey(
                entity = Wine.class,
                parentColumns = "mId",
                childColumns = "wine_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
        },
        indices = { @Index(value = "wine_id")}
        )
public class Dish
{
    @PrimaryKey(autoGenerate = true)
    public int mId;

    @ColumnInfo(name = "wine_id")
    public int mWineId;

    @ColumnInfo(name = "dish_name")
    public String mDishName;

    public Dish(int mWineId, String mDishName)
    {
        this.mWineId = mWineId;
        this.mDishName = mDishName;
    }
}
