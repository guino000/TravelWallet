package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.android.travelwallet.utils.GooglePlacesUtils;
import com.example.android.travelwallet.utils.RestCountriesUtils;
import com.example.android.travelwallet.utils.TravelUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertTravelFormActivity extends AppCompatActivity implements AsyncTaskDelegate<List<Country>> {
    private static final String TAG = InsertTravelFormActivity.class.getSimpleName();
    public static final String KEY_INTENT_EXTRA_TRAVEL = "extra_travel";
    public static final String KEY_INTENT_EXTRA_COUNTRY_LIST = "extra_country_list";
    public static final int PLACE_PICKER_REQUEST = 1;

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
    private Travel mEditTravel;
    @State
    String mSelectedPlaceID;
    @State
    int mCurrentCurrencyPosition;
    @State
    int mCurrentDestinationPosition;
    List<Country> mCountries;
    @State
    String mCurrentGooglePlaceID;

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
                public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                    if (response.isSuccessful()) {
                        mCountries = response.body();
                        fireCleanCountriesTask();
                    } else {
                        mCountries = Collections.emptyList();
                        Log.e(TAG, "Country API returned empty list");
                    }
                }

                @Override
                public void onFailure(Call<List<Country>> call, Throwable t) {
                    mCountries = Collections.emptyList();
                    Log.e(TAG, String.format("Failed to get countries from API! %s",t.getMessage()));
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
//              Change the current budget currency format
                updateCurrentBudgetCurrency(0);
//              Change the destination photo
                GooglePlacesUtils.findPlaceFromText(getCurrentSelectedDestination().getName()).enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
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
                    public void onFailure(Call<PlacesResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//              Save last clicked position for currency recovery
                mCurrentCurrencyPosition = position;
//              Change the current budget currency format
                updateCurrentBudgetCurrency(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Create calendars for date pickers
        final Calendar calendar = Calendar.getInstance();
        final Calendar minDate = Calendar.getInstance();
        final Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.YEAR, minDate.get(Calendar.YEAR) + 99);

//        Create dateSetListeners for date picker
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

//        Create a text listener to format currency on budget edit text
        mTotalBudgetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                NumberFormat format = TravelUtils.getCurrencyNumberFormat(getCurrentSelectedCurrency().getCode());
                if (hasFocus) {
                    try {
                        mTotalBudgetEditText.setText(
                                format.parse(mTotalBudgetEditText.getText().toString().trim()).toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mTotalBudgetEditText.setText("");
                    }
                } else {
                    if (!mTotalBudgetEditText.getText().toString().trim().equals("")) {
                        try {
                            mTotalBudgetEditText.setText(TravelUtils.getCurrencyFormattedValue(
                                    new BigDecimal((mTotalBudgetEditText.getText().toString().trim())),
                                    getCurrentSelectedCurrency().getCode()
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
        if (intent.hasExtra(KEY_INTENT_EXTRA_TRAVEL)) {
            mEditMode = true;
            mEditTravel = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_TRAVEL));
            enableEditMode(mEditTravel);
        } else {
            mEditMode = false;
            disableEditMode();
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void updateCurrentBudgetCurrency(int currencySpinnerPosition) {
        if (!mTotalBudgetEditText.getText().toString().isEmpty()) {
//            Get new currency code
            Currency currentCurrency = (Currency) mCurrencySpinner.getItemAtPosition(currencySpinnerPosition);
            try {
//                Extract numbers from current budget
                Pattern pattern = Pattern.compile("[\\d\\.\\,]+");
                Matcher matcher = pattern.matcher(mTotalBudgetEditText.getText().toString());
                if (matcher.find()) {
//                    Set new currency on budget
                    String number = matcher.group(0);
                    BigDecimal currentBudget = new BigDecimal(number);
                    mTotalBudgetEditText.setText(TravelUtils.getCurrencyFormattedValue(
                            currentBudget, currentCurrency.getCode()));
                } else {
                    mTotalBudgetEditText.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mTotalBudgetEditText.setText("");
            }
        }
    }

    private void enableEditMode(Travel travel) {
        mTravelNameEditText.setText(travel.getName());
        mTotalBudgetEditText.setText(TravelUtils.getCurrencyFormattedValue(travel.getBudget(), travel.getCurrencyCode()));
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

    @OnClick(R.id.bt_create_travel)
    public void AddOrEditTravel() {
        if (getCurrentFocus() != null)
            getCurrentFocus().clearFocus();

//        Check if form was filled correctly
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

        if (flagError) return;

//        Get view model instances
        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);

//        Check form mode
        if (mEditMode) {
//            Save changes to travel if form is correct
            mEditTravel.setName(mTravelNameEditText.getText().toString().trim());
            try {
                mEditTravel.setBudget(
                        new BigDecimal(TravelUtils.getCurrencyNumberFormat(getCurrentSelectedCurrency().getCode()).parse(
                                mTotalBudgetEditText.getText().toString().trim()).toString()));
            } catch (ParseException e) {
                e.printStackTrace();
                mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_invalid));
                return;
            }

            mEditTravel.setDestination(getCurrentSelectedDestination().getName());
            mEditTravel.setCurrencyCode(getCurrentSelectedCurrency().getCode());
            mEditTravel.setStartDate(
                    Converters.stringToDate(mStartDateEditText.getText().toString().trim()));
            mEditTravel.setEndDate(
                    Converters.stringToDate(mEndDateEditText.getText().toString().trim()));
            travelViewModel.update(mEditTravel);
            mEditTravel.setGooglePlaceID(mCurrentGooglePlaceID);
        } else {
//            Insert travel if form is correct
            try {
                Travel travel = new Travel(
                        mTravelNameEditText.getText().toString().trim(),
                        getCurrentSelectedDestination().getName(),
                        getCurrentSelectedCurrency().getCode(),
                        new BigDecimal(TravelUtils.getCurrencyNumberFormat(getCurrentSelectedCurrency().getCode()).parse(
                                mTotalBudgetEditText.getText().toString().trim()).toString()),
                        Converters.stringToDate(mStartDateEditText.getText().toString().trim()),
                        Converters.stringToDate(mEndDateEditText.getText().toString().trim()));
                travel.setGooglePlaceID(mCurrentGooglePlaceID);

                travelViewModel.insert(travel);
            } catch (ParseException e) {
                e.printStackTrace();
                mTotalBudgetEditText.setError(getString(R.string.error_travel_budget_invalid));
                return;
            }
        }

//        Toast to tell user about success
        if (mEditMode)
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

    private void fireCleanCountriesTask(){
        new CleanCountriesAsyncTask(this).execute(mCountries);
    }

    @Override
    public void processFinish(List<Country> output) {
        mCountries = output;
        populateDestinationSpinner(mCountries);
    }
}
