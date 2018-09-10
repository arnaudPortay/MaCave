package com.dev.portay.macave.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.dev.portay.macave.db.dao.WineDao;
import com.dev.portay.macave.db.entity.Wine;


@Database(entities = {Wine.class}, version = 1)
public abstract class CellarDatabase extends RoomDatabase
{

    /************** MEMBERS **************/
    public abstract WineDao mWineDao();

    private static CellarDatabase smInstance;


    /************** FUNCTIONS **************/
    public static CellarDatabase getInstance(final Context pContext)
    {
        if (smInstance == null) {
            synchronized (CellarDatabase.class)
            {
                if (smInstance == null)
                {
                    smInstance = Room.databaseBuilder(pContext.getApplicationContext(),
                            CellarDatabase.class, "cellar_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sCellarDatabaseCallback)
                            .build(); // TODO: Delete callback later and handle migration
                }
            }
        }
        return smInstance;
    }

    public static  CellarDatabase getInstance()
    {
        return smInstance;
    }

    // TODO: Delete later
    private  static  CellarDatabase.Callback sCellarDatabaseCallback =
            new CellarDatabase.Callback()
            {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase pDb)
                {
                    super.onOpen(pDb);
                    new PopulateDBAsync(smInstance).execute();
                }
            };

    // TODO: Delete later
    private static class PopulateDBAsync extends AsyncTask<Void, Void, Void>
    {
        private final WineDao mWineDao;

        PopulateDBAsync(CellarDatabase pDb)
        {
            mWineDao = pDb.mWineDao();
        }

        @Override
        protected Void doInBackground(final Void... pParams)
        {
            mWineDao.deleteAll();

            Wine lW1 = new Wine("toto", "bourgogne", Wine.WineColor.eRed,"titi",1992,7);
            lW1.setId(0);

            mWineDao.insert(lW1);
            Wine lW2 = new Wine("pore", "champange", Wine.WineColor.eWhite,"ta mère", 1955,4);
            lW2.setId(1);

            mWineDao.insert(lW2);
            Wine lW3 = new Wine("aae", "bordeaux", Wine.WineColor.eChampagne,"lalili", 1996,6);
            lW3.setId(2);
            mWineDao.insert(lW3);

            return null;
        }
    }
}
