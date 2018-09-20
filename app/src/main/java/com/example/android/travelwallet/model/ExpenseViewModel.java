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

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ExpenseRepository(application);
        mAllExpenses = mRepository.getAllExpenses();
    }

    public LiveData<List<TravelExpense>> getAllExpenses(){return mAllExpenses;}
    public List<TravelExpense> getAllExpensesOfTravel(Travel travel){return mRepository.getTravelExpenses(travel);}

    public void insert(TravelExpense expense){mRepository.insert(expense);}

    public void update(TravelExpense expense){mRepository.update(expense);}

    public void delete(TravelExpense expense){mRepository.delete(expense);}
}
