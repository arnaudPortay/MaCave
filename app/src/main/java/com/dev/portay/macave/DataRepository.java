package com.dev.portay.macave;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.dev.portay.macave.db.CellarDatabase;
import com.dev.portay.macave.db.dao.WineDao;
import com.dev.portay.macave.db.entity.Wine;

import java.util.List;

/**
 * Note : Abstraction is kinda useless for now but may come in handy if we ever try to get
 *  the database on the net or whatever. it allows multiple back ends...
 */
public class DataRepository
{
    /************** MEMBERS **************/
    private static DataRepository msInstance;
    private WineDao mWineDao;
    private LiveData<List<Wine>> mWines;

    /************** FUNCTIONS **************/
    public static DataRepository getDataRepository(Application pApplication)
    {
        if (msInstance == null)
        {
            synchronized (DataRepository.class)
            {
                if (msInstance == null)
                {
                    msInstance = new DataRepository(pApplication);
                }
            }
        }
        return msInstance;
    }

    public static DataRepository getDataRepository()
    {
        return msInstance;
    }

    private DataRepository(Application pApplication)
    {
        CellarDatabase lDatabase = CellarDatabase.getInstance(pApplication);
        mWineDao = lDatabase.mWineDao();
        mWines = mWineDao.getAllWines();
    }

    // Wrapper
    LiveData<List<Wine>> getWineById(final int pId)
    {
        return mWineDao.getWineById(pId);
    }

    // Wrapper
    LiveData<List<Wine>> getAllWines()
    {
        return mWines;
    }

    LiveData<List<Wine>> getWinesWitBottles()
    {
        return mWineDao.getWinesWithBottles();
    }

    public void insertWine(Wine pWine)
    {
        new insertWineAsyncTask(mWineDao).execute(pWine);
    }

    private static class insertWineAsyncTask extends AsyncTask<Wine, Void, Void>
    {
        private WineDao mAsyncTaskDao;

        insertWineAsyncTask(WineDao pDao)
        {
            mAsyncTaskDao = pDao;
        }

        @Override
        protected Void doInBackground(final Wine... params)
        {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void updateBottleNumber(int pNumber, int pId)
    {
        new updateBottleNumberAsyncTask(mWineDao, pId, pNumber).execute();
    }

    private static class updateBottleNumberAsyncTask extends  AsyncTask<Void, Void, Void>
    {
        private WineDao mAsyncTaskDao;
        private int mId;
        private int mNumber;

        updateBottleNumberAsyncTask(WineDao pDao, int pId, int pNumber)
        {
            mAsyncTaskDao = pDao;
            mId = pId;
            mNumber = pNumber;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            mAsyncTaskDao.updateBottleNumber(mNumber, mId);
            return null;
        }
    }

    public void updateRebuy(boolean pRebuy, int pId)
    {
        new updateRebuyAsyncTask(mWineDao, pId, pRebuy).execute();
    }

    private static class updateRebuyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private WineDao mAsyncTaskDao;
        private int mId;
        private boolean mRebuy;

        updateRebuyAsyncTask(WineDao pDao, int pId, boolean pRebuy)
        {
            mAsyncTaskDao = pDao;
            mId = pId;
            mRebuy = pRebuy;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            mAsyncTaskDao.updateRebuy(mRebuy, mId);
            return null;
        }
    }
}
