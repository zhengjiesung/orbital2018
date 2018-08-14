package com.montethecat.scroogev2;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class CashflowsFragment2 extends Fragment {

    public static BarChart barChart;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cashflows2, null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = (BarChart) getView().findViewById(R.id.barGraph);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);


        MainActivity.barEntries2.add(new BarEntry(1,MainActivity.barEntries4.get(6).getY()));
        MainActivity.barEntries2.add(new BarEntry(2,MainActivity.barEntries4.get(7).getY()));
        MainActivity.barEntries2.add(new BarEntry(3,MainActivity.barEntries4.get(8).getY()));
        MainActivity.barEntries2.add(new BarEntry(4,MainActivity.barEntries4.get(9).getY()));
        MainActivity.barEntries2.add(new BarEntry(5,MainActivity.barEntries4.get(10).getY()));
        MainActivity.barEntries2.add(new BarEntry(6,MainActivity.barEntries4.get(11).getY()));

        BarDataSet barDataSet = new BarDataSet(MainActivity.barEntries2, "Cash Inflows");
        barDataSet.setColors(Color.rgb(254, 247, 120));

        MainActivity.barEntries3.add(new BarEntry(1,MainActivity.barEntries5.get(6).getY()));
        MainActivity.barEntries3.add(new BarEntry(2,MainActivity.barEntries5.get(7).getY()));
        MainActivity.barEntries3.add(new BarEntry(3,MainActivity.barEntries5.get(8).getY()));
        MainActivity.barEntries3.add(new BarEntry(4,MainActivity.barEntries5.get(9).getY()));
        MainActivity.barEntries3.add(new BarEntry(5,MainActivity.barEntries5.get(10).getY()));
        MainActivity.barEntries3.add(new BarEntry(6,MainActivity.barEntries5.get(11).getY()));

        BarDataSet barDataSet1 = new BarDataSet(MainActivity.barEntries3, "Cash Outflows");
        barDataSet1.setColors(Color.rgb(254, 149, 7));

        BarData data = new BarData(barDataSet, barDataSet1);

        float groupSpace = 0.04f;
        float barSpace = 0.03f;
        float barWidth = 0.45f;

        barChart.setData(data);

        data.setBarWidth(barWidth);
        barChart.groupBars(1, groupSpace, barSpace);


        String[] months = new String[] {MainActivity.prev10Month,MainActivity.prev5Month,MainActivity.prev4Month,MainActivity.prev3Month,MainActivity.prev2Month,MainActivity.prevMonth,MainActivity.currMonth,MainActivity.prev2Month , MainActivity.prevMonth, MainActivity.currMonth};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisVlaueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(7);

        /*Calendar now = Calendar.getInstance();

        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};

        final String currMonth = monthName[now.get(Calendar.MONTH)];
        final String prevMonth = monthName[now.get(Calendar.MONTH) - 1];
        final String prev2Month = monthName[now.get(Calendar.MONTH) - 2];

        month3 = new ArrayList<String>();
        month3.add(prev2Month);
        month3.add(prevMonth);
        month3.add(currMonth);
        Log.i("Months",currMonth);
        Log.i("Months",prevMonth);
        Log.i("Months",prev2Month);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {   //Do something after 100ms
                //retrieveFile(fileName);
                barEntries = new ArrayList<>();
                barEntries1 = new ArrayList<>();

                int count = 1;
                for(String temp: month3){
                    double currAmt = 0.00;
                    double currAmt2 = 0.00;
                    for(Transaction curr: incomeArray){
                        String date = curr.getTimeStamp().toString().substring(4,7);
                        if(date.equals(temp)){
                            currAmt += Double.parseDouble(curr.getAmount());
                            Log.i("curr amount1", curr.getAmount());
                        }
                    }
                    for(Transaction curr: expenseArray){
                        String date2 = curr.getTimeStamp().toString().substring(4,7);
                        if(date2.equals(temp)){
                            currAmt2 += Double.parseDouble(curr.getAmount());
                            Log.i("curr amount2", curr.getAmount());
                        }
                    }
                    Log.i("current month", temp);
                    Log.i("total amount1", Double.toString(currAmt));
                    Log.i("total amount2", Double.toString(currAmt2));
                    barEntries.add(new BarEntry(count, (int)currAmt));
                    barEntries1.add(new BarEntry(count, (int)currAmt2));
                    count++;
                }
                barChart = (BarChart) getView().findViewById(R.id.barGraph);

                barChart.setDrawBarShadow(false);
                barChart.setDrawValueAboveBar(true);
                barChart.setMaxVisibleValueCount(50);
                barChart.setPinchZoom(false);
                barChart.setDrawGridBackground(true);

                BarDataSet barDataSet = new BarDataSet(barEntries, "Cash Inflows");
                barDataSet.setColors(Color.rgb(254, 247, 120));

                BarDataSet barDataSet1 = new BarDataSet(barEntries1, "Cash Outflows");
                barDataSet1.setColors(Color.rgb(254, 149, 7));

                BarData data = new BarData(barDataSet, barDataSet1);

                float groupSpace = 0.04f;
                float barSpace = 0.03f;
                float barWidth = 0.45f;

                barChart.setData(data);

                data.setBarWidth(barWidth);
                barChart.groupBars(1, groupSpace, barSpace);


                String[] months = new String[] {"blank",prev2Month , prevMonth, currMonth,"blank","blank","blank"};
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new CashflowsFragment.MyXAxisVlaueFormatter(months));
                xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                xAxis.setGranularity(1);
                xAxis.setCenterAxisLabels(true);
                xAxis.setAxisMinimum(1);
                xAxis.setAxisMaximum(4);


            }
        }, 0);
        /*ExpenseQuery.orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        expenseArray.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Transaction transaction = document.toObject(Transaction.class);
                            expenseArray.add(transaction);
                        }
                    }
                });*/

        //For Transaction Data




        /*barEntries.add(new BarEntry(1, 40f));
        barEntries.add(new BarEntry(2, 44f));
        barEntries.add(new BarEntry(3, 30f));



        barEntries1.add(new BarEntry(1, 44f));
        barEntries1.add(new BarEntry(2, 54f));
        barEntries1.add(new BarEntry(3, 60f));*/

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

}
