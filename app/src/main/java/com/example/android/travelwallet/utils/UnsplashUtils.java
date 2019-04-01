package com.example.android.travelwallet.utils;

import com.example.android.travelwallet.BuildConfig;
import com.example.android.travelwallet.interfaces.UnsplashService;
import com.example.android.travelwallet.model.UnsplashPhoto;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class UnsplashUtils {
    private static final String BASE_URL = "https://api.unsplash.com/";

    public static Call<UnsplashPhoto> getRandomPhoto(String query){
        return getService().getRandomPhoto(query, BuildConfig.UnsplashAccessKey, "landscape");
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
