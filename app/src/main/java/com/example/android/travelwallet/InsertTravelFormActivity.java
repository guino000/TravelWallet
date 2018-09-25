package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_travel_form);
        ButterKnife.bind(this);

        mCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };

        mStartDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(InsertTravelFormActivity.this, dateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                updateLabel(mStartDateEditText);
            }
        });

        mEndDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(InsertTravelFormActivity.this, dateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                updateLabel(mEndDateEditText);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.bt_create_travel)
    public void AddTravel(){

//        Check if form was filled correctly
        boolean flagError = false;
        if(mTravelNameEditText.getText().toString().trim().equals("")){
            mTravelNameEditText.setError(getString(R.string.error_travel_name_required));
            flagError = true;
        }

        if(mTotalBudgetEditText.getText().toString().trim().equals("")){
            mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_required));
            flagError = true;
        }

        if(mDestinationEditText.getText().toString().trim().equals("")){
            mDestinationEditText.setError(getString(R.string.error_travel_destination_required));
            flagError = true;
        }

        if(mStartDateEditText.getText().toString().trim().equals("")){
            mStartDateEditText.setError(getString(R.string.error_travel_start_date_required));
            flagError = true;
        }

        if(mEndDateEditText.getText().toString().trim().equals("")){
            mEndDateEditText.setError(getString(R.string.error_travel_end_date_required));
            flagError = true;
        }

        if(flagError) return;

//        Insert travel if form is correct
        Travel travel = new Travel(
                mTravelNameEditText.getText().toString(),
                mDestinationEditText.getText().toString(),
                new BigDecimal(mTotalBudgetEditText.getText().toString()),
                mStartDateEditText.getText().toString(),
                mEndDateEditText.getText().toString());

        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);
        travelViewModel.insert(travel);

//        Return to main activity
        finish();
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

    private void updateLabel(EditText dateEditText){
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        dateEditText.setText(sdf.format(mCalendar.getTime()));
    }
}
