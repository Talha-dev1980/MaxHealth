package com.medical.dialysiscenter.tech;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.MessageDialog;

import java.util.HashMap;
import java.util.Map;

public class LoginTech extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPass;
    private Button btnLoginTech;
    private TextView tvForget,tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login_tech );
        mAuth = FirebaseAuth.getInstance();
        edtEmail = (EditText) findViewById( R.id.edtTechEmailLogIn );
        edtPass = (EditText) findViewById( R.id.edtPassLoginTech );
        tvForget = (TextView) findViewById( R.id.btnForgetPassTech );
        tvError = (TextView) findViewById( R.id.tvTechLoginError );
        btnLoginTech = (Button) findViewById( R.id.btnLogInTechLogin );

        tvForget.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                if (email.length() > 5) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail( email )
                            .addOnCompleteListener( new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        MessageDialog messageDialog = new MessageDialog( "Password reset link sent check mail box", "Email Sent", LoginTech.this );
                                    } else {
                                       tvError.setVisibility( View.VISIBLE );
                                    tvError.setText( "Email not sent" );
                                    }
                                }
                            } );
                }
            }
        } );
        findViewById( R.id.btnSignInTechL ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent( LoginTech.this, SignUpTech.class );
                it.putExtra( "logged", "new" );
                //startActivity( it );
            }
        } );
        btnLoginTech.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                if (!email.isEmpty() && pass.length() >= 6) {
                    loginUser( email, pass );

                } else {
                    tvError.setVisibility( View.VISIBLE );
                    tvError.setText( "Please enter valid data" );  edtEmail.setText( "" );
                    edtPass.setText( "" );
                }
            }
        } );

    }

    private void queryEligibility(String email, String pass) {
        FirebaseDatabase.getInstance().getReference().child( "TechRequests" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapData : snapshot.getChildren()) {
                    if (snapData.hasChild( "email" )) {
                        if (snapData.child( "email" ).getValue().toString().trim().equals( email )) {
                            String reques = snapData.child( "request" ).getValue().toString().trim();
                            if (reques.equals( "approved" ) || reques.equals( "normal" )) {
                                loginUser( email, pass );
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI( user );

                        } else {
                            // If sign in fails, display a message to the user.
                            tvError.setVisibility( View.VISIBLE );
                            tvError.setText( "Login Failed" );
                            // updateUI(null);
                        }

                        // ...
                    }
                } );
    }

    private void updateUI(FirebaseUser user) {
        Map map = new HashMap();
        map.put( "request", "normal" );
        FirebaseDatabase.getInstance().getReference().child( "TechRequests" ).child( user.getUid().toString() ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity( new Intent( LoginTech.this, TechDashboard.class ) );
                    finish();
                }
            }
        } );
    }
}