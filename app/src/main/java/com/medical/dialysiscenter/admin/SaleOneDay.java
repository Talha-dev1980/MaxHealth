package com.medical.dialysiscenter.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.ChildPatient;
import com.medical.dialysiscenter.utils.ChildPatientHolder;
import com.medical.dialysiscenter.utils.MeasureToday;
import com.medical.dialysiscenter.utils.MessageDialog;
import com.medical.dialysiscenter.utils.ParentTechHolder;
import com.medical.dialysiscenter.utils.ParentTechnicians;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SaleOneDay extends AppCompatActivity {

    private RecyclerView expandList;
    private String day = "1613415600000";
    private String parentName = null;
    private TextView tvDate, tvTime, tvDay, tvearningCount, tvPatientCount;
    private int patientCount = 0, earningCount = 0;
    private String[] dayArray = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sale_one_day );
        try {
            expandList = (RecyclerView) findViewById( R.id.recycler_Expand );
            expandList.setLayoutManager( new LinearLayoutManager( this ) );

            dialog = new ProgressDialog( SaleOneDay.this );
            dialog.setTitle( "Loading..." );
            dialog.setCancelable( false );
            dialog.show();
            Intent it = getIntent();
            day = it.getStringExtra( "day" );
            tvDate = (TextView) findViewById( R.id.tvDateSOD );
            tvDay = (TextView) findViewById( R.id.tvDaySOD );
            tvTime = (TextView) findViewById( R.id.tvTimeSOD );
            tvearningCount = (TextView) findViewById( R.id.tvearningCountSOd );
            tvPatientCount = (TextView) findViewById( R.id.tvPatientCountSOD );
            try {
                setDateTime( day );
            } catch (Exception e) {
                Log.e( "SettingTime", e.getMessage() );
            }

            DatabaseReference parentReference = FirebaseDatabase.getInstance().getReference().child( "Sales" );
            parentReference.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild( day )) {
                        fetchStats( parentReference.child( day ) );
                    } else {
                        dialog.dismiss();
                        MessageDialog m = new MessageDialog( "No Sales ", "Message", SaleOneDay.this );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            } );
        } catch (Exception e) {
            Log.e( "notifFrag", e.getMessage() );
        }
    }

    private void fetchStats(DatabaseReference child) {
        child.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() >= 1) {
                    final List<ParentTechnicians> Parent = new ArrayList<>();
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final String ParentKey = snapshot.getKey().toString();
                        snapshot.child( "titre" ).getValue();

                        DatabaseReference childReference = FirebaseDatabase.getInstance().getReference().child( "Sales" ).child( day ).child( ParentKey );
                        childReference.addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                patientCount = patientCount + (int) (dataSnapshot.getChildrenCount());

                                final List<ChildPatient> Child = new ArrayList<>();
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    earningCount = earningCount + Integer.parseInt( snapshot1.child( "amount" ).getValue().toString().trim() );
                                    Child.add( new ChildPatient( snapshot1 ) );

                                }

                                Parent.add( new ParentTechnicians( ParentKey, dataSnapshot.getChildrenCount() + "", Child ) );
                                DocExpandableRecyclerAdapter adapterx = new DocExpandableRecyclerAdapter( Parent );
                                expandList.setAdapter( adapterx );
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                System.out.println( "Failed to read value." + error.toException() );
                            }
                        } );
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        } );

    }


    private void setDateTime(String timeInMillis) {
        //  timeInMillis="1614884400000";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( Long.parseLong( timeInMillis ) );
        MeasureToday measureToday = new MeasureToday();
        // measureToday.getTimeFromMillis( Long.parseLong( timeInMillis ) );
        tvTime.setText( calendar.get( Calendar.HOUR_OF_DAY ) + ":" +
                calendar.get( Calendar.MINUTE ) + "" );
        //tvDay.setText(( calendar.get(Calendar.DAY_OF_WEEK)-1)+ "" );
        tvDay.setText( measureToday.getDayByIndex( calendar.get( (Calendar.DAY_OF_WEEK) ) - 1 ) + "" );
        tvDate.setText( calendar.get( Calendar.DAY_OF_MONTH ) + " " + monthArray[(calendar.get( Calendar.MONTH ))] + ", " + calendar.get( Calendar.YEAR ) );
    }

    public class DocExpandableRecyclerAdapter extends ExpandableRecyclerViewAdapter<ParentTechHolder, ChildPatientHolder> {


        public DocExpandableRecyclerAdapter(List<ParentTechnicians> groups) {
            super( groups );
        }

        @Override
        public ParentTechHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_parent, parent, false );
            return new ParentTechHolder( view );
        }

        @Override
        public ChildPatientHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_child, parent, false );
            return new ChildPatientHolder( view );
        }

        @Override
        public void onBindChildViewHolder(ChildPatientHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

            final ChildPatient childItem = ((ParentTechnicians) group).getItems().get( childIndex );
            holder.onBind( childItem.getId(), childItem.getName(), childItem.getBill(), childItem.getTime() );
            final String TitleChild = group.getTitle();

            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast toast = Toast.makeText( getApplicationContext(), childItem.getName(), Toast.LENGTH_SHORT );
                    toast.show();
                }

            } );

        }

        @Override
        public void onBindGroupViewHolder(ParentTechHolder holder, int flatPosition, final ExpandableGroup group) {
            ParentTechnicians tech = (ParentTechnicians) group;

            holder.setParent( tech.getTitle(), tech.getCount() );
            tvearningCount.setText( earningCount + "/-" );
            tvPatientCount.setText( patientCount + "" );
            dialog.dismiss();
            if (group.getItems() == null) {
                holder.itemView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast toast = Toast.makeText( getApplicationContext(), group.toString(), Toast.LENGTH_SHORT );
                        toast.show();
                    }
                } );

            }
        }


    }


}
