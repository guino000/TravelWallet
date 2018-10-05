package com.example.android.travelwallet.model;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converters {
    private static DateFormat df = SimpleDateFormat.getDateInstance();

    @TypeConverter
    public static BigDecimal fromDouble(Double value){
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public static Double toDouble(BigDecimal bigDecimal){
        if(bigDecimal == null){
            return null;
        }else{
            return bigDecimal.doubleValue();
        }
    }

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String toTimestamp(Date value) {
        return value.toString();
    }
}
