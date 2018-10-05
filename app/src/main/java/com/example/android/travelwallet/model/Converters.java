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
    public static String dateToString(Date value) {
        return value == null ? "" : df.format(value);
    }

    @TypeConverter
    public static Date stringToDate(String value) {
        try {
            return value.equals("") ? null : df.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
