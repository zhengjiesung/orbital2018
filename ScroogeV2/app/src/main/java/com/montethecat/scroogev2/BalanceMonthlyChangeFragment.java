package com.montethecat.scroogev2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BalanceMonthlyChangeFragment extends Fragment {

    BarChart barChart;
    TextView balanceAndMontlyChangeTitle2;
    public static TextView bankAmt;
    public static TextView bankChangeAmt;
    public static TextView creditAmt;
    public static TextView creditChangeAmt;
    public static TextView cashAmt;
    public static TextView cashChangeAmt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_balancemonthlychange, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        balanceAndMontlyChangeTitle2=(TextView)view.findViewById(R.id.balanceAndMontlyChangeTitle2);
        balanceAndMontlyChangeTitle2.setText("Current Month: " + MetaData.setMonth(MainActivity.monthCurrently)+" "+MainActivity.yearCurrently);

        bankAmt = view.findViewById(R.id.bankAmt);
        bankChangeAmt = view.findViewById(R.id.bankChangeAmt);
        creditAmt = view.findViewById(R.id.creditAmt);
        creditChangeAmt = view.findViewById(R.id.creditChangeAmt);
        cashAmt = view.findViewById(R.id.cashAmt);
        cashChangeAmt = view.findViewById(R.id.cashChangeAmt);

        MainActivity.updateOverviewBalance();


        // BarCharts code
        /*barChart = (BarChart) getView().findViewById(R.id.barGraph);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1, 40f));
        barEntries.add(new BarEntry(2, 44f));
        barEntries.add(new BarEntry(3, 30f));
        barEntries.add(new BarEntry(4, 36f));

        ArrayList<BarEntry> barEntries1 = new ArrayList<>();

        barEntries1.add(new BarEntry(1, 44f));
        barEntries1.add(new BarEntry(2, 54f));
        barEntries1.add(new BarEntry(3, 60f));
        barEntries1.add(new BarEntry(4, 31f));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Data Set1");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarDataSet barDataSet1 = new BarDataSet(barEntries1, "Data Set1");
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(barDataSet, barDataSet1);

        float groupSpace = 0.1f;
        float barSpace = 0.02f;
        float barWidth = 0.43f;

        barChart.setData(data);

        data.setBarWidth(barWidth);
        barChart.groupBars(1, groupSpace, barSpace);


        String[] months = new String[] {"Jan", "Feb", "Mar", "April", "May", "Jun"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisVlaueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(1);*/




    }

    /*public class MyXAxisVlaueFormatter implements IAxisValueFormatter{

        private String[] mValues;

        public MyXAxisVlaueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }*/
}


