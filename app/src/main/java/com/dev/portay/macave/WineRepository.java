package com.dev.portay.macave;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Note : Abstraction is kinda useless for now but may come in handy if we ever try to get
 *  the database on the net or whatever. it allows multiple back ends
 */
public class WineRepository
{
    /************** MEMBERS **************/
    private WineDao mWineDao;
    private LiveData<List<Wine>> mWines;

    /************** FUNCTIONS **************/
    public WineRepository(Application pApplication)
    {
        CellarDatabase lDatabase = CellarDatabase.getInstance(pApplication);
        mWineDao = lDatabase.mWineDao();
        mWines = mWineDao.getAllWines();
    }

    // Wrapper
    LiveData<List<Wine>> getAllWines()
    {
        return mWines;
    }

    public void insert(Wine pWine)
    {
        new insertAsyncTask(mWineDao).execute(pWine);
    }

    private static class insertAsyncTask extends AsyncTask<Wine, Void, Void>
    {
        private WineDao mAsyncTaskDao;

        insertAsyncTask(WineDao pDao)
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
}
