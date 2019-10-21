package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

public class TravelAndExpenseViewModel extends AndroidViewModel {
    private final TravelAndExpensesRepository mRepository;

    public TravelAndExpenseViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TravelAndExpensesRepository(application);
    }

    public LiveData<TravelAndExpenses> getTravelAndExpenses(long mTravelID) {
        return mRepository.getTravelAndExpenses(mTravelID);
    }
}
