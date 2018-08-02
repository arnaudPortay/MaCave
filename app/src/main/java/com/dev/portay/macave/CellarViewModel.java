package com.dev.portay.macave;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.dev.portay.macave.db.entity.CellarItem;

import java.util.List;

public class CellarViewModel extends AndroidViewModel
{
    /* ************* MEMBERS ************* */

    private DataRepository mRepository;
    private LiveData<List<CellarItem>> mCellarItems;

    /* ************* FUNCTIONS ************* */
    public CellarViewModel(Application pApplication)
    {
        super(pApplication);
        mRepository = DataRepository.getDataRepository(pApplication);
        mCellarItems = mRepository.getCellarBottles();
    }

    LiveData<List<CellarItem>> getAllCellarItems()
    {
        return mCellarItems;
    }

    LiveData<List<CellarItem>> getCellarItemById(int pId)
    {
        return mRepository.getCellarItemById(pId);
    }

    public void insert(CellarItem pCellarItem)
    {
        mRepository.insertCellarItem(pCellarItem);
    }
}
