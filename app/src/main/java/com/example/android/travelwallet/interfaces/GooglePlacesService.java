package com.example.android.travelwallet.interfaces;

import com.example.android.travelwallet.model.GooglePlaces.PlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GooglePlacesService {
    @GET("findplacefromtext/{output}")
    Call<PlacesResponse> findPlaceFromText(@Path("output") String output,
                                           @Query("input") String query,
                                           @Query("inputtype") String inputType,
                                           @Query("key") String apiKey,
                                           @Query("fields") String fields);

    @GET("photo")
    Call<String> getPhotoFromPhotoReference(@Query("key") String apiKey,
                                            @Query("photoreference") String photoReference,
                                            @Query("maxwidth") int maxWidth);
}
