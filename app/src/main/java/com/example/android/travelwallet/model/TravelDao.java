package com.example.android.travelwallet.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface TravelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Travel> travels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Travel travel);

    @Update
    void update(Travel travel);

    @Delete
    void delete(Travel travel);

    @Query("SELECT * FROM Travel WHERE mId = :travelID")
    Travel getTravel(long travelID);

    @Query("SELECT * FROM Travel")
    LiveData<List<Travel>> findAll();

    @Query("SELECT * FROM Travel WHERE :currentDate = mStartDate")
    List<Travel> getCurrentTravels(Date currentDate);
}
