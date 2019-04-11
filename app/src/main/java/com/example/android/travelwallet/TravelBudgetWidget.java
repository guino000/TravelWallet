package com.example.android.travelwallet;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;
import com.example.android.travelwallet.utils.TravelUtils;

/**
 * Implementation of App Widget functionality.
 */
public class TravelBudgetWidget extends AppWidgetProvider {
    public static final String WIDGET_EXTRA_IDS = "extra_ids";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
//        Get last viewed travel ID
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        long lastViewTravelID = sharedPreferences.getLong(
                MainActivity.KEY_SHARED_PREFS_LAST_VIEWED_TRAVEL_ID,-1);

        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance((Application) context.getApplicationContext()).create(TravelViewModel.class);
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance((Application) context.getApplicationContext()).create(ExpenseViewModel.class);

        Travel lastViewTravel = travelViewModel.getTravel(lastViewTravelID);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.travel_budget_widget);
        if(lastViewTravel == null){
            views.setViewVisibility(R.id.tv_widget_empty_view, View.VISIBLE);
            views.setViewVisibility(R.id.tv_widget_travel_name, View.GONE);
            views.setViewVisibility(R.id.ll_budget, View.GONE);
            views.setViewVisibility(R.id.ll_expenses, View.GONE);
        }else{
            views.setViewVisibility(R.id.tv_widget_empty_view, View.GONE);
            views.setViewVisibility(R.id.tv_widget_travel_name, View.VISIBLE);
            views.setViewVisibility(R.id.ll_budget, View.VISIBLE);
            views.setViewVisibility(R.id.ll_expenses, View.VISIBLE);
            views.setTextViewText(R.id.tv_widget_travel_name, lastViewTravel.getName());
            views.setTextViewText(R.id.tv_widget_budget, TravelUtils.getCurrencyFormattedValue(lastViewTravel.getBudget(), lastViewTravel.getCurrencyCode()));
            views.setTextViewText(R.id.tv_widget_expenses,
                    TravelUtils.getCurrencyFormattedValue(expenseViewModel.getTotalExpensesOfTravel(lastViewTravelID).getTotal(), lastViewTravel.getCurrencyCode()));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra(WIDGET_EXTRA_IDS)){
            @SuppressWarnings("ConstantConditions") int[] ids = intent.getExtras().getIntArray(WIDGET_EXTRA_IDS);
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        }else {
            super.onReceive(context, intent);
        }
    }
}

