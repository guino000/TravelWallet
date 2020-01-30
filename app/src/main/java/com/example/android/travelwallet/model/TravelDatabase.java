package com.example.android.travelwallet.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {Travel.class, TravelExpense.class}, version = 13)
@TypeConverters({Converters.class})
public abstract class TravelDatabase extends RoomDatabase {
    private static volatile TravelDatabase INSTANCE;
    public abstract TravelDao travelDao();
    public abstract TravelExpenseDao expenseDao();

    public abstract TravelAndExpenseDao travelAndExpenseDao();

    public static TravelDatabase getInstance(Context context){
        synchronized (TravelDatabase.class){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TravelDatabase.class, "travel_database.db")
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
    }
}
