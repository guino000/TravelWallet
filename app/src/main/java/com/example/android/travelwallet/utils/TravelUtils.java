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
import java.util.Date;

public abstract class TravelUtils {
    public static int getExpenseIconIDByCategory(String category, Context context){
        if(category.equals(context.getString(R.string.category_item_hotel)))
            return R.drawable.ic_hotel_black_50dp;

        if(category.equals(context.getString(R.string.category_item_food_drink)))
            return R.drawable.ic_restaurant_black_50dp;

        if(category.equals(context.getString(R.string.category_item_shopping)))
            return R.drawable.ic_local_mall_black_50dp;

        if(category.equals(context.getString(R.string.category_item_transport)))
            return R.drawable.ic_airplanemode_active_black_50dp;

        if(category.equals(context.getString(R.string.category_item_entertainment)))
            return R.drawable.ic_local_play_black_50dp;

        if(category.equals(context.getString(R.string.category_item_others)))
            return R.drawable.ic_local_grocery_store_black_50dp;

        return 0;
    }

    public static Travel getCurrentTravel(long travelID, Application application) {
        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(TravelViewModel.class);
        return travelViewModel.getTravel(travelID);
    }

    public static float getBudgetSpentPercentage(Application application, Travel travel){
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        TravelValues expenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());

        if(expenses.total == null || travel.getBudget() == null) return 0;

        return expenses.total.divide(travel.getBudget(),2,RoundingMode.HALF_EVEN).floatValue();
    }

    public static BigDecimal getTravelExpensesTotal(Application application, Travel travel) {
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        TravelValues expenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());

        if (expenses.getTotal() == null)
            return new BigDecimal(0);
        else
            return expenses.getTotal();
    }

    public static BigDecimal getTotalExpensesOfDate(Application application, Date date, long travelID){
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        return  expenseViewModel.getTotalExpensesOfDate(date, travelID);
    }
}
