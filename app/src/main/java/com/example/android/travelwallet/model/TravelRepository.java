package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TravelRepository {
    private TravelDao mTravelDao;
    private LiveData<List<Travel>> mAllTravels;

    TravelRepository(Application application){
        TravelDatabase db = TravelDatabase.getInstance(application);
        mTravelDao = db.travelDao();
        mAllTravels = mTravelDao.findAll();
    }

    LiveData<List<Travel>> getAllTravels(){
        return mAllTravels;
    }

    public void insert(Travel travel){
        new insertAsyncTask(mTravelDao).execute(travel);
    }

    private static class insertAsyncTask extends AsyncTask<Travel, Void, Void>{
        private TravelDao mAsyncTaskDao;

        insertAsyncTask(TravelDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Travel... travels) {
            mAsyncTaskDao.save(travels[0]);
            return null;
        }
    }
}
