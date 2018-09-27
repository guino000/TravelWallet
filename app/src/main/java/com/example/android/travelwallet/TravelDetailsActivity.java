package com.example.android.travelwallet;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.travelwallet.adapters.ExpenseAdapter;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.model.TravelValues;
import com.example.android.travelwallet.model.TravelViewModel;
import com.example.android.travelwallet.utils.TravelUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TravelDetailsActivity extends AppCompatActivity {
    public static final String KEY_INTENT_EXTRA_TRAVEL = "extra_travel";

    @BindView(R.id.tv_detail_travel_name)
    TextView mTravelNameTextView;
    @BindView(R.id.tv_detail_total_expenses)
    TextView mTotalExpensesTextView;
    @BindView(R.id.tv_detail_total_budget)
    TextView mTotalBudgetTextView;
    @BindView(R.id.pb_budget_spent)
    ProgressBar mBudgetSpentProgressBar;
    @BindView(R.id.rv_detail_expenses)
    RecyclerView mExpensesRecyclerView;
    @BindView(R.id.fab_add_expense)
    FloatingActionButton mAddExpenseFAB;

    ExpenseAdapter mExpenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_details);
        ButterKnife.bind(this);

//        Configure Recycler View
        mExpenseAdapter = new ExpenseAdapter(this);
        mExpensesRecyclerView.setAdapter(mExpenseAdapter);
        mExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL,
                false
        ));
        mExpensesRecyclerView.setHasFixedSize(true);

//        Get incoming intent
        Intent intent = getIntent();
        Travel travel = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_TRAVEL));

//        Get LiveData for expenses
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(ExpenseViewModel.class);
        expenseViewModel.getAllExpensesOfTravel(travel.getId()).observe(this, new Observer<List<TravelExpense>>() {
            @Override
            public void onChanged(@Nullable List<TravelExpense> travelExpenses) {
                mExpenseAdapter.setData(travelExpenses);
            }
        });

//        Configure Activity
        mTravelNameTextView.setText(travel.getName());
        mTotalBudgetTextView.setText(TravelUtils.getCurrencyFormattedValue(travel.getBudget()));
        TravelValues totalExpenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());
        if(totalExpenses != null) {
                mTotalExpensesTextView.setText(
                        TravelUtils.getCurrencyFormattedValue(totalExpenses.total));
        }else{
            mTotalExpensesTextView.setText(TravelUtils.getCurrencyFormattedValue(new BigDecimal(0)));
        }
        mBudgetSpentProgressBar.setProgress(TravelUtils.getBudgetSpentPercentage(getApplication(),travel));

//        Configure Back Button
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.fab_add_expense)
    public void showAddExpenseForm(){
        Intent intent = new Intent(this, InsertExpenseFormActivity.class);
        startActivity(intent);
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
