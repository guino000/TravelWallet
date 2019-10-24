package com.example.android.travelwallet.utils;

import com.example.android.travelwallet.interfaces.RestCountriesService;
import com.example.android.travelwallet.model.restcountries.Country;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RestCountriesUtils {
    private static final String BASE_URL = "https://restcountries.eu/rest/v2/";

    public static Call<List<Country>> getAllCountries() {
        return getService().getAllCountries();
    }

    private static RestCountriesService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create()))
                .build();
        return retrofit.create(RestCountriesService.class);
    }
}
