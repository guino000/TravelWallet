package com.example.android.travelwallet.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TravelAndExpenseDao {
    @Query("SELECT * FROM Travel")
    LiveData<List<TravelAndExpenses>> getAllTravelsAndExpenses();

    @Query("SELECT * FROM Travel WHERE mId = :travelID")
    LiveData<TravelAndExpenses> getTravelAndExpenses(long travelID);
}
