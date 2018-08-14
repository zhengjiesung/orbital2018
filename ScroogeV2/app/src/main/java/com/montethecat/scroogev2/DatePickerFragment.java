package com.montethecat.scroogev2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    TextView textView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        textView=(TextView)getActivity().findViewById(R.id.dateTextView);
        final Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),R.style.datepicker,this,year,month,day);
    }
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        populateSetDate(year, month,date);
    }

    public void populateSetDate(int year, int month, int day){
//+1 is necessary this is called when the datepicker fragment is activated
        MetaData.date=year+"-"+(month+1)+"-"+day+"T";
       textView.setText(day+MetaData.setMonth(month)+year);

    }

}
