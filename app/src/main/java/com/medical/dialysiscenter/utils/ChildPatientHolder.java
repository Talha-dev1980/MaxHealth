package com.medical.dialysiscenter.utils;

import android.view.View;
import android.widget.TextView;

import com.medical.dialysiscenter.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChildPatientHolder extends ChildViewHolder {

    public TextView tvPatientId,tvPatientName,tvBill,tvTime;
    long timeInMillis;
    long milliSecPerMinute = 60 * 1000; //Milliseconds Per Minute
    long milliSecPerHour = milliSecPerMinute * 60; //Milliseconds Per Hour
    long milliSecPerDay = milliSecPerHour * 24; //Milliseconds Per Day
    long milliSecPerMonth = milliSecPerDay * 30; //Milliseconds Per Month
    long milliSecPerYear = milliSecPerDay * 365; //Milliseconds Per Year

    public ChildPatientHolder(View itemView) {
        super(itemView);
        tvPatientId = (TextView) itemView.findViewById( R.id.tvPatientIdC);
        tvPatientName = (TextView) itemView.findViewById( R.id.tvPatientNameC);
        tvBill = (TextView) itemView.findViewById( R.id.tvPatientBillC);
        tvTime = (TextView) itemView.findViewById( R.id.tvPatientTimeC);

    }

    public void onBind(String id,String name,String bill,String time) {
        tvPatientId.setText(id);
        tvPatientName.setText(name);
        tvBill.setText(bill+"/-");
        timeInMillis= Long.parseLong( time.trim() );
        tvTime.setText(getTimeFromMillis(timeInMillis)+"");

    }

    private String getTimeFromMillis(long timeInMillis) {
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(timeInMillis);
        result.getTime();
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(result);   // assigns calendar to given date
        return calendar.get(Calendar.HOUR_OF_DAY)+":"+ // gets hour in 24h format
        calendar.get(Calendar.MINUTE)+"";        // gets hour in 12h format



    }


}
