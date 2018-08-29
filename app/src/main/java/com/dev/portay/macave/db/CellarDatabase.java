package com.dev.portay.macave.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.dev.portay.macave.db.dao.CellarDao;
import com.dev.portay.macave.db.dao.WineDao;
import com.dev.portay.macave.db.entity.CellarItem;
import com.dev.portay.macave.db.entity.Wine;

import java.util.ArrayList;

@Database(entities = {Wine.class, CellarItem.class}, version = 1)
public abstract class CellarDatabase extends RoomDatabase
{

    /************** MEMBERS **************/
    public abstract WineDao mWineDao();
    public abstract CellarDao mCellarDao();

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
    private static class PopulateDBAsync extends AsyncTask<Void, Void, ArrayList<Integer>>
    {
        private final WineDao mWineDao;
        private final CellarDao mCellarDao;

        PopulateDBAsync(CellarDatabase pDb)
        {
            mWineDao = pDb.mWineDao();
            mCellarDao = pDb.mCellarDao();
        }

        @Override
        protected ArrayList<Integer> doInBackground(final Void... pParams)
        {
            mWineDao.deleteAll();
            mCellarDao.deleteAll();

            Wine lW1 = new Wine("toto", "bourgogne", "red","titi");
            lW1.setId(0);
            mWineDao.insert(lW1);
            Wine lW2 = new Wine("pore", "champange", "blue","ta mère");
            lW2.setId(1);
            mWineDao.insert(lW2);
            Wine lW3 = new Wine("aae", "bordeaux", "white","lalili");
            lW3.setId(2);
            mWineDao.insert(lW3);

            ArrayList<Integer> lList = new ArrayList<>(3);
            lList.add(0);
            lList.add(1);
            lList.add(2);

            return lList;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> pResult)
        {
            new PopulateDBAsyncNext(mCellarDao).execute(pResult);
        }
    }

    // TODO: Delete later
    private static class PopulateDBAsyncNext extends AsyncTask<ArrayList<Integer>, Void, Void>
    {
        private CellarDao mCellarDao;

        PopulateDBAsyncNext(CellarDao pCellarDao)
        {
            this.mCellarDao = pCellarDao;
        }

        @Override
        protected Void doInBackground(ArrayList<Integer>... pParam)
        {
            CellarItem lC1 = new CellarItem(pParam[0].get(0), 1990, 3);
            mCellarDao.insert(lC1);

            CellarItem lC2 = new CellarItem(pParam[0].get(1), 888, 1);
            mCellarDao.insert(lC2);
            CellarItem lC3 = new CellarItem(pParam[0].get(2), 1992, 6);
            mCellarDao.insert(lC3);
            return null;
        }
    }
}
