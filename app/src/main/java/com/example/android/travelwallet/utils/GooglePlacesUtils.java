package com.example.android.travelwallet.utils;

import com.example.android.travelwallet.BuildConfig;
import com.example.android.travelwallet.interfaces.GooglePlacesService;
import com.example.android.travelwallet.model.GooglePlaces.Candidate;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class GooglePlacesUtils {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    public static Call<Candidate> findPlaceFromText(String input) {
        return getService().findPlaceFromText("json", input, BuildConfig.GooglePlacesAPIKey);
    }

    private static GooglePlacesService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create()))
                .build();
        return retrofit.create(GooglePlacesService.class);
    }
}
