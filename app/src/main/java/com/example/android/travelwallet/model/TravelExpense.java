package com.example.android.travelwallet.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.math.BigDecimal;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Travel.class,
                                    parentColumns = "mId",
                                    childColumns = "mTravelID",
                                    onDelete = CASCADE),
        indices = {@Index("mID"), @Index("mTravelID")})
public class TravelExpense {
    @PrimaryKey(autoGenerate = true)
    private long mID;
    private long mTravelID;
    private String mExpenseDescription;
    private BigDecimal mExpenseTotal;
    private String mCategory;
    private String mExpenseDate;

    public TravelExpense(){

    }

    public TravelExpense(String description, BigDecimal total, String category, String date, long travelID){
        mExpenseDescription = description;
        mExpenseTotal = total;
        mCategory = category;
        mExpenseDate = date;
        mTravelID = travelID;
    }

    public long getID(){return mID;}
    public void setID(long id){mID = id;}

    public long getTravelID(){return mTravelID;}
    public void setTravelID(long travelID){mTravelID = travelID;}

    public String getExpenseDescription(){return mExpenseDescription;}
    public void setExpenseDescription(String expenseDescription){mExpenseDescription = expenseDescription;}

    public BigDecimal getExpenseTotal(){return mExpenseTotal;}
    public void setExpenseTotal(BigDecimal total){mExpenseTotal = total;}

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getExpenseDate() {
        return mExpenseDate;
    }

    public void setExpenseDate(String mExpenseDate) {
        this.mExpenseDate = mExpenseDate;
    }
}
