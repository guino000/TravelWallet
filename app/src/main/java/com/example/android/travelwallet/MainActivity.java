package com.example.android.travelwallet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.travelwallet.adapters.TravelAdapter;
import com.example.android.travelwallet.firebase.NotificationJobService;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        TravelAdapter.TravelAdapterOnClickHandler{

    public static final String KEY_SHARED_PREFS_LAST_VIEWED_TRAVEL_ID = "last_viewed_travel";
    public static final String SHARED_PREFS_NAME = "com.example.android.travelwallet";
    public static final String NOTIFICATION_CHANNEL_ID = "travel_notifications";
    public static final String NOTIFICATION_JOB_TAG = "notification_job";
    public static final int SECONDS_IN_DAY = 24*60*60;

    @BindView(R.id.rv_travels)
    RecyclerView mTravelsRecyclerView;
    TravelAdapter mTravelAdapter;
    @BindView(R.id.bt_add_travel)
    FloatingActionButton mAddTravelButton;
    @BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.tvEmptyView)
    TextView mEmptyTextView;
    @BindView(R.id.imgEmpty)
    ImageView mEmptyImageView;

    TravelViewModel mTravelViewModel;
    ExpenseViewModel mExpenseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        Configure Travel Notification Channel
        createNotificationChannel();

//        Schedule Firebase Job for notifications
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job job = dispatcher.newJobBuilder()
                .setService(NotificationJobService.class)
                .setTag(NOTIFICATION_JOB_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0,SECONDS_IN_DAY))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();

        dispatcher.mustSchedule(job);

//        Configure AdView
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        Configure Travel Recycler View
        mTravelAdapter = new TravelAdapter(this, this);
        mTravelAdapter.setHasStableIds(true);
        mTravelsRecyclerView.setAdapter(mTravelAdapter);

//        Check how many columns the grid will have
        if(getResources().getConfiguration().screenWidthDp < 600) {
//            If screen width is small, the travel cards occupy the entire screen
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1,
                    GridLayoutManager.VERTICAL, false));
        }else{
//            If screen width is big, the travel cards occupy half the screen
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,
                    GridLayoutManager.VERTICAL, false));
        }
        mTravelsRecyclerView.setHasFixedSize(true);

//        Add an observer to update widget data on travel change
        mTravelViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);

        mTravelViewModel.getAllTravels().observe(this, new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                mTravelAdapter.setData(travels);
                updateWidget();
                setEmptyViewVisibility(travels.isEmpty());
            }
        });
    }

    private void setEmptyViewVisibility(boolean isVisible){
        if(isVisible){
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyImageView.setVisibility(View.VISIBLE);
        }else{
            mEmptyTextView.setVisibility(View.GONE);
            mEmptyImageView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.bt_add_travel)
    public void callAddTravelActivity(){
        Intent intent = new Intent(this, InsertTravelFormActivity.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.travel_notification_channel_name);
            String description = getString(R.string.travel_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void updateWidget() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] ids = widgetManager.getAppWidgetIds(
                new ComponentName(this, TravelBudgetWidget.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(TravelBudgetWidget.WIDGET_EXTRA_IDS, ids);
        sendBroadcast(updateIntent);
    }

    @Override
    public void onClick(Travel clickedTravel) {
//        Writes to shared preferences the ID of the last clicked recipe
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putLong(KEY_SHARED_PREFS_LAST_VIEWED_TRAVEL_ID, clickedTravel.getId())
                .apply();

//        Notify widget that data changed
        updateWidget();

//        Calls details activity
        Intent intent = new Intent(this, TravelDetailsActivity.class);
        intent.putExtra(TravelDetailsActivity.KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(clickedTravel));
        startActivity(intent);
    }
}
