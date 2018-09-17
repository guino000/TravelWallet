package com.example.android.travelwallet.model;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Travel.class}, version = 1)
public abstract class TravelDatabase extends RoomDatabase {
    private static volatile TravelDatabase INSTANCE;
    public abstract TravelDao travelDao();

    public static TravelDatabase getInstance(Context context){
        synchronized (TravelDatabase.class){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TravelDatabase.class, "travel_database.db")
                        .build();
            }
            return INSTANCE;
        }
    }
}