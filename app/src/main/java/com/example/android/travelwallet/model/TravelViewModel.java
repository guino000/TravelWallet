package com.example.android.travelwallet.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class TravelViewModel extends AndroidViewModel {
    private TravelRepository mRepository;
    private LiveData<List<Travel>> mAllTravels;

    public TravelViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TravelRepository(application);
        mAllTravels = mRepository.getAllTravels();
    }

    LiveData<List<Travel>> getAllTravels(){return mAllTravels;}

    public void insert(Travel travel){mRepository.insert(travel);}
}
