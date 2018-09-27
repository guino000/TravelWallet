package com.example.android.travelwallet;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.travelwallet.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InsertExpenseFormActivity extends AppCompatActivity {
    @BindView(R.id.et_expense_description)
    EditText mExpenseDescriptionEditText;
    @BindView(R.id.et_expense_amount)
    EditText mExpenseAmountEditText;
    @BindView(R.id.sp_category)
    Spinner mExpenseCategorySpinner;
    @BindView(R.id.et_expense_date)
    EditText mExpenseDateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_expense_form);
        ButterKnife.bind(this);

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

//        Configure back button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
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
