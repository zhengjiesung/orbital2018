package com.montethecat.scroogev2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class BudgetFragment extends Fragment {

    Spinner spinnerCat;
    EditText limit;
    BarChart barChart;
    TextView information;
    String currMonth;
    String prevMonth;
    String prev2Month;
    //for wallet or in account
    String collectionNAME;
    FirebaseUser user;
    String uid;
    FirebaseAuth auth;
    double totalAmount;
    int validMonths;
    Button viewBudgetMeter;
    Fragment fragment = null;
    Button setLimit;
    DocumentReference mDocRef;


    DecimalFormat f = new DecimalFormat("##.00");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Budget");
        MainActivity.updateBudgetMeters();
        return inflater.inflate(R.layout.fragment_budget, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCat = (Spinner) view.findViewById(R.id.spinnerCat);
        limit = (EditText) view.findViewById(R.id.limit);
        barChart = (BarChart) view.findViewById(R.id.barGraph);
        information = (TextView) view.findViewById(R.id.information);
        viewBudgetMeter = (Button) view.findViewById(R.id.viewBudgetMeter);
        setLimit = (Button) view.findViewById(R.id.setLimit);

        final ProgressDialog csprogress=new ProgressDialog(getContext());

        viewBudgetMeter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                csprogress.setMessage("Loading Budget Meter page...");
                csprogress.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        csprogress.dismiss();
                        //whatever you want just you have to launch overhere.
                        fragment = new BudgetMeterPageFragment();
                        setFragment(fragment);

                    }
                }, 1000);//just mention the time when you want to launch your action


            }
        });


        setLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // brings user to the budgetMeterFragment
                if(limit.getText() == null){
                    Toast.makeText(getContext(), "Please key in an amount", Toast.LENGTH_SHORT).show();
                }else{

                    // check whether the user exists
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        getActivity().finish();
                        startActivity(new Intent(getContext(), Login.class));

                        // obtain the collection path
                    } else {
                        uid = user.getUid();
                        collectionNAME = collectionNAME = "users/" + uid;

                        if (MetaData.inwallet == true) {
                            uid = MetaData.walletName;
                            collectionNAME = "group/" + uid;
                        }
                    }


                    mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME + "/budgetData").document("budgetDataDocument");
                    String categorySelected = spinnerCat.getSelectedItem().toString();

                    String budget = limit.getText().toString();

                    HashMap<String, String> budgetAllocation = new HashMap<String, String>();
                    budgetAllocation.put(categorySelected, budget);

                    mDocRef.set(budgetAllocation, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                MainActivity.updateBudgetMeters();
                            }
                        }
                    });

                    // hide keyboard
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Toast.makeText(getContext(), "Budget has been set", Toast.LENGTH_SHORT).show();

                }

            }


        });


        final String[] categorySpinner = new String[]{
                "Food and Drinks",
                "Shopping",
                "Housing",
                "Transport",
                "Vehicle",
                "Life & Entertainment",
                "Communications,PC",
                "Financial Expenses",
                "Investment",
                "Education",
                "Insurance Payment",
                "Transfer-out",
                "Others (Expenditure)"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, categorySpinner);
        spinnerCat.setAdapter(adapter);

        int pos = 0;
        for (int i = 0; i < categorySpinner.length; i++){
            String category = categorySpinner[i];
            if (category.equals(MetaData.budgetCategory)){
                break;
            }
            pos ++;

        }
        spinnerCat.setSelection(pos);

        // reset budgetCategory to Food and Drinks
        MetaData.budgetCategory = "Food and Drinks";

        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // set validMonths to 0 each time a category is selected
                validMonths = 0;
                // set totalAmount to 0 each time a category is selected
                totalAmount = 0.00;

                final String selectedCategory = categorySpinner[position];

                final ArrayList<BarEntry> barEntries = new ArrayList<>();
                Calendar now = Calendar.getInstance();


                int value1 = now.get(Calendar.MONTH);
                currMonth = MetaData.setMonth(value1);
                Log.i("CurrentMonth", currMonth);
                Log.i("CurrentMonthNumber", String.valueOf(value1));

                int value2 = now.get(Calendar.MONTH) - 1;
                if (value2 < 0) {
                    value2 = 12 + value2;
                }
                prevMonth = MetaData.setMonth(value2);
                Log.i("PrevMonth", prevMonth);

                int value3 = now.get(Calendar.MONTH) - 2;
                if (value3 < 0) {
                    value3 = 12 + value3;
                }
                prev2Month = MetaData.setMonth(value3);
                Log.i("Prev2Month", prev2Month);

                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    getActivity().finish();
                    startActivity(new Intent(getContext(), Login.class));
                } else {
                    uid = user.getUid();
                    collectionNAME = collectionNAME = "users/" + uid;

                    if (MetaData.inwallet == true) {
                        uid = MetaData.walletName;
                        collectionNAME = "group/" + uid;
                    }
                }

                Calendar c = Calendar.getInstance(TimeZone.getDefault());
                int curr_year = c.get(Calendar.YEAR);
                int prev_year = c.get(Calendar.YEAR);
                int prev2_year = c.get(Calendar.YEAR);


                if (currMonth.equals("Jan")) {
                    prev_year -= 1;
                    prev2_year -= 1;
                } else if (currMonth.equals("Feb")) {
                    prev2_year -= 1;
                }

                DocumentReference documentRef = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + currMonth + curr_year);
                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        double amt = 0.00;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.getString(spinnerCat.getSelectedItem().toString()) != null) {
                                String amount = document.getString(spinnerCat.getSelectedItem().toString());

                                amt += Double.parseDouble(amount);

                                if(amt != 0.0){
                                    validMonths ++;
                                }

                                totalAmount += amt;
                            }


                            if (document.exists()) {
                                Log.d("test", "DocumentSnapshot data" + barEntries.size());
                            } else {
                                Log.d("test", "No such document" + barEntries.size());
                            }
                        } else {
                            Log.d("test", "get failed with ", task.getException());
                        }

                        barEntries.add(new BarEntry((float) 3.5, (int) amt));
                    }
                });
                DocumentReference documentRef2 = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + prevMonth + prev_year);
                documentRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        double amt = 0.00;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.getString(spinnerCat.getSelectedItem().toString()) != null) {
                                String amount = document.getString(spinnerCat.getSelectedItem().toString());


                                amt += Double.parseDouble(amount);

                                if(amt != 0.0){
                                    validMonths ++;
                                }

                                totalAmount += amt;


                            }
                            if (document.exists()) {
                                Log.d("test", "DocumentSnapshot data" + barEntries.size());
                            } else {
                                Log.d("test", "No such document" + barEntries.size());
                            }
                        } else {
                            Log.d("test", "get failed with ", task.getException());
                        }
                        barEntries.add(new BarEntry((float) 2.5, (int) amt));
                    }
                });

                DocumentReference documentRef3 = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + prev2Month + prev2_year);
                documentRef3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        double amt = 0.00;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.getString(spinnerCat.getSelectedItem().toString()) != null) {
                                String amount = document.getString(spinnerCat.getSelectedItem().toString());

                                amt += Double.parseDouble(amount);

                                if(amt != 0.0){
                                    validMonths ++;
                                }

                                totalAmount += amt;
                            }


                            if (document.exists()) {
                                Log.d("test", "DocumentSnapshot data" + barEntries.size());
                            } else {
                                Log.d("test", "No such document" + barEntries.size());
                            }
                        } else {
                            Log.d("test", "get failed with ", task.getException());
                        }
                        barEntries.add(new BarEntry((float) 1.5, (int) amt));
                    }
                });




                final Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms


                        barChart.setDrawBarShadow(false);
                        barChart.setDrawValueAboveBar(true);
                        barChart.setMaxVisibleValueCount(50);
                        barChart.setPinchZoom(false);
                        barChart.setDrawGridBackground(true);


                        BarDataSet barDataSet = new BarDataSet(barEntries, "Past Expenses");
                        barDataSet.setColors(Color.rgb(254, 149, 7));

                        float barWidth = 0.45f;

                        BarData data = new BarData(barDataSet);
                        data.setBarWidth(barWidth);
                        barChart.setData(data);
                        barChart.setFitBars(true);




                        String[] months = new String[]{"blank",prev2Month, prevMonth, currMonth,"blank","blank","blank"};
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new MyXAxisVlaueFormatter(months));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                        xAxis.setGranularity(1);
                        xAxis.setCenterAxisLabels(true);
                        xAxis.setAxisMinimum(1);
                        xAxis.setAxisMaximum(4);

                        if (barChart !=null) {
                            barChart.notifyDataSetChanged();
                            barChart.invalidate();
                        }
                        double avg = 0.00;

                        if (validMonths != 0){
                            avg = totalAmount/validMonths;
                        }

                        String avg_str = f.format(avg);

                        information.setText("Historically you've spent about " + "$" + avg_str + " on " + selectedCategory);

                    }
                }, 1500);

            }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }



    public class MyXAxisVlaueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisVlaueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }


    public void setFragment(Fragment fragment) {
        if (fragment != null) {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.screen_area, fragment);
            ft.commit();

        }
    }
}


