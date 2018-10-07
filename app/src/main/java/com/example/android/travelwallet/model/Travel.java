package com.example.android.travelwallet.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

import java.math.BigDecimal;
import java.util.Date;

@Parcel
@Entity(indices = {@Index("mId")})
public class Travel {
    @PrimaryKey(autoGenerate = true)
    private long mId;
    private String mName;
    private String mDestination;
    private BigDecimal mBudget;
    private Date mStartDate;
    private Date mEndDate;
    private String mPlaceID;

    public Travel(){}

    @Ignore
    public Travel(String name, String destination, BigDecimal budget,
                  Date startDate, Date endDate, String placeID){
        mName = name;
        mDestination = destination;
        mBudget = budget;
        mStartDate = startDate;
        mEndDate = endDate;
        mPlaceID = placeID;
    }

    public long getId(){return mId;}
    public void setId(long id){mId = id;}
    public String getName(){return mName;}
    public void setName(String name){mName = name;}
    public String getDestination(){return mDestination;}
    public void setDestination(String dest){mDestination = dest;}
    public BigDecimal getBudget(){return mBudget;}
    public void setBudget(BigDecimal budget){mBudget = budget;}
    public Date getStartDate(){return mStartDate;}
    public void setStartDate(Date startDate){mStartDate = startDate;}
    public Date getEndDate(){return mEndDate;}
    public void setEndDate(Date endDate){mEndDate = endDate;}
    public String getPlaceID(){
        return mPlaceID;
    }
    public void setPlaceID(String mPlaceID) {
        this.mPlaceID = mPlaceID;
    }
}
