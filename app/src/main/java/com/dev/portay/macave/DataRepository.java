package com.dev.portay.macave;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.dev.portay.macave.db.CellarDatabase;
import com.dev.portay.macave.db.dao.CellarDao;
import com.dev.portay.macave.db.dao.WineDao;
import com.dev.portay.macave.db.entity.CellarItem;
import com.dev.portay.macave.db.entity.Wine;

import java.util.List;

/**
 * Note : Abstraction is kinda useless for now but may come in handy if we ever try to get
 *  the database on the net or whatever. it allows multiple back ends...
 */
public class DataRepository
{
    /************** MEMBERS **************/
    // TODO: Check if the wine related members are still usefull and delete them otherwise
    private static DataRepository msInstance;
    private WineDao mWineDao;
    private CellarDao mCellarDao;
    private LiveData<List<Wine>> mWines;
    private LiveData<List<CellarItem>> mCellarItems;

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

        mCellarDao = lDatabase.mCellarDao();
        mCellarItems = mCellarDao.getCellarBottles();
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

    //Wrapper
    LiveData<List<CellarItem>> getCellarItemById(final int pId)
    {
        return mCellarDao.getItemById(pId);
    }

    //Wrapper
    LiveData<List<CellarItem>> getCellarBottles()
    {
        return mCellarItems;
    }

    public void insertCellarItem(CellarItem pCellarItem)
    {
        new insertCellarItemAsyncTask(mCellarDao).execute(pCellarItem);
    }

    private static class insertCellarItemAsyncTask extends AsyncTask<CellarItem,Void,Void>
    {
        private CellarDao mAsyncTaskDao;
        insertCellarItemAsyncTask(CellarDao pDao)
        {
            mAsyncTaskDao = pDao;
        }

        @Override
        protected Void doInBackground(final CellarItem... cellarItems)
        {
            mAsyncTaskDao.insert(cellarItems[0]);
            return null;
        }
    }

    public void updateBottleNumber(int pNumber, int pId)
    {
        new updateBottleNumberAsyncTask(mCellarDao, pId, pNumber).execute();
    }

    private static class updateBottleNumberAsyncTask extends  AsyncTask<Void, Void, Void>
    {
        private CellarDao mAsyncTaskDao;
        private int mId;
        private int mNumber;

        updateBottleNumberAsyncTask(CellarDao pDao, int pId, int pNumber)
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
}
