package com.montethecat.scroogev2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AddSort extends AppCompatActivity {
    Spinner dropdown1, dropdown2, dropdown3, dropdown4;
    String text1, text2, text3, text4;
    String layoutName1, layoutName2, layoutName3, layoutName4;

    static ArrayList<String> arr;

    public void reset(View view){
        Intent intent = new Intent(AddSort.this, MainActivity.class);
        intent.putExtra("0", "Balance and Monthly Change");
        intent.putExtra("1", "Expenditure and Income");
        intent.putExtra("2", "Budget Meter");
        //intent.putExtra("tracker", "true");

        startActivity(intent);
    }

    public void confirm(View view){
        Intent intent = new Intent(AddSort.this, MainActivity.class);

        text1 = dropdown1.getSelectedItem().toString();
        text2 = dropdown2.getSelectedItem().toString();
        text3 = dropdown3.getSelectedItem().toString();
        text4 = dropdown4.getSelectedItem().toString();


        arr = new ArrayList<String>(Arrays.asList(text1, text2, text3, text4));

        for (int i = 0; i < arr.size(); i++){
            intent.putExtra(Integer.toString(i), arr.get(i));
        }

        intent.putExtra("tracker", "true");
        startActivity(intent);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutName1 = getIntent().getStringExtra("0");
        layoutName2 = getIntent().getStringExtra("1");
        layoutName3 = getIntent().getStringExtra("2");
        layoutName4 = getIntent().getStringExtra("3");


        int intlayoutName1 =optionsInt(layoutName1);
        int intlayoutName2 =optionsInt(layoutName2);
        int intlayoutName3 =optionsInt(layoutName3);
        int intlayoutName4 =optionsInt(layoutName4);
        setContentView(R.layout.activity_add_sort);

        //get the spinner from the xml.
        dropdown1 = findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Balance and Monthly Change",
                "Expenditure and Income",
                "Budget Meter",
                "Recent Transactions",
                "None"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown1.setAdapter(adapter);

        dropdown2 = findViewById(R.id.spinner2);
        dropdown2.setAdapter(adapter);

        dropdown3 = findViewById(R.id.spinner3);
        dropdown3.setAdapter(adapter);

        dropdown4 = findViewById(R.id.spinner4);
        dropdown4.setAdapter(adapter);

        dropdown1.setSelection(intlayoutName1);
        dropdown2.setSelection(intlayoutName2);
        dropdown3.setSelection(intlayoutName3);
        dropdown4.setSelection(intlayoutName4);
    }


}
