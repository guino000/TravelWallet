package com.example.android.travelwallet.interfaces;

import com.example.android.travelwallet.model.GooglePlaces.Candidate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GooglePlacesService {
    @GET("textsearch/{output}")
    Call<Candidate> findPlaceFromText(@Path("output") String output,
                                      @Query("query") String query,
                                      @Query("key") String apiKey);
}
