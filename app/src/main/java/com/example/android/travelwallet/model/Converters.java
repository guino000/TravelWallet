package com.example.android.travelwallet.model;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Converters {
    @TypeConverter
    public BigDecimal fromDouble(Double value){
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public Double toDouble(BigDecimal bigDecimal){
        if(bigDecimal == null){
            return null;
        }else{
            return bigDecimal.doubleValue();
        }
    }
}
