package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseRepository mRepository;
    private final LiveData<List<TravelExpense>> mAllExpenses;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ExpenseRepository(application);
        mAllExpenses = mRepository.getAllExpenses();
    }

    public LiveData<List<TravelExpense>> getAllExpenses(){return mAllExpenses;}
    public LiveData<List<TravelExpense>> getAllExpensesOfTravel(long travelID){return mRepository.getTravelExpenses(travelID);}

    public TravelValues getTotalExpensesOfTravel(long travelID){return mRepository.getTotalExpenses(travelID);}

    public Boolean isOverspent(long travelID) {
        return mRepository.isOverspent(travelID);
    }

    public LiveData<List<Date>> getAllDates(long travelID){
        return mRepository.getAllDates(travelID);
    }

    public LiveData<List<TravelExpense>> getExpensesOfDate(Date date, long travelID){
        return mRepository.getExpensesOfDate(date, travelID);
    }

    public BigDecimal getTotalExpensesOfDate(Date date, long travelID){
        return mRepository.getTotalExpensesOfDate(date, travelID);
    }

    public void insert(TravelExpense expense){mRepository.insert(expense);}

    public void update(TravelExpense expense){mRepository.update(expense);}

    public void delete(TravelExpense expense){mRepository.delete(expense);}
}
