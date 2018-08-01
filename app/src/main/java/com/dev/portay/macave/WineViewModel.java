package com.dev.portay.macave;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

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

    public void insert(Wine pWine)
    {
        mRepository.insertWine(pWine);
    }


}
