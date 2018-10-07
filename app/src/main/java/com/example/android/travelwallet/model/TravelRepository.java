package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

public class TravelRepository {
    private final TravelDao mTravelDao;
    private final LiveData<List<Travel>> mAllTravels;

    TravelRepository(Application application){
        TravelDatabase db = TravelDatabase.getInstance(application);
        mTravelDao = db.travelDao();
        mAllTravels = mTravelDao.findAll();
    }

    LiveData<List<Travel>> getAllTravels(){
        return mAllTravels;
    }

    public Travel getTravel(long travelID){
        try {
            return new queryTravelAsyncTask(mTravelDao).execute(travelID).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Travel> getCurrentTravels(Date currentDate){
        try {
            return new queryCurrentTravelsAsyncTask(mTravelDao).execute(currentDate).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(Travel travel){
        new insertAsyncTask(mTravelDao).execute(travel);
    }

    public void update(Travel travel){
        new updateAsyncTask(mTravelDao).execute(travel);
    }

    public void delete(Travel travel){
        new deleteAsyncTask(mTravelDao).execute(travel);
    }

    private static class insertAsyncTask extends AsyncTask<Travel, Void, Void>{
        private final TravelDao mAsyncTaskDao;

        insertAsyncTask(TravelDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Travel... travels) {
            mAsyncTaskDao.save(travels[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Travel, Void, Void>{
        private final TravelDao mAsyncTaskDao;

        updateAsyncTask(TravelDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Travel... travels) {
            mAsyncTaskDao.update(travels[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Travel, Void, Void>{
        private final TravelDao mAsyncTaskDao;

        deleteAsyncTask(TravelDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Travel... travels) {
            mAsyncTaskDao.delete(travels[0]);
            return null;
        }
    }

    private static class queryTravelAsyncTask extends AsyncTask<Long, Void, Travel>{
        private final TravelDao mAsyncTaskDao;

        queryTravelAsyncTask(TravelDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Travel doInBackground(Long... longs) {
            return mAsyncTaskDao.getTravel(longs[0]);
        }
    }

    private static class queryCurrentTravelsAsyncTask extends AsyncTask<Date, Void, List<Travel>>{
        private final TravelDao mAsyncTaskDao;

        queryCurrentTravelsAsyncTask(TravelDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Travel> doInBackground(Date... dates) {
            return mAsyncTaskDao.getCurrentTravels(dates[0]);
        }
    }

}
