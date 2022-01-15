package com.medical.dialysiscenter.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestModel {
    static int patients=0, earning=0;
    boolean flag = false;

    public static int getPatients() {
        return patients;
    }

    public static void setPatients(int patients) {
        TestModel.patients = patients;
    }

    public static int getEarning() {
        return earning;
    }

    public static void setEarning(int earning) {
        TestModel.earning = earning;
    }
    public String getStringMillisFromDate(String date) {

        Calendar cal = Calendar.getInstance();

        Date date1 = new Date();
        try {
            date1 = new SimpleDateFormat( "dd MMM, yyyy " ).parse( date );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTimeInMillis( date1.getTime() );
        String todayMillis = date1.getTime() + "";
        return todayMillis;
    }

    public void addEarning(String amount, String todayMillis, Context context) {
        getEarningValues( amount, todayMillis );
        if (earning == 0 && patients == 0) {
            getEarningValues( amount, todayMillis );

        } else {


            Map mapCounter = new HashMap();
            mapCounter.put( "earning", (getEarning() + Integer.parseInt( amount )) + "" );
            mapCounter.put( "patients", (getPatients() + 1) + "" );
            FirebaseDatabase.getInstance().getReference().child( "Earnings" ).child( todayMillis ).updateChildren( mapCounter ).addOnCompleteListener( new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            } );
        }

    }

    public boolean getEarningValues(String amount, String todayMillis) {
        FirebaseDatabase.getInstance().getReference().child( "Earnings" ).child( todayMillis ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild( "earning" )) {
                    earning=( Integer.parseInt( snapshot.child( "earning" ).getValue().toString().trim() ) );
                    patients=( Integer.parseInt( snapshot.child( "patients" ).getValue().toString().trim() ) );
                    flag = true;
                } else {
                    Map mapCounter = new HashMap();
                    earning = 0;
                    patients = 0;

                    mapCounter.put( "earning", amount + "" );
                    mapCounter.put( "patients", 1 + "" );
                    FirebaseDatabase.getInstance().getReference().child( "Earnings" ).child( todayMillis ).updateChildren( mapCounter ).addOnCompleteListener( new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            flag = true;
                        }
                    } );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
        return flag;
    }

}
