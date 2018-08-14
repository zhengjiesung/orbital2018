package com.montethecat.scroogev2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class BudgetMeterFragment extends Fragment {

    View view;
    RecyclerView budgetMeterCard;
    public static BudgetMeterViewAdapter budgetMeterViewAdapter;
    DocumentReference mDocRef;
    FirebaseAuth auth;
    List<BudgetMeterItemBudget> budgetMeterItemBudgetList;
    List<BudgetMeterItemProgress> budgetMeterItemProgressList;
    TextView budgetMeterDate;
    Button refresh;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_budgetmeter, container, false);
        budgetMeterCard = view.findViewById(R.id.budgetMeterCard);
        budgetMeterDate = view.findViewById(R.id.budgetMeterDate);
        refresh = view.findViewById(R.id.refresh);

        budgetMeterDate.setText("Top 3 Budget Meters: "+MetaData.setMonth(MainActivity.monthCurrently)+" "+MainActivity.yearCurrently);


        MainActivity.updateBudgetMeters();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                budgetMeterViewAdapter = new BudgetMeterViewAdapter(getContext(), MetaData.budgetMeterItemBudgetListTop3, MetaData.budgetMeterItemProgressListTop3);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

                budgetMeterCard.setLayoutManager(mLayoutManager);
                budgetMeterCard.setAdapter(budgetMeterViewAdapter);

            }
        }, 1500);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.updateBudgetMeters();

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        budgetMeterViewAdapter = new BudgetMeterViewAdapter(getContext(), MetaData.budgetMeterItemBudgetListTop3, MetaData.budgetMeterItemProgressListTop3);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

                        budgetMeterCard.setLayoutManager(mLayoutManager);
                        budgetMeterCard.setAdapter(budgetMeterViewAdapter);

                    }
                }, 1500);

            }
        });
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        MainActivity.updateBudgetMeters();
//
//        Handler handler = new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                budgetMeterViewAdapter = new BudgetMeterViewAdapter(getContext(), MetaData.budgetMeterItemBudgetListTop3, MetaData.budgetMeterItemProgressListTop3);
//                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//
//                budgetMeterCard.setLayoutManager(mLayoutManager);
//                budgetMeterCard.setAdapter(budgetMeterViewAdapter);
//
//            }
//        }, 1500);
//
//
//
//
//
//
//    }
}


