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
        mCountries = new ArrayList<>();
        List<Country> countries = lists[0];
        for (Country country : countries) {
            List<Currency> newCurrencies = new ArrayList<>();
            List<Currency> currencies = country.getCurrencies();
            if (currencies != null) {
                if (!currencies.isEmpty()) {
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
            mCountries.add(country);
        }
        return mCountries;
    }

    @Override
    protected void onPostExecute(List<Country> countries) {
        mDelegate.processFinish(countries);
    }
}
