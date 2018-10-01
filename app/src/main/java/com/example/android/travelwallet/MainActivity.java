package com.example.android.travelwallet;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupMenu;

import com.example.android.travelwallet.adapters.TravelAdapter;
import com.example.android.travelwallet.interfaces.CardPopupMenuListener;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        TravelAdapter.TravelAdapterOnClickHandler,
        CardPopupMenuListener {
    @BindView(R.id.rv_travels)
    RecyclerView mTravelsRecyclerView;
    TravelAdapter mTravelAdapter;

    @BindView(R.id.bt_add_travel)
    FloatingActionButton mAddTravelButton;

    TravelViewModel mTravelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        Configure Travel Recycler View
        mTravelAdapter = new TravelAdapter(this, this, this);
        mTravelsRecyclerView.setAdapter(mTravelAdapter);
        if(getResources().getConfiguration().screenWidthDp < 600) {
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,
                    GridLayoutManager.VERTICAL, false));
        }else{
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,
                    GridLayoutManager.VERTICAL, false));
            ViewTreeObserver viewTreeObserver = mTravelsRecyclerView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    calculateSize();
                }
            });
        }
        mTravelsRecyclerView.setHasFixedSize(true);
        mTravelViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);
        mTravelViewModel.getAllTravels().observe(this, new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                mTravelAdapter.setData(travels);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTravelAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.bt_add_travel)
    public void callAddTravelActivity(){
        Intent intent = new Intent(this, InsertTravelFormActivity.class);
        startActivity(intent);
    }

    private void calculateSize() {
        int spanCount = (int) Math.floor(mTravelsRecyclerView.getWidth() /
                getResources().getDimension(R.dimen.travel_miniature_width));
        ((GridLayoutManager) mTravelsRecyclerView.getLayoutManager()).setSpanCount(spanCount);
    }

    @Override
    public void onClick(Travel clickedTravel) {
        Intent intent = new Intent(this, TravelDetailsActivity.class);
        intent.putExtra(TravelDetailsActivity.KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(clickedTravel));
        startActivity(intent);
    }

    public void startTravelFormActivityAsEditMode(Travel travel){
        Intent intent = new Intent(this, InsertTravelFormActivity.class);
        intent.putExtra(InsertTravelFormActivity.KEY_INTENT_EXTRA_TRAVEL, Parcels.wrap(travel));
        startActivity(intent);
    }

    @Override
    public void onPopupMenuClick(View view, final int pos) {
        //        Create Card popup menu
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.travel_miniature_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_delete :
                        mTravelViewModel.delete(mTravelAdapter.getData().get(pos));
                        return true;
                    case R.id.item_edit :
                        startTravelFormActivityAsEditMode(mTravelAdapter.getData().get(pos));
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}
