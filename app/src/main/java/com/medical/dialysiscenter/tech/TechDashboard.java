package com.medical.dialysiscenter.tech;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.MeasureToday;
import com.medical.dialysiscenter.utils.PatientModel;

public class TechDashboard extends AppCompatActivity {

    private RecyclerView listPatients;
    private EditText edtSearchPat;
    private ImageButton btnSearch;
    private ProgressDialog dialog;
    private Button btnAddPatient;
    private TextView tvTotalPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tech_dashboard );
       try{ listPatients = (RecyclerView) findViewById( R.id.listPatientsTechDashboard );
        listPatients.setLayoutManager( new LinearLayoutManager( this ) );

        tvTotalPatients = (TextView) findViewById( R.id.tvTotalPatients );
        edtSearchPat = (EditText) findViewById( R.id.edtSearchPateTech );
        btnAddPatient = (Button) findViewById( R.id.btnAddPatient );
        btnSearch = (ImageButton) findViewById( R.id.btnSearch );

        dialog = new ProgressDialog( this );
        dialog.setTitle( "Loading" );
        dialog.show();
        checkRole();
        fetchTotalPatients();
        setList( "" );

        edtSearchPat.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setList( s.toString().toLowerCase() );
            }
        } );
        btnAddPatient.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( TechDashboard.this, AddPatient.class ) );
            }
        } );
       } catch (Exception e) {
           Log.e( "TechDashboard", e.getMessage() );
       }
    }

    private void fetchTotalPatients() {
        FirebaseDatabase.getInstance().getReference().child( "Patients" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    tvTotalPatients.setText( "Total Patients\t" + snapshot.getChildrenCount() + "" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_tech, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPatient:
                startActivity( new Intent( this, AddPatient.class ) );
                return (true);
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent( this, LoginTech.class ) );
                finish();
                return (true);
        }
        return (super.onOptionsItemSelected( item ));
    }


    private void checkRole() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userIdd = FirebaseAuth.getInstance().getCurrentUser().getUid() + "";
            dialog.dismiss();
     /*       FirebaseDatabase.getInstance().getReference().child( "TechRequests" ).child( userIdd ).addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild( "request" )) {
                        String status = snapshot.child( "request" ).getValue().toString().trim();
                        if (status.equals( "recieved" )) {
                            finnishScreen( "notApproved" );
                        } else if (status.equals( "approved" )) {
                            finnishScreen( "requestApprovedLoginNeed" );
                        } else if (status.equals( "normal" )) {
                            // it.putExtra( "logged", "normal" );
                        }
                    } else {
                        finnishScreen( "requestNotsent" );
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            } );
     */
        } else {
            startActivity( new Intent( TechDashboard.this, LoginTech.class ) );
            finish();
            dialog.dismiss();
        }
    }


    private void finnishScreen(String msg) {
        Intent it = new Intent( TechDashboard.this, SignUpTech.class );

        it.putExtra( "logged", msg );
        startActivity( it );
        finish();
        dialog.dismiss();
    }

    private void setList(String s) {
        Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child( "Patients" ).orderByChild( "name" ).startAt( s ).endAt( s + "\uf8ff" );
        FirebaseRecyclerAdapter<PatientModel, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<PatientModel, UsersViewHolder>(
                PatientModel.class, R.layout.item_visit, UsersViewHolder.class, firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, PatientModel patient, int i) {
                MeasureToday measureToday = new MeasureToday();
                holder.tvName.setText( patient.getName() + "" );
                holder.tvId.setText( patient.getId() + "" );
                holder.tvdate.setText( measureToday.getTimeFromMillis( Long.parseLong( patient.getTime() + "" ) ) );
                FirebaseDatabase.getInstance().getReference().child( "Techs" ).child( patient.getTech().toString().trim() ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild( "TechName" ))
                            holder.tvTech.setText( snapshot.child( "TechName" ).getValue().toString().trim() );
                        //holder.tvTech.setText( patient.getTech().toString().trim()+"check"+snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
                holder.mView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it = new Intent( TechDashboard.this, PatientTransaction.class );
                        it.putExtra( "name", patient.getName() );
                        it.putExtra( "amount", patient.getAmount() );
                        it.putExtra( "pId", patient.getId() );
                        startActivity( it );
                    }
                } );


            }
        };
        listPatients.setAdapter( adapter );
        dialog.dismiss();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView tvName, tvId, tvTech, tvdate;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tvName = (TextView) mView.findViewById( R.id.tvPatientNameVisit );
            tvId = (TextView) mView.findViewById( R.id.tvPidVisit );
            tvdate = (TextView) mView.findViewById( R.id.tvTimDateVisit );
            tvTech = (TextView) mView.findViewById( R.id.tvTechTreatedBy );
            // userImage=(CircleImageView) mView.findViewById(R.id.single_userImage_users);


        }
    }


}