package com.example.android.travelwallet.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
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
    LiveData<List<TravelExpense>> findAllOfTravel(long travelID);

    @Query("SELECT SUM(mExpenseTotal) as total FROM TravelExpense WHERE mTravelID = :travelID")
    TravelValues getTotalExpensesOfTravel(long travelID);

    @Query("SELECT SUM(te.mExpenseTotal) > t.mBudget as total " +
            "FROM TravelExpense te, Travel t " +
            "WHERE te.mTravelID = :travelID AND t.mId = :travelID " +
            "GROUP BY t.mId")
    Boolean isOverspent(long travelID);

    @Query("SELECT * FROM TravelExpense WHERE mExpenseDate = :expenseDate")
    LiveData<List<TravelExpense>> getExpensesOfDate(String expenseDate);
}
