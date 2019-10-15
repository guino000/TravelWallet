package com.example.android.travelwallet.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;

public abstract class CurrencyUtils {
    public static String FormatAsCurrencyWithoutSymbol(double number) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        return nf.format(number);
    }

    public static String getCurrencyFormattedValue(BigDecimal value, String currencyCode) {
        NumberFormat format = getCurrencyNumberFormat(currencyCode);
        if (value == null) return format.format(0);
        return format.format(value);
    }

    public static NumberFormat getCurrencyNumberFormat(String currencyCode) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(Currency.getInstance(currencyCode));
        return format;
    }
}
