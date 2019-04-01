package com.example.android.travelwallet.interfaces;

import com.example.android.travelwallet.model.restcountries.Country;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestCountriesService {
    @GET("all")
    Call<List<Country>> getAllCountries();
}
