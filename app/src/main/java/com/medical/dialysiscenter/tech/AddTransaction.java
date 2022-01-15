package com.medical.dialysiscenter.tech;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.medical.dialysiscenter.utils.MeasureToday;
import com.medical.dialysiscenter.utils.MessageDialog;
import com.medical.dialysiscenter.utils.NotificationManager;
import com.medical.dialysiscenter.utils.TestModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTransaction {
    MeasureToday measureToday;
    TestModel model;
    static boolean usedDialyser = false;
    ProgressDialog dialog;

    public AddTransaction() {
        measureToday = new MeasureToday();
        model = new TestModel();
    }

    public void enterTransaction(String userId, String pId, String name, String date, boolean injUsed,boolean isPanel, String amount, Context context) {
        dialog = new ProgressDialog( context );
        dialog.setTitle( "Adding Transaction" );
        dialog.show();
        Calendar cal = Calendar.getInstance();
        String currentTime = cal.getTimeInMillis() + "";
        int day, month, year;
        day = cal.get( Calendar.DAY_OF_MONTH );
        String todayMillis = measureToday.getTodayinMillisec( day );
        Map transacData = new HashMap();
        transacData.put( "id", pId );
        transacData.put( "name", name.toLowerCase() );
        transacData.put( "amount", amount );
        transacData.put( "time", currentTime );
        if (isPanel){

            transacData.put( "panel", isPanel+"" );
        }else{

            transacData.put( "panel", isPanel+"" );
        }
        model.getEarningValues( amount, todayMillis );
        NotificationManager manager = new NotificationManager();
        manager.getTechName( userId );
        FirebaseDatabase.getInstance().getReference().child( "Sales" ).child( todayMillis ).child( userId ).child( pId ).setValue( transacData ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    transacData.put( "tech", userId );
                    FirebaseDatabase.getInstance().getReference().child( "Patients" ).child( pId ).setValue( transacData ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                useDialyser( measureToday.getCount() );
                                if (injUsed) {
                                    useInjection( measureToday.getInjections() );
                                }
                                dialog.dismiss();
                                MessageDialog messageBox = new MessageDialog( "Transaction Added Successfuly", "Success", context );
                                NotificationManager notif = new NotificationManager();
                                String techName = notif.getTechName( userId );
                                String message = techName + " treated " + pId + " " + name + ", get Rs." + amount + "/-";
                                notif.sendNotif( userId, message, measureToday.getStringDateInMillis( measureToday.getCurrentDate() ) );
                                model.addEarning( amount, todayMillis, context );

                            }
                        }
                    } );

                } else {
                    dialog.dismiss();
                    MessageDialog message = new MessageDialog( "Transaction not added", "Failed", context );

                }
            }
        } );

    }

    public boolean useDialyser(int count) {
        Map map = new HashMap();
        map.put( "dCount", (count - 1) + "" );
        // map.put( "injCount", (injection - 1) + "" );
        FirebaseDatabase.getInstance().getReference().child( "Dialysers" ).child( "Admin" ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    usedDialyser = true;
                }
            }
        } );
        return usedDialyser;
    }

    public boolean useInjection(int injection ) {
        Map map = new HashMap();
        map.put( "injCount", (injection - 1) + "" );
        FirebaseDatabase.getInstance().getReference().child( "Dialysers" ).child( "Admin" ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    usedDialyser = true;
                }
            }
        } );
        return usedDialyser;
    }


}
