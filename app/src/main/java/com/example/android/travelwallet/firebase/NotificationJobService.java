package com.example.android.travelwallet.firebase;

import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.android.travelwallet.MainActivity;
import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.Converters;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.List;

import static com.example.android.travelwallet.MainActivity.NOTIFICATION_CHANNEL_ID;

@SuppressWarnings("FieldCanBeLocal")
public class NotificationJobService extends JobService {
    public static final String TAG = NotificationJobService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 11;
    private TravelViewModel mTravelViewModel;
    private NotificationCompat.Builder mTravelBuilder;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.v(TAG, "Querying current travels...");
//        Check if there are travels ongoing on the current date
        mTravelViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(TravelViewModel.class);
        List<Travel> mCurrentTravels = mTravelViewModel.getCurrentTravels(Converters.fromTimestamp(System.currentTimeMillis()));
        if(mCurrentTravels == null) return false;
        if(mCurrentTravels.size() == 0) return false;

//        Create a notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mTravelBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_travel)
                .setContentTitle(getString(R.string.travel_notification_title))
                .setContentText(getString(R.string.travel_notification_text))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.travel_notification_text)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
//        Show created notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, mTravelBuilder.build());

        Log.v(TAG, "Notification showed!");

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
