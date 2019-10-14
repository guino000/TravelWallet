package com.example.android.travelwallet;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.charts.CircularGauge;
import com.bumptech.glide.Glide;
import com.example.android.travelwallet.adapters.ExpenseAdapter;
import com.example.android.travelwallet.interfaces.CardPopupMenuListener;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.model.TravelValues;
import com.example.android.travelwallet.utils.GooglePlacesUtils;
import com.example.android.travelwallet.utils.TravelUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TravelDetailsActivity extends AppCompatActivity
    implements CardPopupMenuListener {
    public static final String KEY_INTENT_EXTRA_TRAVEL = "extra_travel";

    @BindView(R.id.budget_circular_gauge)
    AnyChartView mPieChartBudget;
    @BindView(R.id.rv_detail_expenses)
    RecyclerView mExpensesRecyclerView;
    @BindView(R.id.fab_add_expense)
    FloatingActionButton mAddExpenseFAB;
    @BindView(R.id.iv_travel_details_header)
    ImageView mTravelDetailsHeaderImageView;
    @BindView(R.id.travel_details_collapsible_toolbar)
    CollapsingToolbarLayout mTravelDetailsCT;

    ExpenseAdapter mExpenseAdapter;
    ExpenseViewModel mExpenseViewModel;

    private long mTravelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_details);
        ButterKnife.bind(this);

//        Configure Recycler View
        mExpenseAdapter = new ExpenseAdapter(this, this);
        mExpensesRecyclerView.setAdapter(mExpenseAdapter);
        mExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL,
                false
        ));
        mExpensesRecyclerView.setHasFixedSize(true);

//        Get incoming intent
        Intent intent = getIntent();
        final Travel travel = Parcels.unwrap(intent.getParcelableExtra(KEY_INTENT_EXTRA_TRAVEL));
        mTravelID = travel.getId();

//        Get LiveData for expenses
        mExpenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(ExpenseViewModel.class);
        mExpenseViewModel.getAllExpensesOfTravel(travel.getId()).observe(this, new Observer<List<TravelExpense>>() {
            @Override
            public void onChanged(@Nullable List<TravelExpense> travelExpenses) {
                mExpenseAdapter.setData(travelExpenses);
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
        ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(ExpenseViewModel.class);

        TravelValues totalExpenses = expenseViewModel.getTotalExpensesOfTravel(travel.getId());
        if(totalExpenses.getTotal() == null){
            totalExpenses = new TravelValues();
            totalExpenses.setTotal(new BigDecimal(0));
        }

        float spentPercent = TravelUtils.getBudgetSpentPercentage(getApplication(), travel);
        int remainingPercent = (int) Math.abs(100 - (spentPercent * 100));

        // Configure pie chart
        CircularGauge circularGauge = AnyChart.circular();
        circularGauge.data(new SingleValueDataSet(new String[]{String.valueOf(remainingPercent)}));

        mPieChartBudget.setChart(circularGauge);
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

    public void startExpenseFormActivityAsEditMode(TravelExpense expense){
        Intent intent = new Intent(this, InsertExpenseFormActivity.class);
        intent.putExtra(InsertExpenseFormActivity.KEY_INTENT_EXTRA_TRAVEL_ID, mTravelID);
        intent.putExtra(InsertExpenseFormActivity.KEY_INTENT_EXTRA_EXPENSE_EDIT, Parcels.wrap(expense));
        startActivity(intent);
    }

    @Override
    public void onPopupMenuClick(View view, final int pos) {
        //        Create Card popup menu
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.expense_miniature_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_delete :
                        mExpenseViewModel.delete(mExpenseAdapter.getData().get(pos));
                        return true;
                    case R.id.item_edit :
                        startExpenseFormActivityAsEditMode(mExpenseAdapter.getData().get(pos));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}
