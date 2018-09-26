package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ExpenseRepository {
    private TravelExpenseDao mExpenseDao;
    private LiveData<List<TravelExpense>> mAllExpenses;

    ExpenseRepository(Application application){
        TravelDatabase db = TravelDatabase.getInstance(application);
        mExpenseDao = db.expenseDao();
        mAllExpenses = mExpenseDao.findAll();
    }

    public LiveData<List<TravelExpense>> getAllExpenses(){
        return mAllExpenses;
    }

    public LiveData<List<TravelExpense>> getTravelExpenses(long travelID){
        try {
            return new queryExpensesAsyncTask(mExpenseDao).execute(travelID).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(TravelExpense expense){
        new ExpenseRepository.insertAsyncTask(mExpenseDao).execute(expense);
    }

    public void update(TravelExpense expense){
        new ExpenseRepository.updateAsyncTask(mExpenseDao).execute(expense);
    }

    public void delete(TravelExpense expense){
        new ExpenseRepository.deleteAsyncTask(mExpenseDao).execute(expense);
    }

    private static class insertAsyncTask extends AsyncTask<TravelExpense, Void, Void> {
        private TravelExpenseDao mAsyncTaskDao;

        insertAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(TravelExpense... travels) {
            mAsyncTaskDao.save(travels[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<TravelExpense, Void, Void>{
        private TravelExpenseDao mAsyncTaskDao;

        updateAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(TravelExpense... expenses) {
            mAsyncTaskDao.update(expenses[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<TravelExpense, Void, Void>{
        private TravelExpenseDao mAsyncTaskDao;

        deleteAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(TravelExpense... expenses) {
            mAsyncTaskDao.delete(expenses[0]);
            return null;
        }
    }

    private static class queryExpensesAsyncTask extends AsyncTask<Long, Void, LiveData<List<TravelExpense>>>{
        private TravelExpenseDao mAsyncTaskDao;

        queryExpensesAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected LiveData<List<TravelExpense>> doInBackground(Long... longs) {
            return mAsyncTaskDao.findAllOfTravel (longs[0]);
        }
    }
}
