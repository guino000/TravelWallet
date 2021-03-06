package com.example.android.travelwallet;

import com.example.android.travelwallet.model.GooglePlaces.PlacesResponse;
import com.example.android.travelwallet.model.restcountries.Country;
import com.example.android.travelwallet.utils.GooglePlacesUtils;
import com.example.android.travelwallet.utils.RestCountriesUtils;

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
    public void getPlaceFromGooglePlaces_isCorrect() {
        try{
            Response<PlacesResponse> response = GooglePlacesUtils.findPlaceFromText("Brazil").execute();
            assert response.body() != null;
        }catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }
}