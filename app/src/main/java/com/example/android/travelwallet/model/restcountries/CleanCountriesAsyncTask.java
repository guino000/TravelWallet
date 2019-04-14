package com.example.android.travelwallet.model.restcountries;

import android.os.AsyncTask;

import com.example.android.travelwallet.interfaces.AsyncTaskDelegate;

import java.util.ArrayList;
import java.util.List;

public class CleanCountriesAsyncTask extends AsyncTask<List<Country>, Void, List<Country>> {
    private List<Country> mCountries;
    private AsyncTaskDelegate<List<Country>> mDelegate;

    public CleanCountriesAsyncTask(AsyncTaskDelegate<List<Country>> delegate) {
        mDelegate = delegate;
    }

    @Override
    protected List<Country> doInBackground(List<Country>... lists) {
//        Treat country data retrieved from the API
        mCountries = lists[0];
        for (Country country : mCountries) {
            List<Currency> newCurrencies = new ArrayList<>();
            List<Currency> currencies = country.getCurrencies();
            if (currencies != null) {
                if (!currencies.isEmpty()) {
//                    Remove currencies that have no code (none)
                    for (Currency currency : currencies) {
                        if (currency.getCode() != null) {
                            if (!currency.getCode().equals("(none)")) {
                                newCurrencies.add(currency);
                            }
                        }
                    }
                }
            }
            country.setCurrencies(newCurrencies);
        }
        return mCountries;
    }

    @Override
    protected void onPostExecute(List<Country> countries) {
        mDelegate.processFinish(countries);
    }
}
