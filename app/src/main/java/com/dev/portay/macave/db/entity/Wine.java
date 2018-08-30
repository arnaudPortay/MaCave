package com.dev.portay.macave.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

//import java.net.URI;

@Entity(tableName = "wine_table")
public class Wine implements Parcelable // Parcelable allows you to pass the object from activity to activity
{

    /* *************  MEMBERS  ************* */

    @PrimaryKey/*(autoGenerate = true)*/ //TODO Uncomment when not manually populating db
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "origin")
    private String mOrigin; // Either the country or a more specific region eg: Bourgogne

    @ColumnInfo(name = "color")
    private String mColor; // White - Red - Rosé

    @ColumnInfo(name = "producer")
    private String mProducer;

    // TODO: Add the labelUri getters & setters and uncomment import statement
    //@ColumnInfo(name = "labelPath")
    //private URI mLabelPath;

    //TODO: Checkout the @Ignore statement to use with an image ( https://developer.android.com/training/data-storage/room/defining-data )

    /* ************* FUNCTIONS ************* */

    /* * Parcelable interface * */
    public Wine(Parcel pIn)
    {
        mId = pIn.readInt();
        mName = pIn.readString();
        mColor = pIn.readString();
        mProducer = pIn.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeString(mColor);
        parcel.writeString(mProducer);
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
    public Wine(String mName, String mOrigin, String mColor, String mProducer)
    {
        this.mName = mName;
        this.mOrigin = mOrigin;
        this.mColor = mColor;
        this.mProducer = mProducer;
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

    public String getColor()
    {
        return mColor;
    }

    public void setColor(String pColor)
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
}
