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
import java.sql.Date;
import java.text.ParseException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_travel_form);
        ButterKnife.bind(this);

//        Create calendars for date pickers
        final Calendar calendar = Calendar.getInstance();
        final Calendar minDate = Calendar.getInstance();
        final Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.YEAR, minDate.get(Calendar.YEAR) + 99);

//        Create dateSetListeners for date picker
        final DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mStartDateEditText.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
                minDate.set(Calendar.YEAR,year);
                minDate.set(Calendar.MONTH,month);
                minDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            }
        };

        final DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEndDateEditText.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
                maxDate.set(Calendar.YEAR,year);
                maxDate.set(Calendar.MONTH,month);
                maxDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            }
        };

        mStartDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog startDatePickerDialog = new DatePickerDialog(InsertTravelFormActivity.this, startDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                startDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                startDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                startDatePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mStartDateEditText.setText("");
                        minDate.setTimeInMillis(System.currentTimeMillis() - 1000);
                    }
                });

                startDatePickerDialog.show();
            }
        });

        mEndDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog endDatePickerDialog = new DatePickerDialog(InsertTravelFormActivity.this, endDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                endDatePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

                endDatePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mEndDateEditText.setText("");
                        maxDate.setTimeInMillis(System.currentTimeMillis());
                        maxDate.set(Calendar.YEAR, maxDate.get(Calendar.YEAR) + 99);
                    }
                });

                endDatePickerDialog.show();
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
}
