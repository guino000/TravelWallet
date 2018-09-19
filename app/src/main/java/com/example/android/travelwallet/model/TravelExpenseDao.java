package com.example.android.travelwallet.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

public interface TravelExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<TravelExpense> expenses);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(TravelExpense expense);

    @Update
    void update(TravelExpense expense);

    @Delete
    void delete(TravelExpense expense);

    @Query("SELECT * FROM TravelExpense")
    LiveData<List<TravelExpense>> findAll();

    @Query("SELECT * FROM TravelExpense WHERE mTravelID = :travelID")
    LiveData<List<TravelExpense>> findAllOfTravel(int travelID);
}
