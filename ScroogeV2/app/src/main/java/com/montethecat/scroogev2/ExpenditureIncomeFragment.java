package com.montethecat.scroogev2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExpenditureIncomeFragment extends Fragment {

    public static PieChart pieChart;
    public static PieChart pieChartIncome;
    public static ArrayList<PieEntry> yValues;
    public static ArrayList<PieEntry> yValuesIncome;
    //added This For pie chart colors
    public static int[] colorExpense;
    public static int[] colorIncome;
    public static PieDataSet dataSet;
    public static PieDataSet dataSetIncome;
    //ends here

    TextView totalView, monthYearExpenditureIncomeFragment;
    Button expensePie, incomePie;
    Calendar now = Calendar.getInstance();
    public static boolean expenditureClicked=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditureincome, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalView=getView().findViewById(R.id.totalView);
        pieChart = getView().findViewById(R.id.pieChart);
        expensePie=getView().findViewById(R.id.expensePie);
        incomePie=getView().findViewById(R.id.incomePie);
        pieChartIncome=getView().findViewById(R.id.pieChartIncome);
        monthYearExpenditureIncomeFragment = getView().findViewById(R.id.monthYearExpenditureIncomeFragment);


        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        String month_str = MetaData.setMonth(month);


        monthYearExpenditureIncomeFragment.setText("Current Month: " + month_str + " " + Integer.toString(year));


        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.99f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);

        yValues = new ArrayList<>();

        //added this for pie chart colors
        int[] colorExpense=new int[MetaData.catTotal.size()];
        int n=0;
        for(String key:MetaData.catTotal.keySet()){
            yValues.add(new PieEntry(Float.parseFloat(MetaData.catTotal.get(key).toString()),key));
            colorExpense[n]=rgb(MetaData.chooseColor(key));
            n++;
        }


        pieChart.setEntryLabelTextSize(10f);

        Description description = new Description();

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(yValues, "Countries");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colorExpense);
        //dataSet.setColors(colorExpense);
        //Ends here
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        pieChart.getLegend().setEnabled(false);
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
                List Keys= new ArrayList(MetaData.catTotal.keySet());


                int i = 0;
                for(String key:MetaData.catTotal.keySet()){
                    if(/*MetaData.catTotal.get(key)==Double.parseDouble(price)&&*/i==cat){
                        category= (String) Keys.get(i);
                    }
                    i++;
                }
                totalView.setText(category+"\nSGD$"+MetaData.df.format(Double.parseDouble(price)).toString());

            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChartIncome.setUsePercentValues(true);
        pieChartIncome.getDescription().setEnabled(false);
        pieChartIncome.setExtraOffsets(5, 10, 5, 5);

        pieChartIncome.setDragDecelerationFrictionCoef(0.99f);
        pieChartIncome.setDrawHoleEnabled(true);
        pieChartIncome.setHoleColor(Color.WHITE);
        pieChartIncome.setTransparentCircleRadius(60f);

        yValuesIncome = new ArrayList<>();
        //added this for pie chart colors
        int []colorIncome=new int[MetaData.incTotal.size()];
        int J=0;
        for(String key:MetaData.incTotal.keySet()){
            yValuesIncome.add(new PieEntry(Float.parseFloat(MetaData.incTotal.get(key).toString()),key));
            colorIncome[J]=rgb(MetaData.chooseColor(key));
            J++;
        }


        pieChartIncome.setEntryLabelTextSize(10f);


        pieChartIncome.setDescription(description);

        pieChartIncome.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSetIncome = new PieDataSet(yValuesIncome, "Countries");
        dataSetIncome.setSliceSpace(3f);
        dataSetIncome.setSelectionShift(5f);

        dataSetIncome.setColors(colorIncome);
        //ends here

        PieData dataIncome = new PieData(dataSetIncome);
        dataIncome.setValueTextSize(10f);
        dataIncome.setValueTextColor(Color.YELLOW);

        pieChartIncome.setData(dataIncome);
        pieChartIncome.getLegend().setEnabled(false);
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
                List Keys= new ArrayList(MetaData.incTotal.keySet());


                int i = 0;
                for(String key:MetaData.incTotal.keySet()){
                    if(/*MetaData.catTotal.get(key)==Double.parseDouble(price)&&*/i==cat){
                        category= (String) Keys.get(i);
                    }
                    i++;
                }
                totalView.setText(category+"\nSGD$"+MetaData.df.format(Double.parseDouble(price)).toString());

            }

            @Override
            public void onNothingSelected() {

            }
        });

        expensePie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieChart.setVisibility(View.VISIBLE);
                pieChartIncome.setVisibility(View.GONE);
                expenditureClicked=true;
                Double sum = 0.0;
                for (String key : MetaData.catTotal.keySet()) {
                    sum += MetaData.catTotal.get(key);
                }
                totalView.setText("Total\n$SGD" + MetaData.df.format(sum).toString());

            }
        });
        incomePie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieChart.setVisibility(View.GONE);
                pieChartIncome.setVisibility(View.VISIBLE);
                expenditureClicked=false;
                Double sum = 0.0;
                for (String key : MetaData.incTotal.keySet()) {
                    sum += MetaData.incTotal.get(key);
                }
                totalView.setText("Total\nSGD$" + MetaData.df.format(sum).toString());
            }
        });

            totalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double sum = 0.0;
                    if (expenditureClicked == true) {
                        for (String key : MetaData.catTotal.keySet()) {
                            sum += MetaData.catTotal.get(key);
                        }}else {
                        for (String key : MetaData.incTotal.keySet()) {
                            sum += MetaData.incTotal.get(key);
                        }
                    } totalView.setText("Total\nSGD$" + MetaData.df.format(sum).toString());
                }


            });

    }
    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

}


