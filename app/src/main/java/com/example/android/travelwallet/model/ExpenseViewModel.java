package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private ExpenseRepository mRepository;
    private LiveData<List<TravelExpense>> mAllExpenses;
    private LiveData<List<TravelExpense>> mAllExpensesOfTravel;

    public ExpenseViewModel(@NonNull Application application, int travelID) {
        super(application);
        mRepository = new ExpenseRepository(application, travelID);
        mAllExpenses = mRepository.getAllExpenses();
        mAllExpensesOfTravel = mRepository.getmAllExpensesOfTravel();
    }

    public LiveData<List<TravelExpense>> getAllExpenses(){return mAllExpenses;}
    public LiveData<List<TravelExpense>> getAllExpensesOfTravel(){return mAllExpensesOfTravel;}

    public void insert(TravelExpense expense){mRepository.insert(expense);}

    public void update(TravelExpense expense){mRepository.update(expense);}

    public void delete(TravelExpense expense){mRepository.delete(expense);}
}
