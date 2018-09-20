package com.example.android.travelwallet.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;

import java.math.BigDecimal;
import java.util.List;

@Entity(indices = {@Index("mId")})
public class Travel {
    @PrimaryKey(autoGenerate = true)
    private long mId;
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

    public long getId(){return mId;}
    public void setId(long id){mId = id;}
    public String getName(){return mName;}
    public void setName(String name){mName = name;}
    public String getDestination(){return mDestination;}
    public void setDestination(String dest){mDestination = dest;}
    public BigDecimal getBudget(){return mBudget;}
    public void setBudget(BigDecimal budget){mBudget = budget;}
    public String getStartDate(){return mStartDate;}
    public void setStartDate(String startDate){mStartDate = startDate;}
    public String getEndDate(){return mEndDate;}
    public void setEndDate(String endDate){mEndDate = endDate;}
}
