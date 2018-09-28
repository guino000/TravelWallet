package com.example.android.travelwallet.utils;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.View;

import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.model.TravelValues;
import com.example.android.travelwallet.model.TravelViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public abstract class TravelUtils {
    public static String getBudgetExpenseComparison(Application application, Travel travel){
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        TravelValues expenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());

        if(expenses == null) return "";

        return String.format("%s - %s",
                getCurrencyFormattedValue(expenses.total),
                getCurrencyFormattedValue(travel.getBudget()));
    }

    public static int getBudgetSpentPercentage(Application application, Travel travel){
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        TravelValues expenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());

        if(expenses.total == null || travel.getBudget() == null) return 0;

        return expenses.total.divide(travel.getBudget()).multiply(new BigDecimal("100")).intValue();
    }

    public static String getCurrencyFormattedValue(BigDecimal value){
//        TODO: Create preferences screen to select desired currency
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
//        format.setCurrency(Currency.getInstance("USD"));
        if (value == null) return format.format(0);
        return format.format(value);
    }
}
