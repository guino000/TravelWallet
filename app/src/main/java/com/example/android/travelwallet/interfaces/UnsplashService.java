package com.example.android.travelwallet.interfaces;

import com.example.android.travelwallet.model.unsplash.UnsplashPhoto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashService {
    @GET("photos/random")
    Call<UnsplashPhoto> getRandomPhoto(@Query("query") String query,
                                       @Query("client_id") String accessKey,
                                       @Query("orientation") String orientation,
                                       @Query("featured") String featured);
}
