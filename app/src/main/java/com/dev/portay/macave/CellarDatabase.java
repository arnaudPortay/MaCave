package com.dev.portay.macave;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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
        private final WineDao mDao;
        PopulateDBAsync(CellarDatabase pDb)
        {
            mDao = pDb.mWineDao();
        }

        @Override
        protected Void doInBackground(final Void... pParams)
        {
            mDao.deleteAll();
            Wine lW1 = new Wine("toto", "bourgogne", "red","titi");
            mDao.insert(lW1);
            Wine lW2 = new Wine("pore", "champange", "blue","ta mère");
            mDao.insert(lW2);
            Wine lW3 = new Wine("aae", "bordeaux", "white","lalili");
            mDao.insert(lW3);
            return null;
        }
    }
}
