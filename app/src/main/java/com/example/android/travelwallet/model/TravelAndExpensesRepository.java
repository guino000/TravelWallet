package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TravelAndExpensesRepository {
    private final TravelAndExpenseDao mTravelAndExpenseDao;
    private final LiveData<List<TravelAndExpenses>> mTravelAndExpenses;

    TravelAndExpensesRepository(Application application) {
        TravelDatabase db = TravelDatabase.getInstance(application);
        mTravelAndExpenseDao = db.travelAndExpenseDao();
        mTravelAndExpenses = mTravelAndExpenseDao.getAllTravelsAndExpenses();
    }

    LiveData<List<TravelAndExpenses>> getAllTravelsAndExpenses() {
        return mTravelAndExpenses;
    }

    LiveData<TravelAndExpenses> getTravelAndExpenses(long mTravelID) {
        try {
            return new QueryTravelAndExpenseAsyncTask(mTravelAndExpenseDao).execute(mTravelID).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class QueryTravelAndExpenseAsyncTask extends AsyncTask<Long, Void, LiveData<TravelAndExpenses>> {
        private final TravelAndExpenseDao mAsyncTaskDao;

        QueryTravelAndExpenseAsyncTask(TravelAndExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected LiveData<TravelAndExpenses> doInBackground(Long... longs) {
            return mAsyncTaskDao.getTravelAndExpenses(longs[0]);
        }
    }
}
