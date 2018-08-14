package com.montethecat.scroogev2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlannedPaymentListsFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private PlannedPaymentAdapter plannedPaymentAdapter;
    TextView plannedPaymentlistTitle2;

    public PlannedPaymentListsFragment(){

    }

    List<PlannedPaymentItem> plannedPaymentItemList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plannedpaymentlist,container,false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView);
        plannedPaymentAdapter=new PlannedPaymentAdapter(getContext(),plannedPaymentItemList);
        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(plannedPaymentAdapter);
        plannedPaymentlistTitle2=(TextView)view.findViewById(R.id.plannedPaymentlistTitle2);
        plannedPaymentlistTitle2.setText("Last 5 record "+MetaData.setMonth(MainActivity.monthCurrently)+" "+MainActivity.yearCurrently);


        //recyclerView.setHasFixedSize(true);


        /*plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "Phone Bill",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));*/


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plannedPaymentItemList=new ArrayList<>();
        plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "Phone Bill",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));
        plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));
        /*plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "Phone Bill",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));
        plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "Phone Bill",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));
        plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "Phone Bill",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));
        plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "Phone Bill",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));*/

    }
}



