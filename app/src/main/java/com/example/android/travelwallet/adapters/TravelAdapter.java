package com.example.android.travelwallet.adapters;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Placeholder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.android.travelwallet.R;
import com.example.android.travelwallet.interfaces.CardPopupMenuListener;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.utils.TravelUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {
    private Context mContext;
    private List<Travel> mTravels;
    private final TravelAdapterOnClickHandler mClickHandler;
    private final CardPopupMenuListener mPopupListener;

    public interface TravelAdapterOnClickHandler{
        void onClick(Travel clickedTravel);
    }

    public TravelAdapter(Context context, TravelAdapterOnClickHandler clickHandler, CardPopupMenuListener popupListener){
        mContext = context;
        mClickHandler = clickHandler;
        mPopupListener = popupListener;
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
        Travel travel = mTravels.get(i);
        travelViewHolder.mTravelNameTextView.setText(travel.getName());
        travelViewHolder.mDestinationTextView.setText(travel.getDestination());
        travelViewHolder.mExpensesTextView.setText(
                TravelUtils.getBudgetExpenseComparison((Application) mContext.getApplicationContext(),travel));
        travelViewHolder.mPopupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupListener.onPopupMenuClick(travelViewHolder.itemView, travelViewHolder.getAdapterPosition());
            }
        });
        if(!travel.getPlaceID().equals("")) {
            TravelUtils.loadPlacePhoto(travel.getPlaceID(), mContext, travelViewHolder.mTravelPhotoImageView);
        }
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

    class TravelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_miniature_travel_name)
        TextView mTravelNameTextView;

        @BindView(R.id.tv_miniature_expenses_overview)
        TextView mExpensesTextView;

        @BindView(R.id.tv_miniature_travel_destination)
        TextView mDestinationTextView;

        @BindView(R.id.img_miniature_travel_miniature)
        ImageView mTravelPhotoImageView;

        @BindView(R.id.img_popup_menu_button)
        ImageButton mPopupImageButton;

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
    }
}
