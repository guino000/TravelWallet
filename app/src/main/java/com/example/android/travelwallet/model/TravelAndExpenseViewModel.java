package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class TravelAndExpenseViewModel extends AndroidViewModel {
    private final TravelAndExpensesRepository mRepository;
    private final LiveData<List<TravelAndExpenses>> mTravelAndExpenses;

    public TravelAndExpenseViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TravelAndExpensesRepository(application);
        mTravelAndExpenses = mRepository.getAllTravelsAndExpenses();
    }

    public LiveData<List<TravelAndExpenses>> getAllTravelsAndExpenses() {
        return mTravelAndExpenses;
    }

    public LiveData<TravelAndExpenses> getTravelAndExpenses(long mTravelID) {
        return mRepository.getTravelAndExpenses(mTravelID);
    }
}
