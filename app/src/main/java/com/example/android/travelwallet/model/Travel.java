package com.example.android.travelwallet.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.math.BigDecimal;

@Entity
public class Travel {
    @PrimaryKey(autoGenerate = true)
    private int mId;
    private String mName;
    private String mDestination;
    private BigDecimal mBudget;
    private String mStartDate;
    private String mEndDate;

    public Travel(){}

    @Ignore
    public Travel(String name, String destination, BigDecimal budget,
                  String startDate, String endDate){
        mName = name;
        mDestination = destination;
        mBudget = budget;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    public String getName(){return mName;}
    public String getDestination(){return mDestination;}
    public BigDecimal getBudget(){return mBudget;}
    public String getStartDate(){return mStartDate;}
    public String getEndDate(){return mEndDate;}
}
