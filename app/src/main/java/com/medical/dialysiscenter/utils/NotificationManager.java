package com.medical.dialysiscenter.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NotificationManager {
    static String techName;
    String message, time;

    public NotificationManager(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationManager() {
    }

    public void sendNotif(String userId, String message, String time) {

        Map notificationMap = new HashMap();
        notificationMap.put( "from", userId );
        notificationMap.put( "message", message );
        notificationMap.put( "time", time );
        FirebaseDatabase.getInstance().getReference().child( "Notifications" ).child( "Admin" ).child( time ).setValue( notificationMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        } );
    }

    public String getTechName(String userId) {
        FirebaseDatabase.getInstance().getReference().child( "Techs" ).child( userId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild( "TechName" ))
                    techName = snapshot.child( "TechName" ).getValue().toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
        if (techName == null) {
            return "Admin";
        } else {
            return techName;
        }
    }

    public static String getTechName() {
        return techName;
    }

    public static void setTechName(String techName) {
        NotificationManager.techName = techName;
    }
}
