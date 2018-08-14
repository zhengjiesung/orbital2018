package com.montethecat.scroogev2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.transform.Templates;

/**
 * Created by Mako on 1/13/2017.
 */
public class DataFragment extends Fragment {
    View view;
    ViewPager viewPager;
    TabLayout tabLayout;
    public static Spinner spinnerMonth;
    public static Spinner spinnerYear;
    public static FirebaseUser user;
    public static String uid;
    private static FirebaseAuth auth;
    static String collectionNAME;
    private static DocumentReference mDocRef;
    int year;
    int month;

    public static String monthFromSpinner;
    public static String yearFromSpinner;

    public static boolean spinnerChangedIncome;
    public static boolean spinnerChanged;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sample, container, false);
        getActivity().setTitle("Transaction");
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new sliderAdapter(getChildFragmentManager()));
        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        MetaData.catTotal1 = new HashMap<>();
        MetaData.colorExpense1 = new ArrayList<>();
        MetaData.incTotal1 = new HashMap<>();
        MetaData.colorIncome1 = new ArrayList<>();
        //IncomeContentFragment.yValuesIncome=
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });







        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        spinnerMonth = (Spinner) view.findViewById(R.id.spinnerMonth);
        spinnerYear = (Spinner) view.findViewById(R.id.spinnerYear);

        final String[] monthSpinner = new String[]{
                "Jan",
                "Feb",
                "Mar",
                "Apr",
                "May",
                "Jun",
                "Jul",
                "Aug",
                "Sep",
                "Oct",
                "Nov",
                "Dec"
        };

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, monthSpinner);
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setSelection(month);
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                DataFragment.spinnerMonth.setSelection(month);
            }
        }, 50);


        final String[] yearSpinner = new String[]{
                "2017",
                "2018",
                "2019"
        };

        ArrayAdapter<String>  adapterYear = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, yearSpinner);
        spinnerYear.setAdapter(adapterYear);

        for (int i = 0; i < yearSpinner.length; i++){
            if (year == Integer.valueOf(yearSpinner[i])){
                spinnerYear.setSelection(i);
                break;
            }
        }


        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String month = monthSpinner[position];
                String year = spinnerYear.getSelectedItem().toString();

                monthFromSpinner=month;
                yearFromSpinner=year;

                tallyTotal(month, year);
                tallyTotalIncome(month, year);

                ExpenditureContentFragment.getTransactions();
                IncomeContentFragment.getTransactions();

               /* Double sum=0.0;
                if(DataFragment.spinnerChanged==false){
                    for(String key:MetaData.catTotal.keySet()){
                        sum+=MetaData.catTotal.get(key);
                    }}else {
                    for(String key:MetaData.catTotal1.keySet()){
                        sum+=MetaData.catTotal1.get(key);
                    }}
                ExpenditureContentFragment.totalView1.setText("Total\nSGD$"+MetaData.df.format(sum).toString());
                Double suminc=0.0;
                if(DataFragment.spinnerChangedIncome==false){
                    for(String key:MetaData.incTotal.keySet()){
                        suminc+=MetaData.incTotal.get(key);
                    }}else {
                    for(String key:MetaData.incTotal1.keySet()){
                        suminc+=MetaData.incTotal1.get(key);
                    }}
                IncomeContentFragment.totalView1Income.setText("Total\nSGD$"+MetaData.df.format(suminc).toString());*/


                Log.i("Month and Year Selected", month + " " + year);



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String year = yearSpinner[position];
                String month = spinnerMonth.getSelectedItem().toString();

                monthFromSpinner=month;
                yearFromSpinner=year;

                tallyTotal(month, year);
                tallyTotalIncome(month, year);
                ExpenditureContentFragment.getTransactions();
                IncomeContentFragment.getTransactions();








                Log.i("Month and Year Selected", month + " " + year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }
    private class sliderAdapter extends FragmentPagerAdapter{

        final  String tabs[]={"Expenditure", "Income"};
        public sliderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(position==0) {
                return new ExpenditureContentFragment();
            }
            else {
                return new IncomeContentFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }


    public void tallyTotal(String month, String year) {
        spinnerChanged=true;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {

        }else {
            uid = user.getUid();
            collectionNAME="users/"+uid;
            //wallet Changes
            if(MetaData.inwallet==true){
                uid=MetaData.walletName;
                collectionNAME="group/"+uid;
            }
            MetaData.catTotal1.clear();
            //for pieChart Colors
            MetaData.colorExpense1.clear();
            final ArrayList<String> category = new ArrayList<>();
            category.add("Food and Drinks");
            category.add("Shopping");
            category.add("Housing");
            category.add("Transport");
            category.add("Vehicle");
            category.add("Life & Entertainment");
            category.add("Communications,PC");
            category.add("Financial Expenses");
            category.add("Investment");
            category.add("Education");
            category.add("Insurance Payment");
            category.add("Transfer-out");
            category.add("Others (Expenditure)");
            mDocRef = FirebaseFirestore.getInstance().document(collectionNAME+ "/Metadata/" + month + year);
            mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for (int n = 0; n < category.size(); n++) {
                        if (documentSnapshot != null) {
                            String totalString = documentSnapshot.getString(category.get(n));

                            if (totalString == null) {
                                Double arr = MetaData.catTotal1.get(category);
                                if (arr != null) {

                                } else {
                                    Log.i("Removed", category.get(n));
                                    MetaData.catTotal1.remove(category.get(n));
                                }
                            } else {
                                MetaData.catTotal1.put(category.get(n), Double.parseDouble(totalString));
                                Log.i("HashMap1", String.valueOf(MetaData.catTotal1));
                            }


                        }
                    }
                    Log.i("HashMap1Final", String.valueOf(MetaData.catTotal1));
                    Double sum=0.0;
                    for(String key:MetaData.catTotal1.keySet()){
                        sum+=MetaData.catTotal1.get(key);
                        Log.i("Barnabas Sum", sum.toString());
                    }
                    Log.i("Barnabas Sum", sum.toString());
                    ExpenditureContentFragment.totalView1.setText("Total\nSGD$"+MetaData.df.format(sum).toString());

                    //Update PieChart in Expenditure and Income Fragment
                    if (ExpenditureContentFragment.yValues != null) {
                        ExpenditureContentFragment.yValues.clear();
                       /*/
                        changes this part for colors to work
                        */
                        //for pie chart colors
                        //added
                        MetaData.colorExpense1.clear();

                        //7/15/2018
                        //ExpenditureIncomeFragment.colorExpense=new int[MetaData.catTotal1.size()];

                        for (String key : MetaData.catTotal1.keySet()) {
                            ExpenditureContentFragment.yValues.add(new PieEntry(Float.parseFloat(MetaData.catTotal1.get(key).toString()), key));
                            MetaData.colorExpense1.add(ExpenditureIncomeFragment.rgb(MetaData.chooseColor(key)));

                        }


                        ExpenditureContentFragment.dataSet = new PieDataSet(ExpenditureContentFragment.yValues, "Countries");

                        ExpenditureContentFragment.dataSet.setColors(convertIntegers(MetaData.colorExpense1));
                        PieData data = new PieData(ExpenditureContentFragment.dataSet);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(Color.YELLOW);
                        ExpenditureContentFragment.pieChart.getLegend().setEnabled(false);
                        ExpenditureContentFragment.pieChart.setData(data);
                        ExpenditureContentFragment.pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
                        //Ends here
                        if (ExpenditureContentFragment.pieChart != null) {
                            ExpenditureContentFragment.pieChart.notifyDataSetChanged();
                        }

                        ExpenditureContentFragment.pieChart.invalidate();
                    }
                }
            });



        }


    }

    public static void tallyTotalIncome(String month, String year) {
        spinnerChangedIncome=true;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {

        }else {
            uid = user.getUid();
            collectionNAME="users/"+uid;
            //wallet Changes
            if(MetaData.inwallet==true){
                uid=MetaData.walletName;
                collectionNAME="group/"+uid;
            }
            MetaData.incTotal1.clear();
            //for pieChart Colors
            MetaData.colorIncome1.clear();

            final ArrayList<String> category = new ArrayList<>();
            category.add("Salary");
            category.add("Business");
            category.add("Loan");
            category.add("Parental Leave");
            category.add("Insurance Payout");
            category.add("Transfer-in");
            category.add("Others (Income)");
            mDocRef = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + month + year);
            mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for (int n = 0; n < category.size(); n++) {
                        if (documentSnapshot != null) {
                            String totalString = documentSnapshot.getString(category.get(n));

                            if (totalString == null) {
                                Double arr = MetaData.incTotal1.get(n);
                                if (arr != null) {

                                } else {
                                    MetaData.incTotal1.remove(category.get(n));
                                }
                            } else {
                                MetaData.incTotal1.put(category.get(n), Double.parseDouble(totalString));
                                Log.i(category.get(n), totalString);
                                Log.i("HashMap2 ", String.valueOf(MetaData.incTotal1));
                            }


                        }
                    }
                    Log.i("HashMap2final", String.valueOf(MetaData.incTotal1));
                    Double suminc=0.0;

                    for(String key:MetaData.incTotal1.keySet()) {
                        suminc += MetaData.incTotal1.get(key);
                    }
                    IncomeContentFragment.totalView1Income.setText("Total\nSGD$"+MetaData.df.format(suminc).toString());
                    //Update PieChart in Expenditure and Income Fragment
                    if (IncomeContentFragment.yValuesIncome != null) {
                        IncomeContentFragment.yValuesIncome.clear();
                        /*/
                        changes this part for colors to work
                        */
                        //for pie chart colors
                        //added
                        MetaData.colorIncome1.clear();
                        //7/15/2018
                        //ExpenditureIncomeFragment.colorIncome = new int[MetaData.incTotal1.size()];

                        for (String key : MetaData.incTotal1.keySet()) {
                            IncomeContentFragment.yValuesIncome.add(new PieEntry(Float.parseFloat(MetaData.incTotal1.get(key).toString()), key));
                            MetaData.colorIncome1.add(ExpenditureIncomeFragment.rgb(MetaData.chooseColor(key)));

                        }

                        //7/15/2018
                        IncomeContentFragment.dataSet = new PieDataSet(IncomeContentFragment.yValuesIncome, "Countries");

                        IncomeContentFragment.dataSet.setColors(convertIntegers(MetaData.colorIncome1));
                        PieData data = new PieData(IncomeContentFragment.dataSet);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(Color.YELLOW);
                        IncomeContentFragment.pieChartIncome.getLegend().setEnabled(false);
                        IncomeContentFragment.pieChartIncome.setData(data);
                        IncomeContentFragment.pieChartIncome.animateY(1000, Easing.EasingOption.EaseInOutCubic);
                        //Ends here

                        //7/15/2018
                            IncomeContentFragment.pieChartIncome.notifyDataSetChanged();

                        //ExpenditureIncomeFragment.pieChartIncome.notifyDataSetChanged();
                        IncomeContentFragment.pieChartIncome.invalidate();
                    }
                }
            });
        }


    }

    //for colors
    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

}