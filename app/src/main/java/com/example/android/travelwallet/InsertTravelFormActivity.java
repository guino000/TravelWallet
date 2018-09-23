package com.example.android.travelwallet;

import android.arch.lifecycle.ViewModelProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InsertTravelFormActivity extends AppCompatActivity {
    @BindView(R.id.et_travel_name)
    EditText mTravelNameEditText;

    @BindView(R.id.et_total_budget)
    EditText mTotalBudgetEditText;

    @BindView(R.id.et_destination)
    EditText mDestinationEditText;

    @BindView(R.id.et_start_date)
    EditText mStartDateEditText;

    @BindView(R.id.et_end_date)
    EditText mEndDateEditText;

    @BindView(R.id.bt_create_travel)
    Button mAddTravelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_travel_form);
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.bt_create_travel)
    public void AddTravel(){
        Travel travel = new Travel(
                mTravelNameEditText.getText().toString(),
                mDestinationEditText.getText().toString(),
                new BigDecimal(mTotalBudgetEditText.getText().toString()),
                mStartDateEditText.getText().toString(),
                mEndDateEditText.getText().toString());

        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);
        travelViewModel.insert(travel);
        Toast.makeText(this,"Travel " + travel.getName() + " created!", Toast.LENGTH_LONG).show();
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
