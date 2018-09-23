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
import android.view.ViewTreeObserver;

import com.example.android.travelwallet.adapters.TravelAdapter;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv_travels)
    RecyclerView mTravelsRecyclerView;
    TravelAdapter mTravelAdapter;

    @BindView(R.id.bt_add_travel)
    FloatingActionButton mAddTravelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        Configure Travel Recycler View
        mTravelAdapter = new TravelAdapter(this);
        mTravelsRecyclerView.setAdapter(mTravelAdapter);
        if(getResources().getConfiguration().screenWidthDp < 600) {
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1,
                    GridLayoutManager.VERTICAL, false));
        }else{
            mTravelsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1,
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
        TravelViewModel travelViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(TravelViewModel.class);
        travelViewModel.getAllTravels().observe(this, new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                mTravelAdapter.setData(travels);
            }
        });
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
}
