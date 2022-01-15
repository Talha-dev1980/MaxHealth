package com.medical.dialysiscenter.tech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.MeasureToday;

import java.util.HashMap;
import java.util.Map;

public class SignUpTech extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPass;
    private Button btnLogin, btnSignUp;
    private FirebaseAuth mAuth;
    private RelativeLayout lytMain;
    private TextView tvMsgApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up_tech );
        edtEmail = (EditText) findViewById( R.id.edtTechEmailSignIn );
        edtName = (EditText) findViewById( R.id.edtTechName );
        edtPass = (EditText) findViewById( R.id.edtTechSignInPass );
        btnLogin = (Button) findViewById( R.id.btnLoginTechSignIn );
        btnSignUp = (Button) findViewById( R.id.btnApplyTech );
        tvMsgApp = (TextView) findViewById( R.id.titleMsgApplication );
        lytMain = (RelativeLayout) findViewById( R.id.lytMainSignIn );

        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        } );
       /* Intent it = getIntent();
        String data = it.getStringExtra( "logged" );
        if (data.equals( "notApproved" )) {
            lytMain.setVisibility( View.INVISIBLE );
            tvMsgApp.setVisibility( View.VISIBLE );

        } else if (data.equals( "requestApprovedLoginNeed" )) {
            lytMain.setVisibility( View.INVISIBLE );
            tvMsgApp.setVisibility( View.VISIBLE );
            tvMsgApp.setText( "Request accepted, You can Login Now" );
        } else {
            lytMain.setVisibility( View.VISIBLE );
            tvMsgApp.setVisibility( View.INVISIBLE );
        }*/
        btnSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String name = edtName.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                if (!name.isEmpty() && !email.isEmpty() && pass.length() >= 6) {
                    signUpUser( name, pass, email );
                } else {
                    Toast.makeText( SignUpTech.this, "Please Enter valid data", Toast.LENGTH_LONG ).show();
                    edtEmail.setText( "" );
                    edtPass.setText( "" );
                }
            }
        } );
        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( SignUpTech.this, LoginTech.class ) );
            }
        } );
    }

    private void signUpUser(final String name, String password, final String email) {
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI( user, name, email );
                        } else {
                            Log.e( "SignUpTech","Authentication failed" );
                        }
                    }
                } );
    }

    private void updateUI(FirebaseUser user, String name, String email) {
        if (user != null) {
            MeasureToday measureToday = new MeasureToday();
            Map map = new HashMap();
            map.put( "name", name );
            map.put( "request", "recieved" );
            map.put( "email", email );
            map.put( "time", measureToday.getTodayinMillisec( measureToday.getCurrentDay() ) );
            Map mapTech = new HashMap();
            mapTech.put( "TechName", name );
                        FirebaseDatabase.getInstance().getReference().child( "Techs" ).child( user.getUid().toString() ).updateChildren( mapTech ).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                findViewById( R.id.lytMainSignIn ).setVisibility( View.INVISIBLE );
                                findViewById( R.id.titleMsgApplication ).setVisibility( View.VISIBLE );
                            }
                        } );


                    /*FirebaseDatabase.getInstance().getReference().child( "TechRequests" ).child( user.getUid().toString() ).setValue( map ).addOnCompleteListener( new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Map mapTech = new HashMap();
                        mapTech.put( "TechName", name );*/


        } else {
            Log.e( "SignUpTech","Authentication failed" );
        }
    }


}