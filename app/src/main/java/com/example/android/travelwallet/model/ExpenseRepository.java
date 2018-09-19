package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ExpenseRepository {
    private TravelExpenseDao mExpenseDao;
    private LiveData<List<TravelExpense>> mAllExpenses;
    private LiveData<List<TravelExpense>> mAllExpensesOfTravel;

    ExpenseRepository(Application application, int travelID){
        TravelDatabase db = TravelDatabase.getInstance(application);
        mExpenseDao = db.expenseDao();
        mAllExpenses = mExpenseDao.findAll();
        mAllExpensesOfTravel = mExpenseDao.findAllOfTravel(travelID);
    }

    public LiveData<List<TravelExpense>> getAllExpenses(){
        return mAllExpenses;
    }
    public LiveData<List<TravelExpense>> getmAllExpensesOfTravel(){
        return mAllExpensesOfTravel;
    }

    public void insert(TravelExpense travel){
        new ExpenseRepository.insertAsyncTask(mExpenseDao).execute(travel);
    }

    public void update(TravelExpense travel){
        new ExpenseRepository.updateAsyncTask(mExpenseDao).execute(travel);
    }

    public void delete(TravelExpense travel){
        new ExpenseRepository.deleteAsyncTask(mExpenseDao).execute(travel);
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
}
