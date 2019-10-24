package com.example.android.travelwallet.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.restcountries.Country;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.List;

public class CountryArrayAdapter extends ArrayAdapter<Country>{


    private Context mContext;
    private List<Country> mCountries;


    public CountryArrayAdapter(@NonNull Context context, int resource, @NonNull List<Country> objects) {
        super(context, resource, objects);
        mContext = context;
        mCountries = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Country country = mCountries.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View viewItem = convertView;
        if(viewItem == null)
            viewItem = inflater.inflate(R.layout.country_spinner_list_item, null);

        TextView countryName = viewItem.findViewById(R.id.tv_country_name);
        countryName.setText(country.getName());

        ImageView countryFlag = viewItem.findViewById(R.id.img_country_flag);
        GlideToVectorYou
                .init()
                .with((Activity) mContext)
                .getRequestBuilder()
                .load(country.getFlag())
                .apply(RequestOptions.centerCropTransform())
                .into(countryFlag);

        return viewItem;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Country country = mCountries.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View viewItem = convertView;
        if(viewItem == null)
            viewItem = inflater.inflate(R.layout.country_spinner_list_item, null);

        TextView countryName = viewItem.findViewById(R.id.tv_country_name);
        countryName.setText(country.getName());

        ImageView countryFlag = viewItem.findViewById(R.id.img_country_flag);
        GlideToVectorYou
                .init()
                .with((Activity) mContext)
                .getRequestBuilder()
                .load(country.getFlag())
                .apply(RequestOptions.centerCropTransform())
                .into(countryFlag);

        return viewItem;
    }
}
