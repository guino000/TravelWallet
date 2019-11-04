package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseRepository {
    private final TravelExpenseDao mExpenseDao;
    private final LiveData<List<TravelExpense>> mAllExpenses;

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

    public TravelValues getTotalExpenses(long travelID){
        try {
            return new queryExpensesTotalAsyncTask(mExpenseDao).execute(travelID).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isOverspent(long travelID) {
        try {
            return new queryIsOverspentAsyncTask(mExpenseDao).execute(travelID).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public LiveData<List<TravelExpense>> getExpensesOfDate(String date){
        try{
            return new queryExpensesOfDateAsyncTask(mExpenseDao).execute(date).get();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public BigDecimal getTotalExpensesOfDate(String date){
        try {
            return new queryTotalExpensesOfDateAsyncTask(mExpenseDao).execute(date).get();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<TravelExpense, Void, Void> {
        private final TravelExpenseDao mAsyncTaskDao;

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
        private final TravelExpenseDao mAsyncTaskDao;

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
        private final TravelExpenseDao mAsyncTaskDao;

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
        private final TravelExpenseDao mAsyncTaskDao;

        queryExpensesAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected LiveData<List<TravelExpense>> doInBackground(Long... longs) {
            return mAsyncTaskDao.findAllOfTravel (longs[0]);
        }
    }

    private static class queryExpensesTotalAsyncTask extends AsyncTask<Long, Void, TravelValues>{
        private final TravelExpenseDao mAsyncTaskDao;

        queryExpensesTotalAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected TravelValues doInBackground(Long... longs) {
            return mAsyncTaskDao.getTotalExpensesOfTravel (longs[0]);
        }
    }

    private static class queryIsOverspentAsyncTask extends AsyncTask<Long, Void, Boolean> {
        private final TravelExpenseDao mAsyncTaskDao;

        queryIsOverspentAsyncTask(TravelExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Boolean doInBackground(Long... longs) {
            return mAsyncTaskDao.isOverspent(longs[0]);
        }
    }

    private static class queryExpensesOfDateAsyncTask extends AsyncTask<String, Void, LiveData<List<TravelExpense>>>{
        private final TravelExpenseDao mAsyncTaskDao;

        queryExpensesOfDateAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected LiveData<List<TravelExpense>> doInBackground(String... strings) {
            return mAsyncTaskDao.getExpensesOfDate(strings[0]);
        }
    }

    private static class queryTotalExpensesOfDateAsyncTask extends AsyncTask<String, Void, BigDecimal>{
        private final TravelExpenseDao mAsyncTaskDao;

        queryTotalExpensesOfDateAsyncTask(TravelExpenseDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected BigDecimal doInBackground(String... strings) {
            return mAsyncTaskDao.getTotalExpensesOfDate(strings[0]);
        }
    }
}
