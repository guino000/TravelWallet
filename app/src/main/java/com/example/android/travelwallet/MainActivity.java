package com.example.android.travelwallet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupMenu;

import com.example.android.travelwallet.adapters.TravelAdapter;
import com.example.android.travelwallet.interfaces.CardPopupMenuListener;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        TravelAdapter.TravelAdapterOnClickHandler,
        CardPopupMenuListener {

    public static final String KEY_SHARED_PREFS_LAST_VIEWED_TRAVEL_ID = "last_viewed_travel";
    public static final String SHARED_PREFS_NAME = "com.example.android.travelwallet";
    public static final String NOTIFICATION_CHANNEL_ID = "travel_notifications";
    public static final int NOTIFICATION_ID = 11;

    @BindView(R.id.rv_travels)
    RecyclerView mTravelsRecyclerView;
    TravelAdapter mTravelAdapter;

    @BindView(R.id.bt_add_travel)
    FloatingActionButton mAddTravelButton;

    @BindView(R.id.adView)
    AdView mAdView;

    TravelViewModel mTravelViewModel;
    NotificationCompat.Builder mTravelBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        Configure Travel Notification
        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mTravelBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_flight_grey_24dp)
                .setContentTitle(getString(R.string.travel_notification_title))
                .setContentText(getString(R.string.travel_notification_text))
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.travel_notification_text)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

//        Configure AdView
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        Configure Travel Recycler View
        mTravelAdapter = new TravelAdapter(this, this, this);
        mTravelAdapter.setHasStableIds(true);
        mTravelsRecyclerView.setAdapter(mTravelAdapter);
        if(getResources().getConfiguration().screenWidthDp < 600) {
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,
                    GridLayoutManager.VERTICAL, false));
        }else{
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,
                    GridLayoutManager.VERTICAL, false));
            ViewTreeObserver viewTreeObserver = mTravelsRecyclerView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    calculateSize();
                }
            });
        }
        mTravelsRecyclerView.setHasFixedSize(true);
        mTravelViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);
        mTravelViewModel.getAllTravels().observe(this, new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                mTravelAdapter.setData(travels);
            }
        });
    }

    @OnClick(R.id.bt_add_travel)
    public void callAddTravelActivity(){
        Intent intent = new Intent(this, InsertTravelFormActivity.class);
        startActivity(intent);
    }

    private void calculateSize() {
        int spanCount = (int) Math.floor(mTravelsRecyclerView.getWidth() /
                getResources().getDimension(R.dimen.travel_miniature_width));
        ((GridLayoutManager) mTravelsRecyclerView.getLayoutManager()).setSpanCount(spanCount);
    }

    @Override
    public void onClick(Travel clickedTravel) {
//        Writes to shared preferences the ID of the last clicked recipe
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putLong(KEY_SHARED_PREFS_LAST_VIEWED_TRAVEL_ID, clickedTravel.getId())
                .apply();

//        Notify widget that data changed
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] ids = widgetManager.getAppWidgetIds(
                new ComponentName(this, TravelBudgetWidget.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(TravelBudgetWidget.WIDGET_EXTRA_IDS, ids);
        sendBroadcast(updateIntent);

//        Calls details activity
        Intent intent = new Intent(this, TravelDetailsActivity.class);
        intent.putExtra(TravelDetailsActivity.KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(clickedTravel));
        startActivity(intent);
    }

    public void startTravelFormActivityAsEditMode(Travel travel){
        Intent intent = new Intent(this, InsertTravelFormActivity.class);
        intent.putExtra(InsertTravelFormActivity.KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(travel));
        startActivity(intent);
    }

    @Override
    public void onPopupMenuClick(View view, final int pos) {
        //        Create Card popup menu
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.travel_miniature_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_delete :
                        mTravelViewModel.delete(mTravelAdapter.getData().get(pos));
                        return true;
                    case R.id.item_edit :
                        startTravelFormActivityAsEditMode(mTravelAdapter.getData().get(pos));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @OnClick(R.id.bt_show_notification)
    public void showNotification(){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, mTravelBuilder.build());
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
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
}
