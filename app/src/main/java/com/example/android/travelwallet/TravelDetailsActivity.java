package com.example.android.travelwallet;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.travelwallet.adapters.DailyExpensesAdapter;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.utils.CurrencyUtils;
import com.example.android.travelwallet.utils.GooglePlacesUtils;
import com.example.android.travelwallet.utils.TravelUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TravelDetailsActivity extends AppCompatActivity{
    public static final String KEY_INTENT_EXTRA_TRAVEL = "extra_travel";

    @BindView(R.id.pb_budget_circular_gauge)
    ProgressBar mPieChartBudget;
    @BindView(R.id.rv_detail_expenses)
    RecyclerView mExpensesRecyclerView;
    @BindView(R.id.fab_add_expense)
    FloatingActionButton mAddExpenseFAB;
    @BindView(R.id.iv_travel_details_header)
    ImageView mTravelDetailsHeaderImageView;
    @BindView(R.id.travel_details_collapsible_toolbar)
    CollapsingToolbarLayout mTravelDetailsCT;
    @BindView(R.id.tvBudgetProgressData)
    TextView mBudgetProgressData;
    @BindView(R.id.tvOverviewTotalBudget)
    TextView mTotalBudgetTextView;
    @BindView(R.id.tvOverviewTotalSpent)
    TextView mTotalSpentTextView;

    DailyExpensesAdapter mExpenseAdapter;
    ExpenseViewModel mExpenseViewModel;

    private long mTravelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_details);
        ButterKnife.bind(this);

//        Get incoming intent
        Intent intent = getIntent();
        final Travel travel = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_TRAVEL));
        mTravelID = travel.getId();

//        Configure Recycler View
        mExpenseAdapter = new DailyExpensesAdapter(this, mTravelID);
        mExpensesRecyclerView.setAdapter(mExpenseAdapter);
        mExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL,
                false
        ));
        mExpensesRecyclerView.setHasFixedSize(true);

//        Get LiveData for expenses
        mExpenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(ExpenseViewModel.class);
        mExpenseViewModel.getAllDates(travel.getId()).observe(this, new Observer<List<Date>>() {
            @Override
            public void onChanged(@Nullable List<Date> dates) {
                mExpenseAdapter.setData(dates);
                updateBudgetOverview(travel);
            }
        });

//        Configure Activity
        updateBudgetOverview(travel);
        Glide.with(this)
                .load(GooglePlacesUtils.getPhotoFromPhotoReference(travel.getGooglePlaceID(), mTravelDetailsHeaderImageView.getMaxWidth()))
                .into(mTravelDetailsHeaderImageView);
        mTravelDetailsCT.setTitle(travel.getName());
        setSupportActionBar((Toolbar) findViewById(R.id.travel_details_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.fab_add_expense)
    public void showAddExpenseForm(){
        Intent intent = new Intent(this, InsertExpenseFormActivity.class);
        intent.putExtra(InsertExpenseFormActivity.KEY_INTENT_EXTRA_TRAVEL_ID, mTravelID);
        startActivity(intent);
    }

//    Update budget overview components: Progress bar and text boxes
    public void updateBudgetOverview(Travel travel){
        // Get Budget Data
        BigDecimal totalExpenses = TravelUtils.getTravelExpensesTotal(getApplication(), travel);

        // Update Budget Chart
        float spentPercent = TravelUtils.getBudgetSpentPercentage(getApplication(), travel);
        DecimalFormat percentFormat = new DecimalFormat("##%");
        String progressText = percentFormat.format(spentPercent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int progress = Math.round(spentPercent * 100);
            ObjectAnimator animator = ObjectAnimator.ofInt(mPieChartBudget, "progress", mPieChartBudget.getProgress(), progress*100);
            animator.setDuration(500);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } else {
            mPieChartBudget.setProgress(Math.round(spentPercent * 100));
        }
        mBudgetProgressData.setText(progressText);

        //Update Budget Overview Text
        mTotalSpentTextView.setText(CurrencyUtils.getCurrencyFormattedValue(totalExpenses, travel.getCurrencyCode()));
        mTotalBudgetTextView.setText(CurrencyUtils.getCurrencyFormattedValue(travel.getBudget(), travel.getCurrencyCode()));

        //Update Budget Overview color coding
        if (totalExpenses.compareTo(travel.getBudget()) > 0) {
            mTotalBudgetTextView.setTextColor(getResources().getColor(R.color.red));
            mTotalSpentTextView.setTextColor(getResources().getColor(R.color.red));
            mPieChartBudget.setProgressTintList(getResources().getColorStateList(R.color.red));
        } else {
            mTotalBudgetTextView.setTextColor(getResources().getColor(R.color.progress_green));
            mTotalSpentTextView.setTextColor(getResources().getColor(R.color.progress_green));
            mPieChartBudget.setProgressTintList(getResources().getColorStateList(R.color.progress_green));
        }
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
