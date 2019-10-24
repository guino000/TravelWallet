package com.example.android.travelwallet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.restcountries.Currency;

import java.util.List;

public class CurrencyArrayAdapter extends ArrayAdapter<Currency> {
    private Context mContext;
    private List<Currency> mCurrencies;

    public CurrencyArrayAdapter(@NonNull Context context, int resource, @NonNull List<Currency> objects) {
        super(context, resource, objects);
        mContext = context;
        mCurrencies = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Currency currency = mCurrencies.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = convertView;
        if (listItem == null)
            listItem = inflater.inflate(R.layout.currency_spinner_list_item, null);

        TextView currencyName = listItem.findViewById(R.id.tv_currency_name);
        currencyName.setText(String.format("%s (%s)", currency.getName(), currency.getCode()));

        return listItem;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Currency currency = mCurrencies.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = convertView;
        if (listItem == null)
            listItem = inflater.inflate(R.layout.currency_spinner_list_item, null);

        TextView currencyName = listItem.findViewById(R.id.tv_currency_name);
        currencyName.setText(String.format("%s (%s)", currency.getName(), currency.getCode()));

        return listItem;
    }
}
