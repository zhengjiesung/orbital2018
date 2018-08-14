package com.montethecat.scroogev2;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.TimeZone;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    TextView timeTextView;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        timeTextView=(TextView)getActivity().findViewById(R.id.timeTextView);
        TimeZone timeZone=TimeZone.getDefault();
        final Calendar c=Calendar.getInstance(timeZone);
        int hour=c.get(Calendar.HOUR_OF_DAY)+8;
        int minute=c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),this,hour,minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        addTransactionFragment.timeSet=true;
        if(hour<10){
            if(min<10){
                MetaData.date+="0"+(hour-8)+":0"+min+":00Z";
            }else{
                MetaData.date+="0"+(hour-8)+":"+min+":00Z";
            }
        }else {
            if(min<10){
                MetaData.date+=(hour-8)+":0"+min+":00Z";
            }else{
                MetaData.date+=(hour-8)+":"+min+":00Z";
            }
        }

        timeTextView.setText(hour+":"+min);


    }
}
