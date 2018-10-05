package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.Converters;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.model.TravelViewModel;
import com.example.android.travelwallet.utils.TravelUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private long mTravelID;
    private TravelExpense mEditExpense;
    private boolean mEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_expense_form);
        ButterKnife.bind(this);

//        Get incoming intent
        Intent intent = getIntent();
        mTravelID = intent.getLongExtra(KEY_INTENT_EXTRA_TRAVEL_ID, 0);

//        Configure Calendar Date picker
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mExpenseDateEditText.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
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

//        Load data into category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories_array, R.layout.support_simple_spinner_dropdown_item);
        mExpenseCategorySpinner.setAdapter(adapter);

//        Create a text listener to format currency on budget edit text
        mExpenseAmountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                NumberFormat format = TravelUtils.getCurrencyNumberFormat();
                if(hasFocus){
                    try {
                        mExpenseAmountEditText.setText(
                                format.parse(mExpenseAmountEditText.getText().toString().trim()).toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mExpenseAmountEditText.setText("");
                    }
                }else{
                    if(!mExpenseAmountEditText.getText().toString().trim().equals("")) {
                        try {
                            mExpenseAmountEditText.setText(TravelUtils.getCurrencyFormattedValue(
                                    new BigDecimal((mExpenseAmountEditText.getText().toString().trim()))
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                            mExpenseAmountEditText.setText("");
                            mExpenseAmountEditText.setError(getString(R.string.error_expense_amount_invalid));
                        }
                    }
                }
            }
        });

//        Check if there is an incoming travel object for edit mode
        if(intent.hasExtra(KEY_INTENT_EXTRA_EXPENSE_EDIT)){
            mEditMode = true;
            mEditExpense = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_EXPENSE_EDIT));
            enableEditMode(mEditExpense);
        }else{
            mEditMode = false;
            disableEditMode();
        }

//        Configure back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void enableEditMode(TravelExpense expense){
        mExpenseDescriptionEditText.setText(expense.getExpenseDescription());
        mExpenseAmountEditText.setText(TravelUtils.getCurrencyFormattedValue(expense.getExpenseTotal()));
        int categoryPosition = findItemPositionOnCategorySpinner(expense.getCategory());
        if(categoryPosition > 0)
            mExpenseCategorySpinner.setSelection(categoryPosition);
        else
            mExpenseCategorySpinner.setSelection(0);
        mExpenseDateEditText.setText(Converters.dateToString(expense.getExpenseDate()));
        mCreateExpenseButton.setText(getString(R.string.edit_expense_button_label));
    }

    private void disableEditMode(){
        mExpenseDescriptionEditText.setText("");
        mExpenseAmountEditText.setText("");
        mExpenseCategorySpinner.setSelection(0);
        mExpenseDateEditText.setText("");
        mCreateExpenseButton.setText(getString(R.string.insert_expense_button_label));
    }

    private int findItemPositionOnCategorySpinner(String item){
        for(int i = 0; i < mExpenseCategorySpinner.getCount(); i++){
            if(mExpenseCategorySpinner.getItemAtPosition(i).equals(item))
                return i;
        }
        return -1;
    }

    @OnClick(R.id.bt_create_expense)
    public void createExpense(){
        if(getCurrentFocus() != null)
            getCurrentFocus().clearFocus();
//        Check if inputs are OK
        boolean flagError = false;
        if(mExpenseDescriptionEditText.getText().toString().trim().equals("")){
            mExpenseDescriptionEditText.setError(getString(R.string.error_expense_description_required));
            flagError = true;
        }
        if(mExpenseAmountEditText.getText().toString().trim().equals("")){
            mExpenseAmountEditText.setError(getString(R.string.error_expense_amount_required));
            flagError = true;
        }
        if(mExpenseDateEditText.getText().toString().trim().equals("")){
            mExpenseDateEditText.setError(getString(R.string.error_expense_date_required));
            flagError = true;
        }

        if(flagError) return;

//        Get expense ViewModel instances
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(ExpenseViewModel.class);

        TravelViewModel travelViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(TravelViewModel.class);

//        Check form mode
        if(mEditMode){
//            Save changes to expense if form is correct
            mEditExpense.setExpenseDescription(mExpenseDescriptionEditText.getText().toString().trim());
            try {
                mEditExpense.setExpenseTotal(
                        new BigDecimal(TravelUtils.getCurrencyNumberFormat().parse(
                                mExpenseAmountEditText.getText().toString().trim()).toString()));
            }catch (ParseException e){
                e.printStackTrace();
                mExpenseAmountEditText.setError(getString(R.string.error_expense_amount_invalid));
                return;
            }

            expenseViewModel.update(mEditExpense);
            Travel travel = travelViewModel.getTravel(mEditExpense.getTravelID());
            travelViewModel.update(travel);
        }else{
//        Insert expense if form is correct
            try {
                TravelExpense expense = new TravelExpense(
                        mExpenseDescriptionEditText.getText().toString().trim(),
                        new BigDecimal(TravelUtils.getCurrencyNumberFormat().parse(
                                mExpenseAmountEditText.getText().toString().trim()).toString()),
                        mExpenseCategorySpinner.getSelectedItem().toString().trim(),
                        Converters.stringToDate(mExpenseDateEditText.getText().toString().trim()),
                        mTravelID);

                expenseViewModel.insert(expense);
                Travel travel = travelViewModel.getTravel(expense.getTravelID());
                travelViewModel.update(travel);
            }catch (ParseException e){
                e.printStackTrace();
                mExpenseAmountEditText.setError(getString(R.string.error_expense_amount_invalid));
                return;
            }
        }

//        Toast to tell user about success
        if(mEditMode)
            Toast.makeText(this,
                    getString(R.string.toast_confirm_successful_expense_change),
                    Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,
                    getString(R.string.toast_confirm_successful_expense_creation),
                    Toast.LENGTH_LONG).show();

//        Return to previous activity
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
