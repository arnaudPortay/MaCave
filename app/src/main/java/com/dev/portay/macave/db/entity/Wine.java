package com.dev.portay.macave.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dev.portay.macave.R;

//import java.net.URI;

@Entity(tableName = "wine_table")
public class Wine implements Parcelable // Parcelable allows you to pass the object from activity to activity
{

    /* *************  MEMBERS  ************* */

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "origin")
    private String mOrigin; // Either the country or a more specific region eg: Bourgogne

    @ColumnInfo(name = "color")
    @TypeConverters(WineColorConverter.class)
    private WineColor mColor;

    @ColumnInfo(name = "producer")
    private String mProducer;

    @ColumnInfo(name = "year")
    private int mYear;

    @ColumnInfo(name = "bottle_number")
    private int mBottleNumber;

    @ColumnInfo(name = "rebuy")
    private boolean mRebuy;



    // TODO: Add the labelUri getters & setters and uncomment import statement
    //@ColumnInfo(name = "labelPath")
    //private URI mLabelPath;
    //TODO: Checkout the @Ignore statement to use with an image ( https://developer.android.com/training/data-storage/room/defining-data )


    // Enum for wine color
    public enum WineColor
    {
        eRed(0),
        eWhite(1),
        eRose(2),
        ePaille(3),
        eSparkling(4),
        eCremant(5),
        eChampagne(6),
        eChampagneRose(7);

        private int mCode;

        WineColor(int pCode)
        {
            this.mCode = pCode;
        }

        public int getCode()
        {
            return  mCode;
        }
    }

    /* ************* FUNCTIONS ************* */

    /* * Parcelable interface * */
    public Wine(Parcel pIn)
    {
        mId = pIn.readInt();
        mName = pIn.readString();
        mColor = (WineColor)pIn.readSerializable();
        mProducer = pIn.readString();
        mYear = pIn.readInt();
        mBottleNumber = pIn.readInt();
        mOrigin = pIn.readString();
        mRebuy = pIn.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeSerializable(mColor);
        parcel.writeString(mProducer);
        parcel.writeInt(mYear);
        parcel.writeInt(mBottleNumber);
        parcel.writeString(mOrigin);
        parcel.writeInt(mRebuy ? 1 : 0);
    }

    public static final Parcelable.Creator<Wine> CREATOR = new Parcelable.Creator<Wine>()
    {
        public Wine createFromParcel(Parcel pIn)
        {
            return new Wine(pIn);
        }

        public Wine[] newArray(int pSize)
        {
            return new Wine[pSize];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    /* * Constructor * */
    public Wine(String mName, String mOrigin, WineColor mColor, String mProducer, int mYear, int mBottleNumber)
    {
        this.mName = mName;
        this.mOrigin = mOrigin;
        this.mColor = mColor;
        this.mProducer = mProducer;
        this.mYear = mYear;
        this.mBottleNumber = mBottleNumber;
        this.mRebuy = false; // By default you're adding the wine so you have some and don't need to rebuy right away
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

    public String getName()
    {
        return mName;
    }

    public void setName(String pName)
    {
        this.mName = pName;
    }

    public String getOrigin()
    {
        return mOrigin;
    }

    public void setOrigin(String pOrigin)
    {
        this.mOrigin = pOrigin;
    }

    public WineColor getColor()
    {
        return mColor;
    }

    public void setColor(WineColor pColor)
    {
        this.mColor = pColor;
    }

    public String getProducer()
    {
        return mProducer;
    }

    public void setProducer(String pProducer)
    {
        this.mProducer = pProducer;
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

    public boolean getRebuy()
    {
        return mRebuy;
    }

    public void setRebuy(boolean pRebuy)
    {
        this.mRebuy = pRebuy;
    }

    public static int getStringIdFromColor(WineColor pColor)
    {
        switch (pColor)
        {
            case eRed:
                return R.string.wine_red;
            case eWhite:
                return R.string.wine_white;
            case eRose:
                return R.string.wine_rose;
            case ePaille:
                return R.string.wine_paille;
            case eSparkling:
                return R.string.wine_sparkling;
            case eCremant:
                return R.string.wine_cremant;
            case eChampagne:
                return R.string.wine_champagne;
            case eChampagneRose:
                return R.string.wine_champagne_rose;
            default:
                Log.e("MACAVE", "Wrong number for Wine Color enum");
                return R.string.error;
        }
    }
}