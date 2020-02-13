package com.example.android.travelwallet.adapters;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.utils.CurrencyUtils;
import com.example.android.travelwallet.utils.TravelUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyExpensesAdapter extends RecyclerView.Adapter<DailyExpensesAdapter.DailyExpenseViewHolder> {
    private final Context mContext;
    private long mTravelID;
    private List<Date> mExpenseDates;
    private RecyclerView.RecycledViewPool mViewPool;

    public DailyExpensesAdapter(Context context, long travel){
        mContext = context;
        mTravelID = travel;
        mViewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public DailyExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.travel_expenses_daily_group,viewGroup,false);

        DailyExpenseViewHolder dailyExpenseViewHolder = new DailyExpenseViewHolder(itemView);
        dailyExpenseViewHolder.mExpensesGroupRecyclerView.setRecycledViewPool(mViewPool);

        return dailyExpenseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DailyExpenseViewHolder dailyExpenseViewHolder, int i) {
        Date date = mExpenseDates.get(i);

//        Get total expenses of date
        BigDecimal expensesOfDate = TravelUtils.getTotalExpensesOfDate((Application) mContext.getApplicationContext(),date, mTravelID);

//        Get travel
        Travel travel = TravelUtils.getCurrentTravel(mTravelID, (Application) mContext.getApplicationContext());

//        Set date text and total amount of day
        dailyExpenseViewHolder.mGroupDateTextView.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
        dailyExpenseViewHolder.mGroupTotalTextView.setText(CurrencyUtils.getCurrencyFormattedValue(expensesOfDate, travel.getCurrencyCode()));

//        Configure adapter and recycler view
        ExpenseAdapter expenseAdapter = new ExpenseAdapter(mContext);
        expenseAdapter.setHasStableIds(true);
        dailyExpenseViewHolder.mExpensesGroupRecyclerView.setAdapter(expenseAdapter);
        dailyExpenseViewHolder.mExpensesGroupRecyclerView.setLayoutManager(new LinearLayoutManager(
                dailyExpenseViewHolder.mExpensesGroupRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        dailyExpenseViewHolder.mExpensesGroupRecyclerView.setHasFixedSize(true);

        dailyExpenseViewHolder.setDate(date);
    }

    @Override
    public int getItemCount() {
        if(mExpenseDates == null)
            return 0;
        return mExpenseDates.size();
    }

    public List<Date> getData(){
        return mExpenseDates;
    }

    public void setData(List<Date> newData){
        mExpenseDates = newData;
        notifyDataSetChanged();
    }

    class DailyExpenseViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_group_date)
        TextView mGroupDateTextView;
        @BindView(R.id.tv_group_total)
        TextView mGroupTotalTextView;
        @BindView(R.id.rv_expenses_group)
        RecyclerView mExpensesGroupRecyclerView;
        private Date mDate;

        public DailyExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDate(Date date){
            mDate = date;
//        Instantiate view model
            ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance((Application) mContext.getApplicationContext())
                    .create(ExpenseViewModel.class);
//        Observe changes to expenses
            expenseViewModel.getExpensesOfDate(mDate, mTravelID).observe((AppCompatActivity) mContext, new Observer<List<TravelExpense>>() {
                @Override
                public void onChanged(@Nullable List<TravelExpense> travelExpenses) {
                    ((ExpenseAdapter) mExpensesGroupRecyclerView.getAdapter()).setData(travelExpenses);
                }
            });
        }
    }
}
