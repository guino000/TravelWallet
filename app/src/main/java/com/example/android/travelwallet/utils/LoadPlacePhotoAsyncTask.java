package com.example.android.travelwallet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.travelwallet.R;
import com.example.android.travelwallet.interfaces.LoadPlacePhotoListener;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoadPlacePhotoAsyncTask extends AsyncTask<String, Void, Void> {
    private GeoDataClient mGeoDataClient;
    private LoadPlacePhotoListener mLoadPlacePhotoListener;

    public LoadPlacePhotoAsyncTask(GeoDataClient geoDataClient, LoadPlacePhotoListener listener){
        mGeoDataClient = geoDataClient;
        mLoadPlacePhotoListener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponseTask = mGeoDataClient.getPlacePhotos(strings[0]);

        photoMetadataResponseTask.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                if(!task.isSuccessful()) {
                    mLoadPlacePhotoListener.onLoadPlacePhoto(null);
                    return;
                }
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if(photoMetadataBuffer.getCount() <= 0) {
                    mLoadPlacePhotoListener.onLoadPlacePhoto(null);
                    return;
                }
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                CharSequence attribution = photoMetadata.getAttributions();
                Task<PlacePhotoResponse> photoResponseTask = mGeoDataClient.getPhoto(photoMetadata);
                photoResponseTask.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                        mLoadPlacePhotoListener.onLoadPlacePhoto(bitmap);
                    }
                });
            }
        });

        return null;
    }
}
