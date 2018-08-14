package com.montethecat.scroogev2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddSortFragment extends Fragment {
    Spinner dropdown1, dropdown2, dropdown3, dropdown4;
    String text1, text2, text3, text45;
    Button confrimButton, resetButton;

    private FirebaseAuth auth;

    private DocumentReference mDocRef;
    private Map<String, Object> dataToSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_add_sort,container,false);
        return view;
    }
    public int optionsInt(String layoutName){
        int intLayoutName = 4;

        if(layoutName.equals("Balance and Monthly Change")){
            intLayoutName=0;
        }
        if(layoutName.equals("Expenditure and Income")){
            intLayoutName= 1;}
        if(layoutName.equals("Budget Meter")){
            intLayoutName= 2;
        }
        if(layoutName.equals("Recent Transactions")){
            intLayoutName=3;}
        return intLayoutName;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        confrimButton=view.findViewById(R.id.confirmButton);
        resetButton=view.findViewById(R.id.resetButton);
        //get the spinner from the xml.
        dropdown1 = view.findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Balance and Monthly Change",
                "Expenditure and Income",
                "Budget Meter",
                "Recent Transactions",
                "None"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown1.setAdapter(adapter);

        dropdown2 = view.findViewById(R.id.spinner2);
        dropdown2.setAdapter(adapter);

        dropdown3 = view.findViewById(R.id.spinner3);
        dropdown3.setAdapter(adapter);

        dropdown4 = view.findViewById(R.id.spinner4);
        dropdown4.setAdapter(adapter);

        int intlayoutName1 =optionsInt(MainActivity.layoutName1);
        int intlayoutName2 =optionsInt(MainActivity.layoutName2);
        int intlayoutName3 =optionsInt(MainActivity.layoutName3);
        int intlayoutName4 =optionsInt(MainActivity.layoutName4);

        dropdown1.setSelection(intlayoutName1);
        dropdown2.setSelection(intlayoutName2);
        dropdown3.setSelection(intlayoutName3);
        dropdown4.setSelection(intlayoutName4);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.layoutName1="Balance and Monthly Change";
                MainActivity.layoutName2="Expenditure and Income";
                MainActivity.layoutName3="Budget Meter";
                MainActivity.layoutName4="None";
                MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                MainActivity.overallScreenArea.setVisibility(View.GONE);
                MainActivity.enteredSettings=true;
                MainActivity.upDateSettings();

            }
        });
        confrimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.layoutName1= dropdown1.getSelectedItem().toString();
                MainActivity.layoutName2 = dropdown2.getSelectedItem().toString();
                MainActivity.layoutName3 = dropdown3.getSelectedItem().toString();
                MainActivity.layoutName4 = dropdown4.getSelectedItem().toString();
                MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                MainActivity.overallScreenArea.setVisibility(View.GONE);
                MainActivity.enteredSettings=true;
                MainActivity.upDateSettings();
            }
        });

    }


}
