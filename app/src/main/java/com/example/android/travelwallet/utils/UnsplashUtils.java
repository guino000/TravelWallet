package com.example.android.travelwallet.utils;

import com.example.android.travelwallet.interfaces.UnsplashService;
import com.example.android.travelwallet.model.unsplash.UnsplashPhoto;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class UnsplashUtils {
    private static final String BASE_URL = "https://api.unsplash.com/";
    private static final String QUERY_FEATURED = "yes";
    private static final String QUERY_ORIENTATION = "landscape";

    public static Call<UnsplashPhoto> getRandomPhoto(String query){
        return getService().getRandomPhoto(query, "", QUERY_ORIENTATION, QUERY_FEATURED);
    }

    private static UnsplashService getService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create()
                )).build();
        return retrofit.create(UnsplashService.class);
    }
}
