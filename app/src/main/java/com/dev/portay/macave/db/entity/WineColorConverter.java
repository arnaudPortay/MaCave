package com.dev.portay.macave.db.entity;

import android.arch.persistence.room.TypeConverter;

/* ******* TYPE CONVERTER ********** */
public class WineColorConverter
{

    @TypeConverter
    public static Wine.WineColor toWineColor(int pColor)
    {
        if (pColor == Wine.WineColor.eRed.getCode())
        {
            return Wine.WineColor.eRed;
        }
        else if (pColor == Wine.WineColor.eWhite.getCode())
        {
            return Wine.WineColor.eWhite;
        }
        else if (pColor == Wine.WineColor.eRose.getCode())
        {
            return Wine.WineColor.eRose;
        }
        else if (pColor == Wine.WineColor.ePaille.getCode())
        {
            return Wine.WineColor.ePaille;
        }
        else if (pColor == Wine.WineColor.eSparkling.getCode())
        {
            return Wine.WineColor.eSparkling;
        }
        else if (pColor == Wine.WineColor.eCremant.getCode())
        {
            return Wine.WineColor.eCremant;
        }
        else if (pColor == Wine.WineColor.eChampagne.getCode())
        {
            return Wine.WineColor.eChampagne;
        }
        else if (pColor == Wine.WineColor.eChampagneRose.getCode())
        {
            return Wine.WineColor.eChampagneRose;
        }
        else
        {
            throw new IllegalArgumentException("Could not recognize wine color");
        }
    }

    @TypeConverter
    public static int toInt(Wine.WineColor pWineColor) {
        return pWineColor.getCode();
    }
}
