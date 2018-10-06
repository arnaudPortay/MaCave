package com.dev.portay.macave;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.dev.portay.macave.db.entity.Cepage;
import com.dev.portay.macave.db.entity.Wine;

import java.util.List;

public class WineViewModel extends AndroidViewModel
{
    /* ************* MEMBERS ************* */

    private DataRepository mRepository;
    private LiveData<List<Wine>> mWines;

    /* ************* FUNCTIONS ************* */
    public WineViewModel(Application pApplication)
    {
        super(pApplication);
        mRepository = DataRepository.getDataRepository(pApplication);
        mWines = mRepository.getAllWines();
    }

    LiveData<List<Wine>> getAllWines()
    {
        return mWines;
    }

    LiveData<List<Wine>> getWineById(int pId)
    {
        return mRepository.getWineById(pId);
    }

    LiveData<List<Wine>> getWinesWithBottles()
    {
        return mRepository.getWinesWitBottles();
    }

    LiveData<List<Wine>> getWinesToBuy()
    {
        return mRepository.getWinesToBuy();
    }

    public void insert(Wine pWine)
    {
        mRepository.insertWine(pWine);
    }


    public void insert(Wine pWine, List<Cepage> pCepageList)
    {
        mRepository.insertWine(pWine, pCepageList);
    }
}
