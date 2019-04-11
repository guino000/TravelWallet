package com.example.android.travelwallet.utils;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelValues;
import com.example.android.travelwallet.model.TravelViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public abstract class TravelUtils {
    public static int getExpenseIconIDByCategory(String category, Context context){
        if(category.equals(context.getString(R.string.category_item_hotel)))
            return R.drawable.ic_hotel_grey_24dp;

        if(category.equals(context.getString(R.string.category_item_food_drink)))
            return R.drawable.ic_local_dining_grey_24dp;

        if(category.equals(context.getString(R.string.category_item_shopping)))
            return R.drawable.ic_local_mall_grey_24dp;

        if(category.equals(context.getString(R.string.category_item_transport)))
            return R.drawable.ic_flight_grey_24dp;

        if(category.equals(context.getString(R.string.category_item_entertainment)))
            return R.drawable.ic_local_play_grey_24dp;

        if(category.equals(context.getString(R.string.category_item_others)))
            return R.drawable.ic_local_grocery_store_grey_24dp;

        return 0;
    }

    public static Travel getCurrentTravel(long travelID, Application application) {
        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(TravelViewModel.class);
        return travelViewModel.getTravel(travelID);
    }

    public static String getBudgetExpenseComparison(Application application, Travel travel){
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        TravelValues expenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());

        if(expenses == null) return "";

        return String.format("%s - %s",
                getCurrencyFormattedValue(expenses.total, travel.getCurrencyCode()),
                getCurrencyFormattedValue(travel.getBudget(),travel.getCurrencyCode()));
    }

    public static float getBudgetSpentPercentage(Application application, Travel travel){
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        TravelValues expenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());

        if(expenses.total == null || travel.getBudget() == null) return 0;

        return expenses.total.divide(travel.getBudget(),2,RoundingMode.HALF_EVEN).floatValue();
    }

    public static String getCurrencyFormattedValue(BigDecimal value, String currencyCode){
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
