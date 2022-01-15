package com.medical.dialysiscenter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.medical.dialysiscenter.admin.AdminDashboard;
import com.medical.dialysiscenter.admin.AdminLogin;
import com.medical.dialysiscenter.utils.MeasureToday;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPass;
    private Button btnLogin, btnSignUp;
    private FirebaseAuth mAuth;
    private TextView tvMsgApp,tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
    try{    edtEmail = (EditText) findViewById( R.id.edtEmailAdminSignin );
        edtName = (EditText) findViewById( R.id.edtNameAdminSignIn );
        edtPass = (EditText) findViewById( R.id.edtPassAdminSignin );
        tvError = (TextView) findViewById( R.id.tvAdminSigupError );
        btnLogin = (Button) findViewById( R.id.btnLoginAdminSign );
        btnSignUp = (Button) findViewById( R.id.btnSigninAdminSignin );
        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        } );
        btnSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String name = edtName.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                if (!name.isEmpty() && !email.isEmpty() && pass.length() >= 6) {
                    signUpUser( name, pass, email );
                } else {
                    tvError.setVisibility(  View.VISIBLE );
                    tvError.setText( "Please enter valid data" ); edtEmail.setText( "" );
                    edtPass.setText( "" );
                }
            }
        } );
        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( MainActivity.this, AdminLogin.class ) );
            }
        } );
    } catch (Exception e) {
        Log.e( "MainActivity", e.getMessage() );
    }
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
                            tvError.setVisibility(  View.VISIBLE );
                            tvError.setText( "Login Failed" );
                        }
                    }
                } );
    }

    private void updateUI(FirebaseUser user, String name, String email) {
        if (user != null) {
            MeasureToday measureToday = new MeasureToday();
            Map map = new HashMap();
            map.put( "name", name );
            map.put( "email", email );
            map.put( "time", measureToday.getTodayinMillisec( measureToday.getCurrentDay() ) );
            FirebaseDatabase.getInstance().getReference().child( "Admin" ).child( user.getUid().toString() ).setValue( map ).addOnCompleteListener( new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        startActivity( new Intent( MainActivity.this, AdminDashboard.class ) );
                        finish();
                    }

                }
            } );
        }
    }


}