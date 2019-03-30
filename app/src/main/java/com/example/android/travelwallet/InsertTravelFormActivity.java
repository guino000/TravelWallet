package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.travelwallet.model.Converters;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;
import com.example.android.travelwallet.utils.TravelUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class InsertTravelFormActivity extends AppCompatActivity {
    public static final String KEY_INTENT_EXTRA_TRAVEL = "extra_travel";
    public static final int PLACE_PICKER_REQUEST = 1;

    @BindView(R.id.tv_insert_travel_header)
    TextView mAddTravelHeaderTextView;
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

    @State
    String mCurrentTravelName;
    @State
    String mCurrentTravelBudget;
    @State
    String mCurrentTravelDestination;
    @SuppressWarnings("WeakerAccess")
    @State
    Date mCurrentTravelStartDate;
    @State
    Date mCurrentTravelEndDate;

    @State
    boolean mEditMode;
    private Travel mEditTravel;
    @State
    String mSelectedPlaceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_travel_form);
        ButterKnife.bind(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if(savedInstanceState != null){
            mEditTravel = Parcels.unwrap(savedInstanceState.getParcelable(KEY_INTENT_EXTRA_TRAVEL));
            mTravelNameEditText.setText(mCurrentTravelName);
            mTotalBudgetEditText.setText(mCurrentTravelBudget);
            mDestinationEditText.setText(mCurrentTravelDestination);
            mStartDateEditText.setText(Converters.dateToString(mCurrentTravelStartDate));
            mEndDateEditText.setText(Converters.dateToString(mCurrentTravelEndDate));
        }

//        Create calendars for date pickers
        final Calendar calendar = Calendar.getInstance();
        final Calendar minDate = Calendar.getInstance();
        final Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.YEAR, minDate.get(Calendar.YEAR) + 99);

//        Create dateSetListeners for date picker
        final DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                minDate.set(Calendar.YEAR,year);
                minDate.set(Calendar.MONTH,month);
                minDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                mStartDateEditText.setText(SimpleDateFormat.getDateInstance().format(minDate.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                maxDate.set(Calendar.YEAR,year);
                maxDate.set(Calendar.MONTH,month);
                maxDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                mEndDateEditText.setText(SimpleDateFormat.getDateInstance().format(maxDate.getTime()));
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

//        Create a text listener to format currency on budget edit text
        mTotalBudgetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                NumberFormat format = TravelUtils.getCurrencyNumberFormat();
                if(hasFocus){
                    try {
                        mTotalBudgetEditText.setText(
                                format.parse(mTotalBudgetEditText.getText().toString().trim()).toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mTotalBudgetEditText.setText("");
                    }
                }else{
                    if(!mTotalBudgetEditText.getText().toString().trim().equals("")) {
                        try {
                            mTotalBudgetEditText.setText(TravelUtils.getCurrencyFormattedValue(
                                    new BigDecimal((mTotalBudgetEditText.getText().toString().trim()))
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                            mTotalBudgetEditText.setText("");
                            mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_invalid));
                        }
                    }
                }
            }
        });

//        Check if there is an incoming travel object for edit mode
        Intent intent = getIntent();
        if(intent.hasExtra(KEY_INTENT_EXTRA_TRAVEL)){
            mEditMode = true;
            mEditTravel = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_TRAVEL));
            enableEditMode(mEditTravel);
        }else{
            mEditMode = false;
            disableEditMode();
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void enableEditMode(Travel travel){
        mTravelNameEditText.setText(travel.getName());
        mTotalBudgetEditText.setText(TravelUtils.getCurrencyFormattedValue(travel.getBudget()));
        mDestinationEditText.setText(travel.getDestination());
        mStartDateEditText.setText(Converters.dateToString(travel.getStartDate()));
        mEndDateEditText.setText(Converters.dateToString(travel.getEndDate()));
        mAddTravelButton.setText(R.string.button_edit_travel);
        mAddTravelHeaderTextView.setVisibility(View.GONE);
    }

    private void disableEditMode(){
        mTravelNameEditText.setText("");
        mTotalBudgetEditText.setText("");
        mDestinationEditText.setText("");
        mStartDateEditText.setText("");
        mEndDateEditText.setText("");
        mAddTravelButton.setText(R.string.button_add_travel);
        mAddTravelHeaderTextView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.bt_create_travel)
    public void AddOrEditTravel(){
        if(getCurrentFocus() != null)
            getCurrentFocus().clearFocus();

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

//        Get view model instances
        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);

//        Check form mode
        if(mEditMode){
//            Save changes to travel if form is correct
            mEditTravel.setName(mTravelNameEditText.getText().toString().trim());
            try {
                mEditTravel.setBudget(
                        new BigDecimal(TravelUtils.getCurrencyNumberFormat().parse(
                                mTotalBudgetEditText.getText().toString().trim()).toString()));
            } catch (ParseException e) {
                e.printStackTrace();
                mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_invalid));
                return;
            }
            mEditTravel.setDestination(mDestinationEditText.getText().toString().trim());
            mEditTravel.setStartDate(
                    Converters.stringToDate(mStartDateEditText.getText().toString().trim()));
            mEditTravel.setEndDate(
                    Converters.stringToDate(mEndDateEditText.getText().toString().trim()));
            travelViewModel.update(mEditTravel);
        }else{
//            Insert travel if form is correct
            try{
                Travel travel = new Travel(
                    mTravelNameEditText.getText().toString().trim(),
                    mDestinationEditText.getText().toString().trim(),
                    new BigDecimal(TravelUtils.getCurrencyNumberFormat().parse(
                            mTotalBudgetEditText.getText().toString().trim()).toString()),
                    Converters.stringToDate(mStartDateEditText.getText().toString().trim()),
                    Converters.stringToDate(mEndDateEditText.getText().toString().trim()));

                travelViewModel.insert(travel);
            }catch (ParseException e){
                e.printStackTrace();
                mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_invalid));
                return;
            }
        }

//        Toast to tell user about success
        if(mEditMode)
            Toast.makeText(this,
                    getString(R.string.toast_confirm_successful_travel_change),
                    Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,
                    getString(R.string.toast_confirm_successful_travel_creation),
                    Toast.LENGTH_LONG).show();

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(mEditTravel));
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
