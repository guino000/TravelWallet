package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.travelwallet.model.Converters;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.model.TravelViewModel;
import com.example.android.travelwallet.utils.CurrencyUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class InsertExpenseFormActivity extends AppCompatActivity {
    public static final String KEY_INTENT_EXTRA_TRAVEL_ID = "extra_travel_id";
    public static final String KEY_INTENT_EXTRA_EXPENSE_EDIT = "extra_travel_edit";

    @BindView(R.id.et_expense_description)
    EditText mExpenseDescriptionEditText;
    @BindView(R.id.et_expense_amount)
    EditText mExpenseAmountEditText;
    @BindView(R.id.sp_category)
    Spinner mExpenseCategorySpinner;
    @BindView(R.id.et_expense_date)
    EditText mExpenseDateEditText;
    @BindView(R.id.bt_create_expense)
    Button mCreateExpenseButton;

    @State
    String mCurrentExpenseDescr;
    @State
    String mCurrentExpenseAmount;
    @State
    int mCurrentSelectedCategory;
    @State
    Date mCurrentSelectedDate;

    @State
    long mTravelID;
    private TravelExpense mEditExpense;
    @State
    boolean mEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_expense_form);
        ButterKnife.bind(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState != null) {
            mEditExpense = Parcels.unwrap(savedInstanceState.getParcelable(KEY_INTENT_EXTRA_EXPENSE_EDIT));
            mExpenseDescriptionEditText.setText(mCurrentExpenseDescr);
            mExpenseAmountEditText.setText(mCurrentExpenseAmount);
            mExpenseCategorySpinner.setSelection(mCurrentSelectedCategory);
            mExpenseDateEditText.setText(Converters.dateToString(mCurrentSelectedDate));
        }

//        Populate date pickers
        populateCalendars();

//        Get incoming intent
        Intent intent = getIntent();
        mTravelID = intent.getLongExtra(KEY_INTENT_EXTRA_TRAVEL_ID, 0);

//        Load data into category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories_array, R.layout.support_simple_spinner_dropdown_item);
        mExpenseCategorySpinner.setAdapter(adapter);

//        Create a text listener to format currency on budget edit text
        mExpenseAmountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                Do nothing if the tv is empty
                if (mExpenseAmountEditText.getText().toString().isEmpty())
                    return;

                if (hasFocus) {
//                    If focused, remove currency number format
                    String currentNumber = mExpenseAmountEditText.getText().toString().trim();
//                    Remove the formats and divide by 100 to return the decimal places
                    double cleanNumber = Double.valueOf(currentNumber.replaceAll("\\D", "")) / 100;
                    mExpenseAmountEditText.setText(String.valueOf(cleanNumber));
                } else {
//                    If not focused, apply currency number format
                    String currentNumber = mExpenseAmountEditText.getText().toString().trim();
                    currentNumber = CurrencyUtils.FormatAsCurrencyWithoutSymbol(Double.valueOf(currentNumber));
                    mExpenseAmountEditText.setText(currentNumber);
                }
            }
        });

//        Check if there is an incoming expense object for edit mode
        if (intent.hasExtra(KEY_INTENT_EXTRA_EXPENSE_EDIT)) {
            mEditMode = true;
            mEditExpense = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_EXPENSE_EDIT));
            enableEditMode(mEditExpense);
        } else {
            mEditMode = false;
            disableEditMode();
        }

//        Configure back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void populateCalendars() {
//        Configure Calendar Date picker
        final Calendar calendar = Calendar.getInstance();
        final Calendar selectedDate = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mExpenseDateEditText.setText(Converters.dateToString(selectedDate.getTime()));
            }
        };

        mExpenseDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(InsertExpenseFormActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mExpenseDateEditText.setText("");
                    }
                });

                datePickerDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mEditMode) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void enableEditMode(TravelExpense expense) {
        mExpenseDescriptionEditText.setText(expense.getExpenseDescription());
        mExpenseAmountEditText.setText(CurrencyUtils.FormatAsCurrencyWithoutSymbol(expense.getExpenseTotal().doubleValue()));
        int categoryPosition = findItemPositionOnCategorySpinner(expense.getCategory());
        if (categoryPosition > 0)
            mExpenseCategorySpinner.setSelection(categoryPosition);
        else
            mExpenseCategorySpinner.setSelection(0);
        mExpenseDateEditText.setText(Converters.dateToString(expense.getExpenseDate()));
        mCreateExpenseButton.setText(getString(R.string.edit_expense_button_label));
    }

    private void disableEditMode() {
        mExpenseDescriptionEditText.setText("");
        mExpenseAmountEditText.setText("");
        mExpenseCategorySpinner.setSelection(0);
        mExpenseDateEditText.setText("");
        mCreateExpenseButton.setText(getString(R.string.insert_expense_button_label));
    }

    private int findItemPositionOnCategorySpinner(String item) {
        for (int i = 0; i < mExpenseCategorySpinner.getCount(); i++) {
            if (mExpenseCategorySpinner.getItemAtPosition(i).equals(item))
                return i;
        }
        return -1;
    }

    private boolean formHasErrors() {
        //        Check if inputs are OK
        boolean flagError = false;
        if (mExpenseDescriptionEditText.getText().toString().trim().equals("")) {
            mExpenseDescriptionEditText.setError(getString(R.string.error_expense_description_required));
            flagError = true;
        }
        if (mExpenseAmountEditText.getText().toString().trim().equals("")) {
            mExpenseAmountEditText.setError(getString(R.string.error_expense_amount_required));
            flagError = true;
        }
        if (mExpenseDateEditText.getText().toString().trim().equals("")) {
            mExpenseDateEditText.setError(getString(R.string.error_expense_date_required));
            flagError = true;
        }

        return flagError;
    }

    @OnClick(R.id.bt_create_expense)
    public void createExpense() {
        if (getCurrentFocus() != null)
            getCurrentFocus().clearFocus();

        if (formHasErrors())
            return;

//        Get expense ViewModel instances
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(ExpenseViewModel.class);

        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(TravelViewModel.class);

        TravelExpense auxExpense = new TravelExpense();
        auxExpense.setCategory(mExpenseCategorySpinner.getSelectedItem().toString());
        auxExpense.setExpenseDescription(mExpenseDescriptionEditText.getText().toString());
        auxExpense.setExpenseDate(Converters.stringToDate(mExpenseDateEditText.getText().toString()));
        auxExpense.setTravelID(mTravelID);
        Double expenseNumber = Double.valueOf(mExpenseAmountEditText.getText().toString().replaceAll("\\D", "")) / 100;
        BigDecimal expenseAmount = new BigDecimal(expenseNumber);
        auxExpense.setExpenseTotal(expenseAmount);

//        Insert or edit expense
        if (mEditMode) {
            auxExpense.setID(mEditExpense.getID());
            expenseViewModel.update(auxExpense);
            Toast.makeText(this,
                    getString(R.string.toast_confirm_successful_expense_change),
                    Toast.LENGTH_LONG).show();
        } else {
            expenseViewModel.insert(auxExpense);
            Toast.makeText(this,
                    getString(R.string.toast_confirm_successful_expense_creation),
                    Toast.LENGTH_LONG).show();
        }

//        Update travel to trigger an update on observers
        Travel travel = travelViewModel.getTravel(mTravelID);
        travelViewModel.update(travel);

//        Return to previous activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedID = item.getItemId();
        switch (selectedID) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_delete:
                OnDeletePressed();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return super.onOptionsItemSelected(item);
    }

    private void OnDeletePressed(){
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(ExpenseViewModel.class);
        expenseViewModel.delete(mEditExpense);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_INTENT_EXTRA_EXPENSE_EDIT, Parcels.wrap(mEditExpense));
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
