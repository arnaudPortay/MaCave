package com.dev.portay.macave.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "cellar_table",
        foreignKeys =
                { @ForeignKey(entity = Wine.class,
                                    parentColumns = "mId",
                                    childColumns = "wine_id",
                                    onUpdate = ForeignKey.CASCADE,
                                    onDelete = ForeignKey.CASCADE)
                },
        indices = { @Index(value = "wine_id")}
        )
public class CellarItem implements Parcelable
{
    /* *************  MEMBERS  ************* */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo( name = "id")
    private int mId;

    @ColumnInfo(name = "year")
    private int mYear;

    @ColumnInfo(name = "wine_id")
    private int mWineId;

    @ColumnInfo(name = "bottle_number")
    private int mBottleNumber;

    /* ************* FUNCTIONS ************* */

    /* * Parcelable Interface * */
    public CellarItem(Parcel pIn)
    {
        mId = pIn.readInt();
        mYear = pIn.readInt();
        mWineId = pIn.readInt();
        mBottleNumber = pIn.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mId);
        parcel.writeInt(mYear);
        parcel.writeInt(mWineId);
        parcel.writeInt(mBottleNumber);
    }

    public static final Parcelable.Creator<CellarItem> CREATOR = new Parcelable.Creator<CellarItem>()
    {
        public CellarItem createFromParcel(Parcel pIn)
        {
            return new CellarItem(pIn);
        }

        public CellarItem[] newArray(int pSize)
        {
            return new CellarItem[pSize];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    /* * Constructor * */
    public CellarItem(int mWineId, int mYear, int mBottleNumber)
    {
        this.mWineId = mWineId;
        this.mYear = mYear;
        this.mBottleNumber = mBottleNumber;
    }

    /* * Getters & Setters * */
    public int getId()
    {
        return mId;
    }

    public void setId(int pId)
    {
        this.mId = pId;
    }

    public int getWineId()
    {
        return mWineId;
    }

    public void setWineId(int pWineId)
    {
        this.mWineId = pWineId;
    }

    public int getYear()
    {
        return mYear;
    }

    public void setYear(int pYear)
    {
        this.mYear = pYear;
    }

    public int getBottleNumber()
    {
        return mBottleNumber;
    }

    public void setBottleNumber(int pBottleNumber)
    {
        this.mBottleNumber = pBottleNumber;
    }
}
