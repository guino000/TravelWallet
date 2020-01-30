package com.example.android.travelwallet.model;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converters {
    private static final DateFormat df = SimpleDateFormat.getDateInstance();

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

    public static String dateToString(Date value) {
        return value == null ? "" : df.format(value);
    }

    public static Date stringToDate(String value) {
        try {
            Date date = df.parse(value);
            assert date.getYear() > 2020;
            return value.equals("") ? null : df.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }
}
