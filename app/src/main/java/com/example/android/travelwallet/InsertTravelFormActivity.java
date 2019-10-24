package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.travelwallet.adapters.CountryArrayAdapter;
import com.example.android.travelwallet.adapters.CurrencyArrayAdapter;
import com.example.android.travelwallet.interfaces.AsyncTaskDelegate;
import com.example.android.travelwallet.model.Converters;
import com.example.android.travelwallet.model.GooglePlaces.Candidate;
import com.example.android.travelwallet.model.GooglePlaces.Photo;
import com.example.android.travelwallet.model.GooglePlaces.PlacesResponse;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;
import com.example.android.travelwallet.model.restcountries.CleanCountriesAsyncTask;
import com.example.android.travelwallet.model.restcountries.Country;
import com.example.android.travelwallet.model.restcountries.Currency;
import com.example.android.travelwallet.utils.CurrencyUtils;
import com.example.android.travelwallet.utils.GooglePlacesUtils;
import com.example.android.travelwallet.utils.RestCountriesUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertTravelFormActivity extends AppCompatActivity implements AsyncTaskDelegate<List<Country>> {
    public static final String KEY_INTENT_EXTRA_TRAVEL = "extra_travel";
    public static final String KEY_INTENT_EXTRA_COUNTRY_LIST = "extra_country_list";
    private static final String TAG = InsertTravelFormActivity.class.getSimpleName();
    @BindView(R.id.et_travel_name)
    EditText mTravelNameEditText;
    @BindView(R.id.et_total_budget)
    EditText mTotalBudgetEditText;
    @BindView(R.id.sp_destination)
    Spinner mDestinationSpinner;
    @BindView(R.id.sp_currency)
    Spinner mCurrencySpinner;
    @BindView(R.id.et_start_date)
    EditText mStartDateEditText;
    @BindView(R.id.et_end_date)
    EditText mEndDateEditText;
    @BindView(R.id.iv_destination_photo)
    ImageView mDestinationPhotoImageView;
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
    @State
    String mSelectedPlaceID;
    @State
    int mCurrentCurrencyPosition;
    @State
    int mCurrentDestinationPosition;
    List<Country> mCountries;
    @State
    String mCurrentGooglePlaceID;
    private Travel mEditTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_travel_form);
        ButterKnife.bind(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState != null) {
            mEditTravel = Parcels.unwrap(savedInstanceState.getParcelable(KEY_INTENT_EXTRA_TRAVEL));
            mCountries = Parcels.unwrap(savedInstanceState.getParcelable(KEY_INTENT_EXTRA_COUNTRY_LIST));
            mTravelNameEditText.setText(mCurrentTravelName);
            mTotalBudgetEditText.setText(mCurrentTravelBudget);
            populateDestinationSpinner(mCountries);
            mDestinationSpinner.setSelection(mCurrentDestinationPosition);
            populateCurrencySpinner(mCurrentDestinationPosition);
            mCurrencySpinner.setSelection(mCurrentCurrencyPosition);
            mStartDateEditText.setText(Converters.dateToString(mCurrentTravelStartDate));
            mEndDateEditText.setText(Converters.dateToString(mCurrentTravelEndDate));
            loadPlacePhotoIntoImageView(mCurrentGooglePlaceID);
        } else {
//            Query countries and load destination spinners
            RestCountriesUtils.getAllCountries().enqueue(new Callback<List<Country>>() {
                @Override
                public void onResponse(@NonNull Call<List<Country>> call, @NonNull Response<List<Country>> response) {
                    if (response.isSuccessful()) {
                        mCountries = response.body();
                        fireCleanCountriesTask();
                    } else {
                        mCountries = Collections.emptyList();
                        Log.e(TAG, "Country API returned empty list");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Country>> call, @NonNull Throwable t) {
                    mCountries = Collections.emptyList();
                    Log.e(TAG, String.format("Failed to get countries from API! %s", t.getMessage()));
                    t.printStackTrace();
                }
            });
        }

//        Listen to selections on destination spinner
        mDestinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
//              Save last clicked position for destination recovery
                mCurrentDestinationPosition = position;
                mCurrentCurrencyPosition = 0;
                populateCurrencySpinner(position);
//              Change the destination photo
                GooglePlacesUtils.findPlaceFromText(getCurrentSelectedDestination().getName()).enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PlacesResponse> call, @NonNull Response<PlacesResponse> response) {
                        if (response.isSuccessful()) {
                            try {
                                List<Candidate> candidates = response.body().getCandidates();
                                List<Photo> photos = candidates.get(0).getPhotos();
                                String photoRef = photos.get(0).getPhotoReference();
                                loadPlacePhotoIntoImageView(photoRef);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PlacesResponse> call, @NonNull Throwable t) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Listen to changes on mCurrencySpinner selection
        mCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//              Save last clicked position for currency recovery
                mCurrentCurrencyPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Create and populate calendars for date pickers
        populateCalendars();

//        Create a text listener to format currency on budget edit text
        mTotalBudgetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                Do nothing if the tv is empty
                if (mTotalBudgetEditText.getText().toString().isEmpty())
                    return;

                if (hasFocus) {
//                    If focused, remove currency number format
                    String currentNumber = mTotalBudgetEditText.getText().toString().trim();
//                    Remove the formats and divide by 100 to return the decimal places
                    double cleanNumber = Double.valueOf(currentNumber.replaceAll("\\D", "")) / 100;
                    mTotalBudgetEditText.setText(String.valueOf(cleanNumber));
                } else {
//                    If not focused, apply currency number format
                    String currentNumber = mTotalBudgetEditText.getText().toString().trim();
                    currentNumber = CurrencyUtils.FormatAsCurrencyWithoutSymbol(Double.valueOf(currentNumber));
                    mTotalBudgetEditText.setText(currentNumber);
                }
            }
        });

//        Check if there is an incoming travel object for edit mode
        Intent intent = getIntent();
        if (intent.hasExtra(KEY_INTENT_EXTRA_TRAVEL)) {
            mEditMode = true;
            mEditTravel = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_TRAVEL));
            enableEditMode(mEditTravel);
        } else {
            mEditMode = false;
            disableEditMode();
        }

//        Setup Actionbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void enableEditMode(Travel travel) {
        mTravelNameEditText.setText(travel.getName());
        mTotalBudgetEditText.setText(CurrencyUtils.FormatAsCurrencyWithoutSymbol(travel.getBudget().doubleValue()));
        mStartDateEditText.setText(Converters.dateToString(travel.getStartDate()));
        mEndDateEditText.setText(Converters.dateToString(travel.getEndDate()));
        mAddTravelButton.setText(R.string.button_edit_travel);
    }

    private void disableEditMode() {
        mTravelNameEditText.setText("");
        mTotalBudgetEditText.setText("");
        mDestinationSpinner.setSelection(0);
        mCurrencySpinner.setSelection(0);
        mStartDateEditText.setText("");
        mEndDateEditText.setText("");
        mAddTravelButton.setText(R.string.button_add_travel);
    }

    private void populateCalendars() {
//        Create calendars for date pickers
        final Calendar calendar = Calendar.getInstance();
        final Calendar minDate = Calendar.getInstance();
        final Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.YEAR, minDate.get(Calendar.YEAR) + 99);

        final DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                minDate.set(Calendar.YEAR, year);
                minDate.set(Calendar.MONTH, month);
                minDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mStartDateEditText.setText(SimpleDateFormat.getDateInstance().format(minDate.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                maxDate.set(Calendar.YEAR, year);
                maxDate.set(Calendar.MONTH, month);
                maxDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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
    }

    private void populateDestinationSpinner(List<Country> countries) {
        CountryArrayAdapter countryArrayAdapter = new CountryArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,
                countries);
        mDestinationSpinner.setAdapter(countryArrayAdapter);
        if (mEditMode) {
//        Set selection to the same as the travel being edited
            mDestinationSpinner.post(new Runnable() {
                @Override
                public void run() {
                    mDestinationSpinner.setSelection(
                            findItemPositionOnDestinationSpinner(mEditTravel.getDestination()), true);
                }
            });
        }
    }

    private int findItemPositionOnDestinationSpinner(String item) {
        for (int i = 0; i < mDestinationSpinner.getCount(); i++) {
            if (((Country) mDestinationSpinner.getItemAtPosition(i)).getName().equals(item))
                return i;
        }
        return -1;
    }

    private Country getCurrentSelectedDestination() {
        return (Country) mDestinationSpinner.getSelectedItem();
    }

    private void populateCurrencySpinner(int selectedCountryPosition) {
        List<Currency> currencies = mCountries.get(selectedCountryPosition).getCurrencies();
        CurrencyArrayAdapter currencyAdapter = new CurrencyArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,
                currencies);
        mCurrencySpinner.setAdapter(currencyAdapter);
        if (mEditMode) {
//        Set selection to the same as the travel being edited
            mCurrencySpinner.post(new Runnable() {
                public void run() {
                    mCurrencySpinner.setSelection(
                            findItemPositionOnCurrencySpinner(mEditTravel.getCurrencyCode()), true);
                }
            });
        }
    }

    private int findItemPositionOnCurrencySpinner(String item) {
        for (int i = 0; i < mCurrencySpinner.getCount(); i++) {
            if (((Currency) mCurrencySpinner.getItemAtPosition(i)).getCode().equals(item))
                return i;
        }
        return -1;
    }

    private Currency getCurrentSelectedCurrency() {
        return (Currency) mCurrencySpinner.getSelectedItem();
    }

    private void loadPlacePhotoIntoImageView(String photoRef) {
        String photoUrl = GooglePlacesUtils.getPhotoFromPhotoReference(photoRef, mDestinationPhotoImageView.getMaxWidth());
        mCurrentGooglePlaceID = photoRef;
        Glide.with(getApplicationContext())
                .load(photoUrl)
                .into(mDestinationPhotoImageView);
    }

    private boolean formHasErrors() {
        boolean flagError = false;

        if (mTravelNameEditText.getText().toString().trim().equals("")) {
            mTravelNameEditText.setError(getString(R.string.error_travel_name_required));
            flagError = true;
        }

        if (mTotalBudgetEditText.getText().toString().trim().equals("")) {
            mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_required));
            flagError = true;
        }

        if (mStartDateEditText.getText().toString().trim().equals("")) {
            mStartDateEditText.setError(getString(R.string.error_travel_start_date_required));
            flagError = true;
        }

        if (mEndDateEditText.getText().toString().trim().equals("")) {
            mEndDateEditText.setError(getString(R.string.error_travel_end_date_required));
            flagError = true;
        }

        return flagError;
    }

    @OnClick(R.id.bt_create_travel)
    public void AddOrEditTravel() {
//        Clear all focus
        if (getCurrentFocus() != null)
            getCurrentFocus().clearFocus();

//        Check if form was filled correctly
        if (formHasErrors())
            return;

//        Get view model instances
        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);

//        Create a new travel with values from the form
        Travel auxTravel = new Travel();
        auxTravel.setName(mTravelNameEditText.getText().toString().trim());
        Double budgetNumber = Double.valueOf(mTotalBudgetEditText.getText().toString().replaceAll("\\D", "")) / 100;
        BigDecimal budget = new BigDecimal(budgetNumber);
        auxTravel.setBudget(budget);
        auxTravel.setCurrencyCode(getCurrentSelectedCurrency().getCode());
        auxTravel.setDestination(getCurrentSelectedDestination().getName());
        auxTravel.setCurrencyCode(getCurrentSelectedCurrency().getCode());
        auxTravel.setStartDate(Converters.stringToDate(mStartDateEditText.getText().toString()));
        auxTravel.setEndDate(Converters.stringToDate(mEndDateEditText.getText().toString()));
        auxTravel.setGooglePlaceID(mCurrentGooglePlaceID);

//        Create or update travel based on form data
        if (mEditMode) {
            auxTravel.setId(mEditTravel.getId());
            travelViewModel.update(auxTravel);
            Toast.makeText(this, getString(R.string.toast_confirm_successful_travel_change), Toast.LENGTH_LONG).show();
        } else {
            travelViewModel.insert(auxTravel);
            Toast.makeText(this, getString(R.string.toast_confirm_successful_travel_creation), Toast.LENGTH_LONG).show();
        }

//        Return to main activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedID = item.getItemId();
        switch (selectedID) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(mEditTravel));
        outState.putParcelable(KEY_INTENT_EXTRA_COUNTRY_LIST, Parcels.wrap(mCountries));
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void fireCleanCountriesTask() {
        new CleanCountriesAsyncTask(this).execute(mCountries);
    }

    @Override
    public void processFinish(List<Country> output) {
        mCountries = output;
        populateDestinationSpinner(mCountries);
    }
}
