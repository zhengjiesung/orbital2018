package com.montethecat.scroogev2;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.opencensus.common.Scope;

/**
 * Created by Mako on 1/13/2017.
 */
public class ExpenditureContentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {
    private static RecyclerView recyclerView;
    public static RecentTransactionAdapter recentTransactionAdapter;
    private static RecentTransactionAdapter recentTransactionAdapter2;
    public static DocumentReference mDocRef;

    View view;
    public static PieChart pieChart;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    static Spinner query;
    static String querySelection;
    public static TextView totalView1;
    ScrollView scrollView;
    static String collectionNAME;

    static DocumentSnapshot mLastQuiredDocument;
    //7/15/2018
    public static PieDataSet dataSet;
    public static ArrayList<PieEntry> yValues;
    public static int[] colorExpense;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.expenditurecontent,container,false);
        //7/15/2018
        DataFragment.spinnerChanged=false;
        Log.i("HashMap2", String.valueOf(MetaData.getCatTotal()));
        query = (Spinner) view.findViewById(R.id.querySpinner);
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerViewRecentTransaction2);
        recentTransactionAdapter=new RecentTransactionAdapter(getContext(),MetaData.recentTransactionItemList);
        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(recentTransactionAdapter);

        mSwipeRefreshLayout=view.findViewById(R.id.swipe_view);
        MetaData.recentTransactionItemList.clear();
        recyclerView.removeAllViews();

        query.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                getTransactions();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //query = (Spinner) view.findViewById(R.id.querySpinner);
        totalView1=getView().findViewById(R.id.totalView1);
        pieChart = getView().findViewById(R.id.pieChart2);

        updatePieChart();


    }

    public void updatePieChart(){

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.99f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);
        Double sum=0.0;
        for(String key:MetaData.catTotal.keySet()){
            sum+=MetaData.catTotal.get(key);
        }

        totalView1.setText("Total\nSGD$"+MetaData.df.format(sum).toString());

        pieChart.setEntryLabelTextSize(10f);


        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(ExpenditureIncomeFragment.yValues, "Countries");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
       /*/
        changes this part for colors to work
         */
        for (String key: MetaData.catTotal.keySet()){
            MetaData.colorExpense.add(rgb(MetaData.chooseColor(key)));
        }
        //7/15/2018
        yValues=new ArrayList<>();

        //convertIntegers(colorExpense)
        dataSet.setColors(MetaData.colorExpense);
        //Ends here

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);
        pieChart.setData(data);
        pieChart.getLegend().setEnabled(false);

        final String[] categorySpinner = new String[]{
                "All",
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
                "Others (Expenditure)"
        };
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, categorySpinner);
        //set the spinners adapter to the previously created one.
        query.setAdapter(adapter);


        mSwipeRefreshLayout.setOnRefreshListener(this);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String category = null;
                Log.i("Clicked", String.valueOf(h));
                int pos1=e.toString().indexOf("y: ");
                Pattern p=Pattern.compile("x: (.*?)\\.");
                int cat ;

                Matcher m=p.matcher(h.toString());
                StringBuilder builder = new StringBuilder();
                while(m.find()) {
                    builder.append(m.group(1));
                    System.out.println(m.group(1));
                }
                String price=e.toString().substring(pos1 + 3);
                cat=Integer.parseInt(builder.toString());
                Log.i("price",price);
                Log.i("cat",Integer.toString(cat));


                //7/15/2018
                int i = 0;
                if(DataFragment.spinnerChanged==false){
                    List Keys= new ArrayList(MetaData.catTotal.keySet());
                for(String key:MetaData.catTotal.keySet()){
                    if(/*MetaData.catTotal.get(key)==Double.parseDouble(price)&&*/i==cat){
                        category= (String) Keys.get(i);
                    }
                    i++;
                }}else {
                    List Keys= new ArrayList(MetaData.catTotal1.keySet());
                    for(String key:MetaData.catTotal1.keySet()){
                        if(/*MetaData.catTotal.get(key)==Double.parseDouble(price)&&*/i==cat){
                            category= (String) Keys.get(i);
                        }
                        i++;
                }
                }
                totalView1.setText(category+"\nSGD$"+MetaData.df.format(Double.parseDouble(price)).toString());
                query.setSelection(setSelection(category));
            }

            @Override
            public void onNothingSelected() {

            }
        });
        totalView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double sum=0.0;
                if(DataFragment.spinnerChanged==false){
                for(String key:MetaData.catTotal.keySet()){
                    sum+=MetaData.catTotal.get(key);
                }}else {
                    for(String key:MetaData.catTotal1.keySet()){
                        sum+=MetaData.catTotal1.get(key);
                }}
                totalView1.setText("Total\nSGD$"+MetaData.df.format(sum).toString());
                query.setSelection(0);
            }


        });


    }

    public static int setSelection(String category){
        int i;
        switch(category){
            case "All":i=0;
                break;
            case "Food and Drinks":i=1;
                break;
            case "Shopping": i=2;
                break;
            case "Housing":i=3;
                break;
            case "Transport": i=4;
                break;
            case "Vehicle":i=5;
                break;
            case "Life & Entertainment":i=6;
                break;
            case "Communications,PC": i=7;
                break;
            case "Financial Expenses":i=8;
                break;
            case "Investment": i=9;
                break;
            case "Education":i=10;
                break;
            case "Insurance Payment":i=11;
                break;
            case "Transfer-out":i=12;
                break;
            case "Others (Expenditure)":i=13;
                break;
            default:i=13;
        }

        return i;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MetaData.recentTransactionItemList=new ArrayList<>();
        MetaData.recentTransactionItemList.add(
                new RecentTransactionItem(
                        "Phone",
                        "Cash",
                        "-40.00",
                        "Today",
                        "onofwnnfwn",
                        "HAHA",
                        "Expense",
                        R.drawable.ic_phone_android_black_24dp,
                        "iPhone 7"

                ));
        //query.setSelection(1);



    }


    public static void getTransactions(){


        if(querySelection!=query.getSelectedItem().toString()||DataFragment.spinnerChanged==true){
            mLastQuiredDocument=null;
            querySelection = query.getSelectedItem().toString();
            MetaData.recentTransactionItemList.clear();
            recyclerView.removeAllViews();
            //Toast.makeText(getActivity(), "if "+querySelection+" "+query.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(getActivity(), "else", Toast.LENGTH_SHORT).show();
        }

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        collectionNAME="users/"+userID;
        //wallet Changes
        if(MetaData.inwallet==true){
            userID=MetaData.walletName;
            collectionNAME="group/"+userID;
        }

        CollectionReference transacionCollectionRef=db.collection(collectionNAME +"/transactionData");

        Query transactionQuery=null;

            if (mLastQuiredDocument != null) {
                if(!querySelection.equals("All")) {

                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("category", querySelection)
                            .orderBy("timeStamp", Query.Direction.DESCENDING)
                            .startAfter(mLastQuiredDocument);
                }
                if(querySelection.equals("All")){
                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("type", "Expense")
                            .orderBy("timeStamp", Query.Direction.DESCENDING)
                            .startAfter(mLastQuiredDocument);
                }
                /*}*//*else {
                Toast.makeText(getActivity(), "Hahaha", Toast.LENGTH_SHORT).show();
                transactionQuery = transacionCollectionRef
                        .whereEqualTo("account","Cash")
                        .orderBy("timeStamp",Query.Direction.DESCENDING)
                        .startAfter(mLastQuiredDocument);
            }*/

            } else if(mLastQuiredDocument == null){
                if(!querySelection.equals("All")) {
                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("category", querySelection)
                            .orderBy("timeStamp", Query.Direction.DESCENDING);
                }
                if(querySelection.equals("All")){
                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("type", "Expense")
                            .orderBy("timeStamp", Query.Direction.DESCENDING);
                }
                /*}*//*else {
                Toast.makeText(getActivity(), "YeahMan", Toast.LENGTH_SHORT).show();
                transactionQuery=transacionCollectionRef.whereEqualTo("account","Cash")
                        .orderBy("timeStamp",Query.Direction.DESCENDING);
            }*/
            }


            transactionQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //retrieving a list of documents by looping through the list by calling get results
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Transaction transaction = document.toObject(Transaction.class);
                            Log.i("dateFor Use 2",transaction.getTimeStamp().toString());
                            String dateForUse;
                            String[] changeDate=transaction.getTimeStamp().toString().split(" ");
                            String month=changeDate[1];
                            String year=changeDate[5];
                            String monthFromSpinner=DataFragment.monthFromSpinner;
                            String yearFromSpinner=DataFragment.yearFromSpinner;
                            String date= transaction.getTimeStamp().toString();
                            Pattern pattern=Pattern.compile("(.*?):00 GMT");
                            Matcher matcher= pattern.matcher(date);
                            StringBuilder builder = new StringBuilder();

                            while(matcher.find()){
                                System.out.println(matcher.group(1));
                                builder.append(matcher.group(1));
                            }
                            dateForUse=builder.toString();
                            String category = transaction.getCategory();
                            if (category == null){
                                category = "Others (Expenditure)";
                            }
                            String account = transaction.getAccount();
                            if (account == null){
                                account = "Bank";
                            }
                            String amount = transaction.getAmount();
                            if (amount == null){
                                amount = "0";
                            }
                            String name = transaction.getName();
                            if (name == null || name.equals("")) {
                                name = category;
                            }
                            Log.i("DateMonthHere",monthFromSpinner+"   "+month);
                            Log.i("DateMonthHere",yearFromSpinner+"   "+year);
                            if(monthFromSpinner.equals(month)&&yearFromSpinner.equals(year)) {
                                Log.i("DateMonthHere",yearFromSpinner+"Entered"+year);
                                MetaData.recentTransactionItemList.add(new RecentTransactionItem(
                                                category,
                                                account,
                                                amount,
                                                year + " " + dateForUse,
                                                transaction.getTransaction_id(),
                                                transaction.getDescription(),
                                                transaction.getType(),
                                                MetaData.chooseImage(category),
                                                name
                                        )
                                );
                            }
                        }
                        if (task.getResult().size() != 0) {
                            mLastQuiredDocument = task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);
                        }
                        recentTransactionAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(recentTransactionAdapter);

                    } else {
                        //Toast.makeText(getActivity(), "Query Failed. Check Logs", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    }
    /*/
            changes this part for colors to work
             */
    //for pie chart colors
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
    //for Pie chart colors
    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }
    //ends here


    @Override
    public void onRefresh() {
    getTransactions();
    mSwipeRefreshLayout.setRefreshing(false);

    }
}