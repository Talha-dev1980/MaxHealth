package com.medical.dialysiscenter.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.ContactsAdapter;
import com.medical.dialysiscenter.utils.EarningCount;
import com.medical.dialysiscenter.utils.MeasureToday;
import com.medical.dialysiscenter.utils.MessageDialog;
import com.medical.dialysiscenter.utils.TestModel;

import java.util.ArrayList;
import java.util.Calendar;

public class SaleFixDuration extends AppCompatActivity {

    private TextView tvStartingDate, tvEndingDate, tvPatients, tvEarning;
    private ImageButton btnStarting, btnEnding;
    private RecyclerView expandList;
    private int mYear, mMonth, mDay;
    static String startingDate, endingDate;
    int x = 0;
    int earning, patients;
    private ProgressDialog dialog;
    MeasureToday measureToday = new MeasureToday();
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sale_fix_duration );
       try{ tvStartingDate = (TextView) findViewById( R.id.tvStartingTime );
        tvEndingDate = (TextView) findViewById( R.id.tvEndingTime );
        tvPatients = (TextView) findViewById( R.id.tvPatientscountSFD );
        tvEarning = (TextView) findViewById( R.id.tvEarningsSFD );
        btnStarting = (ImageButton) findViewById( R.id.btnCalPickStarting );
        btnEnding = (ImageButton) findViewById( R.id.btnCalPickEndingTime );
        expandList = (RecyclerView) findViewById( R.id.listFileteredVists );
        expandList.setLayoutManager( new LinearLayoutManager( this ) );
        MeasureToday measureToday = new MeasureToday();
        startingDate = measureToday.getBeforeDay( Long.parseLong( measureToday.getTodayinMillisec( measureToday.getCurrentDay() - 30 ) ) );
        endingDate = measureToday.getBeforeDay( Long.parseLong( measureToday.getTodayinMillisec( measureToday.getCurrentDay() ) ) );
        tvStartingDate.setText( measureToday.getDateFromMillis( Long.parseLong( measureToday.getTodayinMillisec( measureToday.getCurrentDay() - 30 ) ) ) );
        tvEndingDate.setText( measureToday.getCurrentDate() );
        getEarnings( startingDate, endingDate );

        //showEarnings();
        btnStarting.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartingTime();
            }
        } );
        btnEnding.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEndingTime();
            }
        } );
       } catch (Exception e) {
           Log.e( "SaleFixDur", e.getMessage() );
       }

    }

    private void showEarnings() {
        FirebaseRecyclerAdapter<EarningCount, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<EarningCount, UsersViewHolder>(
                EarningCount.class, R.layout.item_visit,
                UsersViewHolder.class, FirebaseDatabase.getInstance().getReference().child( "Earnings" )

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, EarningCount earningCount, int i) {
                Long day = new Long( Long.parseLong( getRef( i ).getKey().toString() ) );
                Long startingLong = new Long( startingDate );
                Long endingLong = new Long( endingDate );
                if (day % startingLong != day || day % startingLong == 0) {
                    if (day % endingLong == day || day % endingLong == 0) {
                        holder.tvdate.setText( measureToday.getMillisToDate( day ) );
                        holder.tvId.setText( "Earnings " );
                        holder.tvName.setText( "Pateints  " + earningCount.getPatientCount() );
                        holder.tvTech.setText( earningCount.getEarning() + "/-" );
                        data = data + "  " + earningCount.getDay() + " earning " + earningCount.getEarning() + " patients " + earningCount.getPatientCount();
                    }
                }
            }
        };
        expandList.setAdapter( adapter );
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


    private void showEndingTime() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get( Calendar.YEAR );
        mMonth = c.get( Calendar.MONTH );
        mDay = c.get( Calendar.DAY_OF_MONTH );


        DatePickerDialog datePickerDialog = new DatePickerDialog( this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        MeasureToday measureToday1 = new MeasureToday();
                        MeasureToday measureToday2 = new MeasureToday();
                        MeasureToday measureToday3 = new MeasureToday();
                        tvEndingDate.setText( measureToday1.setStringDateInFormat( dayOfMonth, monthOfYear, year ) + "" );
                        endingDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        getEarnings( startingDate,
                                endingDate );
                    }
                }, mYear, mMonth, mDay );
        datePickerDialog.show();
    }


    private void showStartingTime() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get( Calendar.YEAR );
        mMonth = c.get( Calendar.MONTH );
        mDay = c.get( Calendar.DAY_OF_MONTH );


        DatePickerDialog datePickerDialog = new DatePickerDialog( this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        MeasureToday measureToday1 = new MeasureToday();
                        MeasureToday measureToday2 = new MeasureToday();
                        TestModel model3 = new TestModel();
                        tvStartingDate.setText( measureToday1.setStringDateInFormat( dayOfMonth, monthOfYear, year ) + "" );
                        startingDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        getEarnings( startingDate, endingDate );
                    }
                }, mYear, mMonth, mDay );
        datePickerDialog.show();
    }

    private void getEarnings(String startingDate, String endingDate) {
        dialog = new ProgressDialog( this );
        dialog.setTitle( "Fetching..." );
        dialog.show();
        earning = 0;
        patients = 0;
        MeasureToday measureToday = new MeasureToday();
        String x = measureToday.getMillis( startingDate );
        ArrayList<EarningCount> earningArray = new ArrayList<EarningCount>();

        String y = measureToday.getMillis( endingDate );
        Long startingLong = new Long( x );
        Long endingLong = new Long( y );
        ContactsAdapter adapter = new ContactsAdapter( earningArray );

        FirebaseDatabase.getInstance().getReference().child( "Earnings" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    data = "";
                    for (DataSnapshot snapDate : snapshot.getChildren()) {
                        EarningCount earningCount = new EarningCount();
                        Long dat = new Long( Long.parseLong( snapDate.getKey().toString() ) );


                        Long day = new Long( Long.parseLong( snapDate.getKey().toString() ) );
                        if (day % startingLong != day || day % startingLong == 0) {
                            if (day % endingLong == day || day % endingLong == 0) {
                                earningCount.setDay( dat );
                                int dayPatients = Integer.parseInt( snapDate.child( "patients" ).getValue().toString() );
                                int dayEarning = Integer.parseInt( snapDate.child( "earning" ).getValue().toString() );
                                earning = earning + dayEarning;
                                patients = patients + dayPatients;

                                earningCount.addStats( dayPatients,
                                        dayEarning );
                                earningArray.add( earningCount );
                                data = data + "  " + earningCount.getDay() + " earning " + earningCount.getEarning() + " patients " + earningCount.getPatientCount();
                            }
                        }
                    }


                    data = data + " " + startingDate + " " + endingDate;

                    if (earningArray.size() > 0) {
                        tvEarning.setText( earning + "/-" );
                        tvPatients.setText( patients + "" );
                        expandList.setAdapter( adapter );
                        dialog.dismiss();
                        //      MessageDialog messageDialog = new MessageDialog( data + "  ", earningArray.size() + "", SaleFixDuration.this );
                    } else {
                        adapter.notifyDataSetChanged();
                        MessageDialog messageDialog = new MessageDialog( "No history for this Query", "Sorry", SaleFixDuration.this );
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }


}