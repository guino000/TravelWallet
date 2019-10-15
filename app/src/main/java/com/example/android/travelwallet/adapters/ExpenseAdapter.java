package com.example.android.travelwallet.adapters;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.travelwallet.R;
import com.example.android.travelwallet.interfaces.CardPopupMenuListener;
import com.example.android.travelwallet.model.Converters;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.utils.CurrencyUtils;
import com.example.android.travelwallet.utils.TravelUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private final Context mContext;
    private List<TravelExpense> mExpenses;
    private final CardPopupMenuListener mPopupListener;

    public ExpenseAdapter(Context context, CardPopupMenuListener popupMenuListener) {
        mContext = context;
        mPopupListener = popupMenuListener;
    }

    public List<TravelExpense> getData() {
        return mExpenses;
    }

    public void setData(List<TravelExpense> newData) {
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
    public void onBindViewHolder(@NonNull final ExpenseViewHolder expenseViewHolder, int i) {
        TravelExpense expense = mExpenses.get(i);
        expenseViewHolder.mExpenseDescriptionTextView.setText(expense.getExpenseDescription());
        expenseViewHolder.mExpenseCategoryTextView.setText(expense.getCategory());
        expenseViewHolder.mExpenseDateTextView.setText(Converters.dateToString(expense.getExpenseDate()));
        expenseViewHolder.mExpenseAmountTextView.setText(
                String.valueOf(CurrencyUtils.getCurrencyFormattedValue(expense.getExpenseTotal(), TravelUtils.getCurrentTravel(
                        expense.getTravelID(), (Application) mContext.getApplicationContext()).getCurrencyCode())));
        expenseViewHolder.mPopupMenuImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupListener.onPopupMenuClick(v, expenseViewHolder.getAdapterPosition());
            }
        });
        expenseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPopupListener.onPopupMenuClick(v, expenseViewHolder.getAdapterPosition());
                return true;
            }
        });

        int categoryIconID = TravelUtils.getExpenseIconIDByCategory(expense.getCategory(), mContext);
        if (categoryIconID != 0) {
            Glide.with(mContext.getApplicationContext())
                    .load(mContext.getResources().getDrawable(categoryIconID))
                    .into(expenseViewHolder.mCategoryIconImageView);
        } else {
            Glide.with(mContext.getApplicationContext())
                    .load(mContext.getResources().getDrawable(R.drawable.ic_error_red_24dp))
                    .into(expenseViewHolder.mCategoryIconImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mExpenses == null)
            return 0;
        return mExpenses.size();
    }

    @SuppressWarnings("WeakerAccess")
    class ExpenseViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.img_popup_menu_button)
        ImageButton mPopupMenuImageButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
