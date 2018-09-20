package com.example.android.travelwallet;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.travelwallet.model.ExpenseViewModel;
import com.example.android.travelwallet.model.Travel;
import com.example.android.travelwallet.model.TravelExpense;
import com.example.android.travelwallet.model.TravelViewModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.android.travelwallet", appContext.getPackageName());
    }

    @Test
    public void testTravelDatabase(){
        Application application = mActivityRule.getActivity().getApplication();
        final TravelViewModel viewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(TravelViewModel.class);
        final Travel travel = new Travel("Test travel",
                "Rome",
                new BigDecimal("100"),
                "09/16/18",
                "09/17/18");

        viewModel.getAllTravels().observe(mActivityRule.getActivity(), new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                if(travels.size() > 0){
                    assertEquals(travels.get(0).getName(), travel.getName());
                }else{
                    assertEquals(viewModel.getAllTravels().getValue().size(),0);
                }
            }
        });

        viewModel.insert(travel);
        viewModel.delete(travel);
    }

    @Test
    public void testExpenseDatabase(){
        Application application = mActivityRule.getActivity().getApplication();
        final TravelViewModel viewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(TravelViewModel.class);
        final ExpenseViewModel expenseViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application).create(ExpenseViewModel.class);
        final Travel travel = new Travel("Test travel",
                "Rome",
                new BigDecimal("100"),
                "09/16/18",
                "09/17/18");

        viewModel.getAllTravels().observe(mActivityRule.getActivity(), new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                if(travels != null) {
                    final Travel queryTravel = travels.get(0);
                    final TravelExpense expense = new TravelExpense(
                            queryTravel.getId(),
                            "Test expense",
                            new BigDecimal("100.00"),
                            "Test Category",
                            "09/20/18"
                    );
                    expenseViewModel.insert(expense);
                }
            }
        });

        viewModel.insert(travel);

        expenseViewModel.getAllExpenses().observe(mActivityRule.getActivity(), new Observer<List<TravelExpense>>() {
            @Override
            public void onChanged(@Nullable List<TravelExpense> expenses) {
                if(expenses != null){
                    assertNotEquals(expenses.get(0).getTravelID(), 0);
                    viewModel.delete(travel);
                }
            }
        });
    }
}
