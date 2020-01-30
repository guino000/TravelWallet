package com.example.android.travelwallet.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

@Dao
public interface TravelAndExpenseDao {
    @Query("SELECT * FROM Travel") @Transaction
    LiveData<List<TravelAndExpenses>> getAllTravelsAndExpenses();

    @Query("SELECT * FROM Travel WHERE mId = :travelID") @Transaction
    LiveData<TravelAndExpenses> getTravelAndExpenses(long travelID);
}
