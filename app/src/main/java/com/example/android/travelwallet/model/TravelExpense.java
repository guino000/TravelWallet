package com.example.android.travelwallet.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.math.BigDecimal;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Travel.class,
                                    parentColumns = "mId",
                                    childColumns = "mTravelID",
                                    onDelete = CASCADE))
public class TravelExpense {
    @PrimaryKey(autoGenerate = true)
    private int mID;
    private int mTravelID;
    private String mExpenseDescription;
    private BigDecimal mExpenseTotal;
    private String mCategory;
    private String mExpenseDate;

    public TravelExpense(){

    }

    public TravelExpense(int travelID, String description, BigDecimal total, String category, String date){
        mTravelID = travelID;
        mExpenseDescription = description;
        mExpenseTotal = total;
        mCategory = category;
        mExpenseDate = date;
    }

    public int getId(){return mID;}
    public void setId(int id){mID = id;}

    public int getTravelID(){return mTravelID;}
    public void setTravelID(int travelID){mTravelID = travelID;}

    public String getExpenseDescription(){return mExpenseDescription;}
    public void setExpenseDescription(String expenseDescription){mExpenseDescription = expenseDescription;}

    public BigDecimal getTotal(){return mExpenseTotal;}
    public void setTotal(BigDecimal total){mExpenseTotal = total;}
}
