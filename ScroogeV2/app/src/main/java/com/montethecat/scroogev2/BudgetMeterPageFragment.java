package com.montethecat.scroogev2;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BudgetMeterPageFragment extends Fragment {

    View view;
    RecyclerView recyclerViewBudget;
    public static BudgetMeterViewAdapter budgetMeterViewAdapter;
    DocumentReference mDocRef;
    FirebaseAuth auth;
    List<BudgetMeterItemBudget> budgetMeterItemBudgetList;
    List<BudgetMeterItemProgress> budgetMeterItemProgressList;
//    Button deleteAllBudgetMeters;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Budget Meters");

        view = inflater.inflate(R.layout.fragment_budgetmeterpage, container, false);
        recyclerViewBudget = (RecyclerView) view.findViewById(R.id.recyclerViewBudget);
//        deleteAllBudgetMeters = view.findViewById(R.id.deleteAllBudgetMeters);

        MainActivity.updateBudgetMeters();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                budgetMeterViewAdapter = new BudgetMeterViewAdapter(getContext(), MetaData.budgetMeterItemBudgetListFull, MetaData.budgetMeterItemProgresslistFull);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

                recyclerViewBudget.setLayoutManager(mLayoutManager);
                recyclerViewBudget.setAdapter(budgetMeterViewAdapter);

            }
        }, 1500);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        deleteAllBudgetMeters.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // clear all the array list
//                MetaData.budgetMeterItemBudgetListFull.clear();
//                MetaData.budgetMeterItemProgresslistFull.clear();
//                MetaData.budgetMeterItemBudgetListTop3.clear();
//                MetaData.budgetMeterItemProgressListTop3.clear();
//
//
//                if (BudgetMeterPageFragment.budgetMeterViewAdapter != null)
//                    BudgetMeterPageFragment.budgetMeterViewAdapter.notifyDataSetChanged();
//                if (BudgetMeterFragment.budgetMeterViewAdapter != null)
//                    BudgetMeterFragment.budgetMeterViewAdapter.notifyDataSetChanged();
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        budgetMeterViewAdapter = new BudgetMeterViewAdapter(getContext(), MetaData.budgetMeterItemBudgetListFull, MetaData.budgetMeterItemProgresslistFull);
//
//                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//
//                        recyclerViewBudget.setLayoutManager(mLayoutManager);
//                        recyclerViewBudget.setAdapter(budgetMeterViewAdapter);
//
//                    }
//                }, 1500);
//
//            }
//        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        budgetMeterItemBudgetList = new ArrayList<>();
        budgetMeterItemBudgetList.add(new BudgetMeterItemBudget("Food and Drinks", "200"));

        budgetMeterItemBudgetList.add( new BudgetMeterItemBudget("Housing",
                                "150"));


        budgetMeterItemProgressList = new ArrayList<>();
        budgetMeterItemProgressList.add(
                new BudgetMeterItemProgress("Food and Drinks",
                        "180")
        );
        budgetMeterItemProgressList.add(new BudgetMeterItemProgress("Housing", "100"));





    }

}


