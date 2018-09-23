package com.example.android.travelwallet.utils;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.view.View;

import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.model.TravelViewModel;

import java.math.BigDecimal;
import java.util.List;

public abstract class TravelUtils {
    public static String getBudgetExpenseComparison(Application application, Travel travel){
        BigDecimal expenseTotal = new BigDecimal(0);
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        List<TravelExpense> expenses = expenseViewModel.getAllExpensesOfTravel(travel);

        for (TravelExpense expense : expenses){
            expenseTotal = expenseTotal.add(expense.getExpenseTotal());
        }

        return String.format("%s - %s", expenseTotal.toString(), travel.getBudget().toString());
    }
}
