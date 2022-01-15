package com.medical.dialysiscenter.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MeasureToday {
    static int count, patients, injections;

    public static int getPatients() {
        return patients;
    }

    public static void setPatients(int patients) {
        MeasureToday.patients = patients;
    }

    public static int getCount() {
        return count;
    }

    public static int getInjections() {
        return injections;
    }

    public static void setInjections(int injections) {
        MeasureToday.injections = injections;
    }

    public static void setCount(int count) {
        MeasureToday.count = count;
    }

    public MeasureToday() {
    }

    String[] dayArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    int mYear, mMonth, mDay;

    public String getDayByIndex(int x) {
        return dayArray[x];
    }

    public int getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        int day, month, year;
        day = cal.get( Calendar.DAY_OF_MONTH );

        return day;
    }

    public String getTodayinMillisec(int day) {

        Calendar cal = Calendar.getInstance();
        int month, year;
        month = cal.get( Calendar.MONTH ) + 1;
        year = cal.get( Calendar.YEAR );
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat( "dd-MM-yyyy" ).parse( day + "-" + month + "-" + year );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime( date1 );
        String todayMillis = cal.getTimeInMillis() + "";
        return todayMillis;
    }

    public String getStringDateInMillis(String date) {

        Calendar cal = Calendar.getInstance();

        Date date1 = new Date();
        try {
            date1 = new SimpleDateFormat( "dd MMM, yyyy " ).parse( date );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime( date1 );
        String todayMillis = cal.getTimeInMillis() + "";
        return todayMillis;
    }

    public String getStringMillisFromDate(String date) {

        // Calendar cal = Calendar.getInstance();

        Date date1 = new Date();
        try {
            date1 = new SimpleDateFormat( "dd Mon, yyyy " ).parse( date.trim() );
        } catch (ParseException e) {
            Log.e( "parsingError", e.getMessage() );
        }
        //  cal.setTimeInMillis( date1.getTime() );
        String todayMillis = date1.getTime() + "";
        return todayMillis;
    }

    public String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get( Calendar.YEAR );
        mMonth = c.get( Calendar.MONTH );
        mDay = c.get( Calendar.DAY_OF_MONTH );
        return (mDay + " " + monthArray[mMonth] + ", " + mYear);
    }

    public String setStringDateInFormat(int day, int month, int year) {
        return day + " " + monthArray[month] + ", " + year;
    }

    public String getTimeFromMillis(long timeInMillis) {
        Date result = new Date( timeInMillis );
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime( result );   // assigns calendar to given date
        return setStringDateInFormat( calendar.get( Calendar.DAY_OF_MONTH ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.YEAR ) ) + " " + calendar.get( Calendar.HOUR_OF_DAY ) + ":" + // gets hour in 24h format
                calendar.get( Calendar.MINUTE ) + "";
    }

    public String getDateFromMillis(long timeInMillis) {
        Date result = new Date( timeInMillis );
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime( result );   // assigns calendar to given date
        return setStringDateInFormat( calendar.get( Calendar.DAY_OF_MONTH ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.YEAR ) ) + "";
    }

    public String getBeforeDay(long timeInMillis) {
        Date result = new Date( timeInMillis );
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime( result );   // assigns calendar to given date
        return calendar.get( Calendar.DAY_OF_MONTH ) + "-" + (calendar.get( Calendar.MONTH ) + 1) + "-" + calendar.get( Calendar.YEAR );

    }

    public String getMillisToDate(long timeInMillis) {
        DateFormat simple = new SimpleDateFormat( "dd MMM yyyy HH:mm:ss:SSS Z" );

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date( timeInMillis );
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime( result );   // assigns calendar to given date
        return setStringDateInFormat( calendar.get( Calendar.DAY_OF_MONTH ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.YEAR ) )
                + " ";        // gets hour in 12h format

    }

    public String getMillis(String startingDate) {
        String someDate = "05.10.2011";
        SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy" );
        Date date = null;
        try {
            date = sdf.parse( startingDate );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime() + "";
    }
}
