package com.medical.dialysiscenter.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.MainActivity;
import com.medical.dialysiscenter.R;

public class AdminDashboard extends AppCompatActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_dashboard );
        try {
            BottomNavigationView navView = findViewById( R.id.nav_view );
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            dialog = new ProgressDialog( this );
            dialog.setTitle( "Loading.." );
            // dialog.show();
            //  checlRole();
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_earning, R.id.navigation_equipment, R.id.navigation_notifications )
                    .build();
            NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
            NavigationUI.setupActionBarWithNavController( this, navController, appBarConfiguration );
            NavigationUI.setupWithNavController( navView, navController );
        } catch (Exception e) {
            Log.e( "AdminDB", e.getMessage() );
        }
    }

    public void gotoOneDaySale(View view) {
        startActivity( new Intent( AdminDashboard.this, SaleOneDay.class ) );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_options, menu );
        return true;
    }

    private void checlRole() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userIdd = FirebaseAuth.getInstance().getCurrentUser().getUid() + "";
            FirebaseDatabase.getInstance().getReference().child( "Admin" ).addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild( userIdd )) {

                        dialog.dismiss();
                    } else {
                        startActivity( new Intent( AdminDashboard.this, AdminLogin.class ) );
                        finish();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            } );
        } else {
            startActivity( new Intent( AdminDashboard.this, AdminLogin.class ) );
            finish();
            dialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_tech:
                startActivity( new Intent( this, AddTech.class ) );
                return (true);
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent( this, MainActivity.class ) );
                finish();
                return (true);
        }
        return (super.onOptionsItemSelected( item ));
    }
}