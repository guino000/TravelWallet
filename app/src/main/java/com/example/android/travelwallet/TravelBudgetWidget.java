package com.example.android.travelwallet;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.android.travelwallet.model.TravelViewModel;

/**
 * Implementation of App Widget functionality.
 */
public class TravelBudgetWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance((Application) context.getApplicationContext()).create(TravelViewModel.class);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.travel_budget_widget);
//        views.setTextViewText(R.id.tv_widget_budget, travelViewModel);

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
}

