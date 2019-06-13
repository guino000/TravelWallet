package com.example.android.travelwallet.utils;

import com.example.android.travelwallet.BuildConfig;
import com.example.android.travelwallet.interfaces.GooglePlacesService;
import com.example.android.travelwallet.model.GooglePlaces.PlacesResponse;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public abstract class GooglePlacesUtils {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    public static Call<PlacesResponse> findPlaceFromText(String input) {
        String fields = "formatted_address,name,photos";
        return getService().findPlaceFromText("json", input, "textquery", BuildConfig.GooglePlacesAPIKey, fields);
    }

    public static String getPhotoFromPhotoReference(String photoReference, int maxWidth) {
        return String.format("%sphoto?key=%s&photoreference=%s&maxwidth=%s", BASE_URL, BuildConfig.GooglePlacesAPIKey, photoReference, maxWidth);
    }

    private static GooglePlacesService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create()))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(GooglePlacesService.class);
    }

    private static GooglePlacesService getServiceStringResponse() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(GooglePlacesService.class);
    }
}
