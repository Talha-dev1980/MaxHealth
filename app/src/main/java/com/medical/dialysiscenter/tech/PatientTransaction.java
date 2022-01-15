package com.medical.dialysiscenter.tech;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.medical.dialysiscenter.utils.MessageDialog;
import com.medical.dialysiscenter.utils.NotificationManager;
import com.medical.dialysiscenter.utils.PatientModel;
import com.medical.dialysiscenter.utils.TestModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PatientTransaction extends AppCompatActivity {

    private TextView tvPatientId;
    private boolean panel = false;
    private EditText edtName, edtAmount, edtICUcharges, edtInjectionPrice;
    private TextView edtDate, tvTotalBill;
    private Button btnAdd, btnPrint;
    private ImageButton btncalender;
    private RecyclerView listPatientHist;
    private int mYear, mMonth, mDay;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
    private ProgressDialog dialog;
    private CheckBox cbxPanel, cbxinjection;
    static int countDialysr = 0;
    MeasureToday measureToday = new MeasureToday();
    private Boolean panelPayment = false;
    TestModel model = new TestModel();
    private int fee = 0, icuCharges = 0, injectionPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_patient_transaction );
        try {
            tvPatientId = (TextView) findViewById( R.id.titlePatientId );
            edtName = (EditText) findViewById( R.id.edtNamePT );
            edtDate = (TextView) findViewById( R.id.edtDatePT );
            edtAmount = (EditText) findViewById( R.id.edtAmountPT );
            edtICUcharges = (EditText) findViewById( R.id.edtICUChargesPT );
            edtInjectionPrice = (EditText) findViewById( R.id.edtInjectionAmountPT );
            btnAdd = (Button) findViewById( R.id.btnAddTransactionPT );
            cbxPanel = (CheckBox) findViewById( R.id.cbxPanelPT );
            cbxinjection = (CheckBox) findViewById( R.id.cbxInjection );
            tvTotalBill = (TextView) findViewById( R.id.tvTotalBillToPayPT );
            btnPrint = (Button) findViewById( R.id.btnPrintPT );
            //btncalender = (ImageButton) findViewById( R.id.btnCaldatePT );
            listPatientHist = (RecyclerView) findViewById( R.id.listPatientHistory );
            listPatientHist.setLayoutManager( new LinearLayoutManager( this ) );
            Intent it = getIntent();
            edtName.setText( it.getStringExtra( "name" ) );
            edtName.setEnabled( false
            );
            edtAmount.setText( it.getStringExtra( "amount" ) );
            tvPatientId.setText( it.getStringExtra( "pId" ) );
            getDialyserCount();

            fee = Integer.parseInt( 0 + edtAmount.getText().toString() );
            icuCharges = Integer.parseInt( 0 + edtICUcharges.getText().toString() );
            injectionPrice = Integer.parseInt( 0 + edtInjectionPrice.getText().toString() );
            calculateBill( fee, icuCharges, injectionPrice );
            edtDate.setText( measureToday.getCurrentDate() );
        /* btncalender.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateChooser();
            }
        } );*/
            edtInjectionPrice.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (edtInjectionPrice.getText().toString().length() > 0) {
                        cbxinjection.setChecked( true );
                        injectionPrice = Integer.parseInt( 0 + edtInjectionPrice.getText().toString() );
                        calculateBill( fee, icuCharges, injectionPrice );
                    } else {
                        cbxinjection.setChecked( false );
                    }
                }
            } );
            edtICUcharges.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    icuCharges = Integer.parseInt( 0 + edtICUcharges.getText().toString() );
                    calculateBill( fee, icuCharges, injectionPrice );
                }
            } );
            edtAmount.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    fee = Integer.parseInt( 0 + edtAmount.getText().toString() );
                    calculateBill( fee, icuCharges, injectionPrice );
                }
            } );
            cbxPanel.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        panel = true;
                        edtAmount.setEnabled( false );
                        edtICUcharges.setEnabled( false );
                        edtInjectionPrice.setEnabled( false );
                    } else {
                        panel = true;
                        edtAmount.setEnabled( true );
                        edtICUcharges.setEnabled( true );
                        edtInjectionPrice.setEnabled( true );
                    }
                }
            } );
            cbxinjection.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        injectionPrice = Integer.parseInt( 0 + edtInjectionPrice.getText().toString() );
                        calculateBill( fee, icuCharges, injectionPrice );
                    } else {
                        injectionPrice = 0;
                        calculateBill( fee, icuCharges, injectionPrice );
                    }
                }
            } );
            btnPrint.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent( PatientTransaction.this, PrintBill.class );
                    NotificationManager notif = new NotificationManager();
                    String techName = notif.getTechName( userId );
                    MeasureToday measureToday = new MeasureToday();
                    int myBill = Integer.parseInt( tvTotalBill.getText().toString().trim() );

                    if (cbxPanel.isChecked()) {
                        myBill = 0;
                    }
                        it.putExtra( "pId", tvPatientId.getText().toString().trim() );
                        it.putExtra( "pName", edtName.getText().toString().trim() );
                        it.putExtra( "tName", techName + "" );
                        it.putExtra( "injCharges", injectionPrice + "" );
                        it.putExtra( "icuCharges", icuCharges + "" );
                        it.putExtra( "fee", fee + "" );
                        it.putExtra( "totalCharges", myBill+"" );
                        it.putExtra( "currentDate", measureToday.getCurrentDate() );
                        if (checkinternet()) {
                            startActivity( it );
                        } else {
                            MessageDialog dialog = new MessageDialog( "Internet not connected", "Message", PatientTransaction.this );
                        }

                }
            } );
            btnAdd.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkinternet()) {
                        dialog = new ProgressDialog( PatientTransaction.this );
                        String pId = tvPatientId.getText().toString().trim();
                        String name = edtName.getText().toString().trim();
                        String date = edtDate.getText().toString().trim();
                        String amount = tvTotalBill.getText().toString().trim();

                        AddTransaction transaction = new AddTransaction();
                        if (validateData( pId, name, amount, date )) {
                            panelPayment = cbxPanel.isChecked();
                            transaction.enterTransaction( userId, pId, name, date, cbxinjection.isChecked(), cbxPanel.isChecked(), amount, PatientTransaction.this );
                            findViewById( R.id.tvError ).setVisibility( View.GONE );
                        } else {
                            dialog.dismiss();
                            findViewById( R.id.tvError ).setVisibility( View.VISIBLE );
                        }
                    } else {
                        MessageDialog dialog = new MessageDialog( "Internet not connected", "Message", PatientTransaction.this );

                    }
                }
            } );
        } catch (Exception e) {
            Log.e( "notifFrag", e.getMessage() );
        }
    }

    private void calculateBill(int fee, int icuCharges, int injectionPrice) {
        int bill = fee + icuCharges + injectionPrice;
        tvTotalBill.setText( bill + "" );
    }

    private boolean checkinternet() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService( Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    private int getDialyserCount() {
        FirebaseDatabase.getInstance().getReference().child( "Dialysers" ).child( "Admin" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                measureToday.setCount( Integer.parseInt( snapshot.child( "dCount" ).getValue().toString() ) );
                measureToday.setInjections( Integer.parseInt( snapshot.child( "injCount" ).getValue().toString() ) );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        } );
        return countDialysr;
    }


    private boolean validateData(String pId, String name, String amount, String date) {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat( "dd MMM, yyyy" ).parse( date );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        MeasureToday dayMeasure = new MeasureToday();
        int day = dayMeasure.getCurrentDay();
        long todayMillis = Long.parseLong( dayMeasure.getTodayinMillisec( day ) );
        cal.setTime( date1 );
        long pickedTime = cal.getTimeInMillis();
        if (pickedTime >= todayMillis) {


            if (!pId.isEmpty() && name.length() > 1 && amount.length() > 1) {
                return true;
            } else return false;
        } else {
            edtDate.setError( "Can't select previous date" );
            return false;
        }
    }

    private void openDateChooser() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get( Calendar.YEAR );
        mMonth = c.get( Calendar.MONTH );
        mDay = c.get( Calendar.DAY_OF_MONTH );


        DatePickerDialog datePickerDialog = new DatePickerDialog( this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        MeasureToday measureToday = new MeasureToday();

                        edtDate.setText( measureToday.setStringDateInFormat( dayOfMonth, monthOfYear, year ) );

                    }
                }, mYear, mMonth, mDay );
        datePickerDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent it = new Intent( PatientTransaction.this, SignUpTech.class );
            String userIdd = FirebaseAuth.getInstance().getCurrentUser().getUid() + "";
      /*      FirebaseDatabase.getInstance().getReference().child( "TechRequests" ).child( userIdd ).addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild( "request" )) {
                        String status = snapshot.child( "request" ).getValue().toString().trim();
                        if (status.equals( "recieved" )) {
                            it.putExtra( "logged", "notApproved" );
                            startActivity( it );
                        } else if (status.equals( "approved" )) {
                            it.putExtra( "logged", "requestApprovedLoginNeed" );
                            startActivity( it );
                        } else if (status.equals( "normal" )) {
                            it.putExtra( "logged", "normal" );
                        }
                    } else {
                        it.putExtra( "logged", "requestNotsent" );
                        startActivity( it );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            } );
      */
        } else {
            startActivity( new Intent( PatientTransaction.this, LoginTech.class ) );
            finish();
        }
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
                        // holder.tvTech.setText( patient.getTech().toString().trim()+"check"+snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
                holder.mView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edtName.setText( patient.getName() );
                        tvPatientId.setText( patient.getId() + "" );
                        edtAmount.setText( patient.getAmount() );
                    }
                } );


            }
        };
        //  listPatientHist.setAdapter( adapter );
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
