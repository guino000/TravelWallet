package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.math.BigDecimal;
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

    public LiveData<List<TravelExpense>> getExpensesOfDate(String date){
        return mRepository.getExpensesOfDate(date);
    }

    public BigDecimal getTotalExpensesOfDate(String date){
        return mRepository.getTotalExpensesOfDate(date);
    }

    public void insert(TravelExpense expense){mRepository.insert(expense);}

    public void update(TravelExpense expense){mRepository.update(expense);}

    public void delete(TravelExpense expense){mRepository.delete(expense);}
}
