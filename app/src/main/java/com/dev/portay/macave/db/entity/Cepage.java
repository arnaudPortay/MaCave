package com.dev.portay.macave.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "cepage_table",
        foreignKeys = {@ForeignKey(
                entity = Wine.class,
                parentColumns = "mId",
                childColumns = "wine_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
        },
        indices = { @Index(value = "wine_id")}
)
public class Cepage
{
    @PrimaryKey(autoGenerate = true)
    public int mId;

    @ColumnInfo(name = "wine_id")
    public int mWineId;

    @ColumnInfo(name = "cepage_name")
    public String mCepageName;

    public Cepage(int mWineId, String mCepageName)
    {
        this.mWineId = mWineId;
        this.mCepageName = mCepageName;
    }
}