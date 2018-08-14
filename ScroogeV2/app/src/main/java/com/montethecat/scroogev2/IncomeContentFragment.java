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
import android.widget.Spinner;
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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncomeContentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {
        private static RecyclerView recyclerViewIncome;
        public static RecentTransactionAdapter recentTransactionAdapterIncome;
        private RecentTransactionAdapter recentTransactionAdapter2Income;
        public static DocumentReference mDocRef;
        View view;
        public static PieChart pieChartIncome;
        private SwipeRefreshLayout mSwipeRefreshLayoutIncome;
        static Spinner queryIncome;
        static String querySelectionIncome;
        public static TextView totalView1Income;
        static String collectionNAME;
        static DocumentSnapshot mLastQuiredDocument;
        //7/15/2018
        public static PieDataSet dataSet;
        public static ArrayList<PieEntry> yValuesIncome;
        public static int[] colorIncome;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view= inflater.inflate(R.layout.incomecontent,container,false);
            //7/15/2018
            DataFragment.spinnerChangedIncome=false;
            Log.i("HashMap2", String.valueOf(MetaData.getCatTotal()));
            queryIncome = (Spinner) view.findViewById(R.id.querySpinnerIncome);
            recyclerViewIncome=(RecyclerView) view.findViewById(R.id.recyclerViewRecentTransaction2Income);
            recentTransactionAdapterIncome=new RecentTransactionAdapter(getContext(),MetaData.recentIncomeItemList);
            RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getActivity());
            recyclerViewIncome.setLayoutManager(mLayoutManager);
            recyclerViewIncome.setAdapter(recentTransactionAdapterIncome);

            mSwipeRefreshLayoutIncome=view.findViewById(R.id.swipe_viewIncome);
            MetaData.recentIncomeItemList.clear();
            recyclerViewIncome.removeAllViews();

            queryIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
           totalView1Income=getView().findViewById(R.id.totalView1Income);
            pieChartIncome = getView().findViewById(R.id.pieChart2Income);

            pieChartIncome.setUsePercentValues(true);
            pieChartIncome.getDescription().setEnabled(false);
            pieChartIncome.setExtraOffsets(5, 10, 5, 5);

            pieChartIncome.setDragDecelerationFrictionCoef(0.99f);
            pieChartIncome.setDrawHoleEnabled(true);
            pieChartIncome.setHoleColor(Color.WHITE);
            pieChartIncome.setTransparentCircleRadius(60f);
            Double suminc=0.0;
            if(DataFragment.spinnerChangedIncome==false){
                for(String key:MetaData.incTotal.keySet()){
                    suminc+=MetaData.incTotal.get(key);
                }}else {
                for(String key:MetaData.incTotal1.keySet()){
                    suminc+=MetaData.incTotal1.get(key);
                }}
            IncomeContentFragment.totalView1Income.setText("Total\nSGD$"+MetaData.df.format(suminc).toString());









            pieChartIncome.setEntryLabelTextSize(10f);

            Description description = new Description();

            pieChartIncome.animateY(1000, Easing.EasingOption.EaseInOutCubic);

            PieDataSet dataSet = new PieDataSet(ExpenditureIncomeFragment.yValuesIncome, "Countries");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            /*/
            changes this part for colors to work
            */

            //7/15/2018
            for (String key: MetaData.incTotal.keySet()){
                MetaData.colorIncome.add(rgb(MetaData.chooseColor(key)));
            }
            //7/15/2018
            yValuesIncome=new ArrayList<>();
            dataSet.setColors(MetaData.colorIncome);



            //dataSet.setColors(convertIntegers(colorExpense));
            //Ends here


            PieData data = new PieData(dataSet);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.YELLOW);
            pieChartIncome.setData(data);
            pieChartIncome.getLegend().setEnabled(false);

            final String[] categorySpinner = new String[]{"All","Salary","Business","Loan","Parental Leave","Insurance Payout","Transfer-in", "Others (Income)"};
            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
            //There are multiple variations of this, but this is the basic variant.
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, categorySpinner);
            //set the spinners adapter to the previously created one.
            queryIncome.setAdapter(adapter);


            mSwipeRefreshLayoutIncome.setOnRefreshListener(this);
            pieChartIncome.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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



                    int i = 0;
                    if (DataFragment.spinnerChangedIncome==false){
                        List Keys= new ArrayList(MetaData.incTotal.keySet());
                    for(String key:MetaData.incTotal.keySet()){
                        if(/*MetaData.catTotal.get(key)==Double.parseDouble(price)&&*/i==cat){
                            category= (String) Keys.get(i);
                        }
                        i++;
                    }}else {
                        List Keys= new ArrayList(MetaData.incTotal1.keySet());
                        for(String key:MetaData.incTotal1.keySet()){
                            if(/*MetaData.catTotal.get(key)==Double.parseDouble(price)&&*/i==cat){
                                category= (String) Keys.get(i);
                            }
                            i++;
                        }
                    }
                    totalView1Income.setText(category+"\nSGD$"+MetaData.df.format(Double.parseDouble(price)).toString());
                    queryIncome.setSelection(setSelection(category));
                }

                @Override
                public void onNothingSelected() {

                }
            });
            totalView1Income.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double sum=0.0;
                    if(DataFragment.spinnerChangedIncome==false){
                    for(String key:MetaData.incTotal.keySet()){
                        sum+=MetaData.incTotal.get(key);
                    }}else {
                        for(String key:MetaData.incTotal1.keySet()){
                            sum+=MetaData.incTotal1.get(key);
                    }}
                    totalView1Income.setText("Total\nSGD$"+MetaData.df.format(sum).toString());
                    queryIncome.setSelection(0);
                }


            });
        }

        public static int setSelection(String category){
            int i;
            switch(category){
                case "All":i=0;
                    break;
                case "Salary":i=1;
                    break;
                case "Business": i=2;
                    break;
                case "Loan":i=3;
                    break;
                case "Parental Leave": i=4;
                    break;
                case "Insurance Payout":i=5;
                    break;
                case "Transfer-in":i=6;
                    break;
                case "Others (Income)":i=7;
                    break;
                default:i=7;
            }

            return i;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            MetaData.recentIncomeItemList=new ArrayList<>();
            MetaData.recentIncomeItemList.add(
                    new RecentTransactionItem(
                            "Phone",
                            "Cash",
                            "-40.00",
                            "Today",
                            "uvsicvwvucbdw",
                            "HAHA",
                            "Income",
                            R.drawable.ic_phone_android_black_24dp,
                            "iPhone7"
                    ));
            //query.setSelection(1);



        }


        public static void getTransactions(){


            if(querySelectionIncome!=queryIncome.getSelectedItem().toString()||DataFragment.spinnerChanged==true){
                mLastQuiredDocument=null;
                querySelectionIncome = queryIncome.getSelectedItem().toString();
                MetaData.recentIncomeItemList.clear();
                recyclerViewIncome.removeAllViews();
                //Toast.makeText(getActivity(), "if "+querySelectionIncome+" "+queryIncome.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
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
                if(!querySelectionIncome.equals("All")) {

                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("category", querySelectionIncome)
                            .orderBy("timeStamp", Query.Direction.DESCENDING)
                            .startAfter(mLastQuiredDocument);
                }
                if(querySelectionIncome.equals("All")){
                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("type", "Income")
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

                } else if(mLastQuiredDocument == null) {
                if (!querySelectionIncome.equals("All")) {
                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("category", querySelectionIncome)
                            .orderBy("timeStamp", Query.Direction.DESCENDING);
                }
                if (querySelectionIncome.equals("All")) {
                    transactionQuery = transacionCollectionRef
                            .whereEqualTo("type", "Income")
                            .orderBy("timeStamp", Query.Direction.DESCENDING);
                }
            }


                transactionQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //retrieving a list of documents by looping through the list by calling get results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                Log.i("dateFor Use 2", transaction.getTimeStamp().toString());
                                String dateForUse;
                                String[] changeDate = transaction.getTimeStamp().toString().split(" ");
                                String month = changeDate[1];
                                String year = changeDate[5];
                                String monthFromSpinner = DataFragment.monthFromSpinner;
                                String yearFromSpinner = DataFragment.yearFromSpinner;
                                String date = transaction.getTimeStamp().toString();
                                Pattern pattern = Pattern.compile("(.*?):00 GMT");
                                Matcher matcher = pattern.matcher(date);
                                StringBuilder builder = new StringBuilder();

                                while (matcher.find()) {
                                    System.out.println(matcher.group(1));
                                    builder.append(matcher.group(1));
                                }
                                dateForUse = builder.toString();
                                String category = transaction.getCategory();
                                if (category == null) {
                                    category = "Others (Expenditure)";
                                }
                                String account = transaction.getAccount();
                                if (account == null) {
                                    account = "Bank";
                                }
                                String amount = transaction.getAmount();
                                if (amount == null) {
                                    amount = "0";
                                }
                                String name = transaction.getName();
                                if (name == null || name.equals("")) {
                                    name = category;
                                }
                                if (monthFromSpinner.equals(month) && yearFromSpinner.equals(year)) {
                                    MetaData.recentIncomeItemList.add(new RecentTransactionItem(
                                                    category,
                                                    account,
                                                    amount,
                                                    year + " " + dateForUse,
                                                    transaction.getTransaction_id(),
                                                    transaction.getDescription(),
                                                    transaction.getType(),
                                                    MetaData.chooseImage(transaction.getCategory().toString()),
                                                    name
                                            )
                                    );

                                }
                            }
                            if (task.getResult().size() != 0) {
                                mLastQuiredDocument = task.getResult().getDocuments()
                                        .get(task.getResult().size() - 1);
                            }
                            recentTransactionAdapterIncome.notifyDataSetChanged();
                            recyclerViewIncome.setAdapter(recentTransactionAdapterIncome);

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
            mSwipeRefreshLayoutIncome.setRefreshing(false);

        }

    }
