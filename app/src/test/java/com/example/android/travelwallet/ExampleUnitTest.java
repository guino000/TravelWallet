package com.example.android.travelwallet;

import com.example.android.travelwallet.model.GooglePlaces.Candidate;
import com.example.android.travelwallet.model.GooglePlaces.PlacesResponse;
import com.example.android.travelwallet.model.restcountries.Country;
import com.example.android.travelwallet.model.unsplash.UnsplashPhoto;
import com.example.android.travelwallet.utils.GooglePlacesUtils;
import com.example.android.travelwallet.utils.RestCountriesUtils;
import com.example.android.travelwallet.utils.UnsplashUtils;

import org.junit.Test;

import java.util.List;

import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getAllCountriesFromAPI_isCorrect(){
        try {
            Response<List<Country>> response = RestCountriesUtils.getAllCountries().execute();
            assert response.body() != null;
            assertFalse(response.body().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void getRandomPhotoFromUnsplash_isCorrect() {
        try {
            Response<UnsplashPhoto> response = UnsplashUtils.getRandomPhoto("Brazil panorama").execute();
            assert response.body() != null;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void getPlaceFromGooglePlaces_isCorrect() {
        try{
            Response<PlacesResponse> response = GooglePlacesUtils.findPlaceFromText("Brazil").execute();
            assert response.body() != null;
        }catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void getPlacePhotoFromGooglePlaces_isCorrect() {
        try {
            Response<PlacesResponse> response = GooglePlacesUtils.findPlaceFromText("Brazil").execute();
            List<Candidate> candidates = response.body().getCandidates();
            assert !candidates.isEmpty();
            Candidate candidate = candidates.get(0);
            String photoRef = candidate.getPhotos().get(0).getPhotoReference();

            Response<String> photoResponse = GooglePlacesUtils.getPhotoFromPhotoReference(photoRef, 900).execute();
            assert photoResponse.body() != null;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
}