package com.medical.dialysiscenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.medical.dialysiscenter.admin.AdminDashboard;
import com.medical.dialysiscenter.admin.AdminLogin;
import com.medical.dialysiscenter.tech.TechDashboard;

public class SelectUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_select_user );

        SharedPreferences prefs = getSharedPreferences( "Role", MODE_PRIVATE );
        String name = prefs.getString( "name", "Admin" );
        if (name.equals( "Admin" )) {
            startActivity( new Intent( SelectUser.this, AdminDashboard.class ) );

        } else if (name.equals( "Tech" )) {
            startActivity( new Intent( SelectUser.this, TechDashboard.class ) );
        }
        findViewById( R.id.btnAdminUser ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPrefenrence( "Admin" );
                startActivity( new Intent( SelectUser.this, AdminLogin.class ) );
            }
        } );
        findViewById( R.id.btnTechUser ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addPrefenrence( "Tech" );
                startActivity( new Intent( SelectUser.this, TechDashboard.class ) );
            }
        } );
    }

    private void addPrefenrence(String currentRole) {
        SharedPreferences.Editor editor = getSharedPreferences( "Role", MODE_PRIVATE ).edit();
        editor.putString( "myRole", currentRole );
        editor.apply();
    }
}