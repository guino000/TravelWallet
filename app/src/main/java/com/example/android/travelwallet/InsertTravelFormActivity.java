package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Placeholder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;
import com.example.android.travelwallet.utils.TravelUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private boolean mEditMode;
    private Travel mEditTravel;
    private String mSelectedPlaceID;

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
        mStartDateEditText.setText(travel.getStartDate());
        mEndDateEditText.setText(travel.getEndDate());
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

    @OnClick(R.id.bt_select_place_map)
    public void startPlacePickerActivity(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    getString(R.string.error_google_play_services_not_available),
                    Toast.LENGTH_LONG).show();
        }

        mDestinationEditText.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case PLACE_PICKER_REQUEST :
                if(data != null && resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    mDestinationEditText.setText(place.getName());
                    mSelectedPlaceID = place.getId();
                }else{
                    mDestinationEditText.setText("");
                    mSelectedPlaceID = "";
                }
                break;
            default:
                throw new UnsupportedOperationException("Not implemented yet!");
        }
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
                                mTotalBudgetEditText.getText().toString().trim()).doubleValue()));
            } catch (ParseException e) {
                e.printStackTrace();
                mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_invalid));
                return;
            }
            mEditTravel.setDestination(mDestinationEditText.getText().toString().trim());
            mEditTravel.setStartDate(mStartDateEditText.getText().toString().trim());
            mEditTravel.setEndDate(mEndDateEditText.getText().toString().trim());
            mEditTravel.setPlaceID(mSelectedPlaceID);
            travelViewModel.update(mEditTravel);
        }else{
//            Insert travel if form is correct
            try{
                Travel travel = new Travel(
                    mTravelNameEditText.getText().toString().trim(),
                    mDestinationEditText.getText().toString().trim(),
                    new BigDecimal(TravelUtils.getCurrencyNumberFormat().parse(
                            mTotalBudgetEditText.getText().toString().trim()).doubleValue()),
                    mStartDateEditText.getText().toString().trim(),
                    mEndDateEditText.getText().toString().trim(),
                    mSelectedPlaceID);

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
}
