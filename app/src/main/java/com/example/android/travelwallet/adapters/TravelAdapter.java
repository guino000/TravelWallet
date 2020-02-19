package com.example.android.travelwallet.adapters;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.travelwallet.InsertTravelFormActivity;
import com.example.android.travelwallet.R;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.utils.CurrencyUtils;
import com.example.android.travelwallet.utils.GooglePlacesUtils;
import com.example.android.travelwallet.utils.TravelUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {
    private final Context mContext;
    private List<Travel> mTravels;
    private final TravelAdapterOnClickHandler mClickHandler;

    public interface TravelAdapterOnClickHandler{
        void onClick(Travel clickedTravel);
    }

    public TravelAdapter(Context context, TravelAdapterOnClickHandler clickHandler){
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.travel_miniature_main, viewGroup, false);
        return new TravelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TravelViewHolder travelViewHolder, int i) {
        final Travel travel = mTravels.get(i);
        travelViewHolder.mTravelNameTextView.setText(travel.getName());
        travelViewHolder.mDestinationTextView.setText(travel.getDestination());

//        Get budget data
        BigDecimal travelBudget = travel.getBudget();
        BigDecimal travelExpenses = TravelUtils.getTravelExpensesTotal((Application) mContext.getApplicationContext(), travel);

//        Set budget values
        travelViewHolder.mTotalBudgetTextView.setText(CurrencyUtils.getCurrencyFormattedValue(travelBudget, travel.getCurrencyCode()));
        travelViewHolder.mTotalExpensesTextView.setText(CurrencyUtils.getCurrencyFormattedValue(travelExpenses, travel.getCurrencyCode()));

//        Set budget color coding
        if (travelExpenses.compareTo(travelBudget) > 0) {
            travelViewHolder.mTotalBudgetTextView.setTextColor(mContext.getResources().getColor(R.color.red));
            travelViewHolder.mTotalExpensesTextView.setTextColor(mContext.getResources().getColor(R.color.red));
            travelViewHolder.mOverspentWarningImageView.setVisibility(View.VISIBLE);
        } else {
            travelViewHolder.mTotalBudgetTextView.setTextColor(mContext.getResources().getColor(R.color.progress_green));
            travelViewHolder.mTotalExpensesTextView.setTextColor(mContext.getResources().getColor(R.color.progress_green));
            travelViewHolder.mOverspentWarningImageView.setVisibility(View.INVISIBLE);
        }

        travelViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = travelViewHolder.getAdapterPosition();
                Travel travel = mTravels.get(position);
                travelViewHolder.startTravelFormActivityAsEditMode(travel);
                return true;
            }
        });

        if (!travel.getGooglePlaceID().isEmpty()) {
            Glide.with(mContext)
                    .load(GooglePlacesUtils.getPhotoFromPhotoReference(travel.getGooglePlaceID(), travelViewHolder.mTravelPhotoImageView.getMaxWidth()))
                    .apply(RequestOptions.placeholderOf(mContext.getResources().getDrawable(R.drawable.img_placeholder)))
                    .apply(RequestOptions.noTransformation())
                    .apply(RequestOptions.noAnimation())
                    .into(travelViewHolder.mTravelPhotoImageView);
        }
    }

    @Override
    public long getItemId(int position) {
        if(mTravels == null) return 0;
        return mTravels.get(position).getId();
    }

    @Override
    public int getItemCount() {
        if(mTravels == null)
            return 0;
        return mTravels.size();
    }

    public List<Travel> getData(){
        return mTravels;
    }

    public void setData(List<Travel> newData){
        mTravels = newData;
        notifyDataSetChanged();
    }

    class TravelViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        @BindView(R.id.tv_miniature_travel_name)
        TextView mTravelNameTextView;

        @BindView(R.id.tvTravelMiniatureTotalBudget)
        TextView mTotalBudgetTextView;

        @BindView(R.id.tvTravelMiniatureTotalSpent)
        TextView mTotalExpensesTextView;

        @BindView(R.id.tv_miniature_travel_destination)
        TextView mDestinationTextView;

        @BindView(R.id.img_miniature_travel_miniature)
        ImageView mTravelPhotoImageView;

        @BindView(R.id.img_overspent)
        ImageView mOverspentWarningImageView;

        @BindView(R.id.layout_travel_miniature)
        ConstraintLayout mLayout;

        public TravelViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(mTravels.get(position));
        }

        public void startTravelFormActivityAsEditMode(Travel travel) {
            Intent intent = new Intent(mContext, InsertTravelFormActivity.class);
            intent.putExtra(InsertTravelFormActivity.KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(travel));
            mContext.startActivity(intent);
        }
    }
}
