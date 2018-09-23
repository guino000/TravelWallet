package com.example.android.travelwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class InsertTravelFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_travel_form);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedID = item.getItemId();
        switch (selectedID){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                throw  new UnsupportedOperationException();
        }
        return super.onOptionsItemSelected(item);
    }
}
