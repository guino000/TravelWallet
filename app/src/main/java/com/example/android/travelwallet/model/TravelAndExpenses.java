package com.example.android.travelwallet.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class TravelAndExpenses {
    @Embedded
    public Travel travel;

    @Relation(parentColumn = "mId", entityColumn = "mTravelID", entity = TravelExpense.class)
    List<TravelExpense> mExpenses;
}
