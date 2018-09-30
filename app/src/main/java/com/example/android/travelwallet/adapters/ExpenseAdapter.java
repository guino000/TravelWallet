package com.example.android.travelwallet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.utils.TravelUtils;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private Context mContext;
    private List<TravelExpense> mExpenses;

    public ExpenseAdapter(Context context){
        mContext = context;
    }

    public void setData(List<TravelExpense> newData){
        mExpenses = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.travel_expense_miniature, viewGroup, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder expenseViewHolder, int i) {
        TravelExpense expense = mExpenses.get(i);
        expenseViewHolder.mExpenseDescriptionTextView.setText(expense.getExpenseDescription());
        expenseViewHolder.mExpenseCategoryTextView.setText(expense.getCategory());
        expenseViewHolder.mExpenseDateTextView.setText(expense.getExpenseDate());
        expenseViewHolder.mExpenseAmountTextView.setText(
                String.valueOf(TravelUtils.getCurrencyFormattedValue(expense.getExpenseTotal())));

        int categoryIconID = TravelUtils.getExpenseIconIDByCategory(expense.getCategory(), mContext);
        if(categoryIconID != 0) {
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(categoryIconID))
                    .apply(RequestOptions.circleCropTransform())
                    .into(expenseViewHolder.mCategoryIconImageView);
        }else{
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.ic_error_red_24dp))
                    .apply(RequestOptions.circleCropTransform())
                    .into(expenseViewHolder.mCategoryIconImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mExpenses == null)
            return 0;
        return mExpenses.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_expense_description)
        TextView mExpenseDescriptionTextView;
        @BindView(R.id.tv_expense_category)
        TextView mExpenseCategoryTextView;
        @BindView(R.id.tv_expense_date)
        TextView mExpenseDateTextView;
        @BindView(R.id.tv_expense_amount)
        TextView mExpenseAmountTextView;
        @BindView(R.id.img_category)
        ImageView mCategoryIconImageView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
