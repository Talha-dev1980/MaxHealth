package com.medical.dialysiscenter.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.medical.dialysiscenter.MainActivity;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.MessageDialog;

import java.util.HashMap;
import java.util.Map;

public class AdminLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPass;
    private Button btnAdminLogin, btnSignIn;
    private TextView tvForget,tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_login );
        mAuth = FirebaseAuth.getInstance();
        edtEmail = (EditText) findViewById( R.id.edtEmailAdminLogin );
        edtPass = (EditText) findViewById( R.id.edtPassAdminLogin );
        tvForget = (TextView) findViewById( R.id.tvForgetAdminLogin );
        tvError = (TextView) findViewById( R.id.tvAdminLoginError );
        btnAdminLogin = (Button) findViewById( R.id.btnLogInAdmin );
        btnSignIn = (Button) findViewById( R.id.btnSignInAdminLogin );
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity( new Intent(AdminLogin.this,AdminDashboard.class) );
        }else
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
                                        MessageDialog messageDialog = new MessageDialog( "Password reset link sent check mail box", "Email Sent", AdminLogin.this );
                                    } else {
                                        tvError.setVisibility(  View.VISIBLE );
                                        tvError.setText( "Please enter valid data" );
                                    }
                                }
                            } );
                }
            }
        } );
        findViewById( R.id.btnSignInAdminLogin ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent( AdminLogin.this, MainActivity.class );
                it.putExtra( "logged", "new" );
                startActivity( it );
            }
        } );
        btnAdminLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                if (!email.isEmpty() && pass.length() >= 6) {
                    loginUser( email, pass );
                  /*  FirebaseDatabase.getInstance().getReference().child( "Admin" ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0) {
                                for (DataSnapshot dataSnap:snapshot.getChildren()){
                                    if (dataSnap.child( "email" ).getValue().toString().trim().equals( email )){
                                        loginUser( email, pass );
                                    }
                                }
                            }else{
                                MessageDialog messageDialog=new MessageDialog("Account not exists","Fail",AdminLogin.this);
                              //  finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    } );
*/
                } else {
                    tvError.setVisibility(  View.VISIBLE );
                    tvError.setText( "Please enter valid data" ); edtEmail.setText( "" );
                    edtPass.setText( "" );
                }
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
                            // updateUI( email, user );
                            startActivity( new Intent( AdminLogin.this, AdminDashboard.class ) );
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            tvError.setVisibility(  View.VISIBLE );
                            tvError.setText( "Login Failed" );
                            // updateUI(null);
                        }

                        // ...
                    }
                } );
    }

    private void updateUI(String email, FirebaseUser user) {
        Map map = new HashMap();
        map.put( "email", email );
        FirebaseDatabase.getInstance().getReference().child( "Admin" ).child( user.getUid().toString() ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity( new Intent( AdminLogin.this, AdminDashboard.class ) );
                    finish();
                }
            }
        } );
    }
}