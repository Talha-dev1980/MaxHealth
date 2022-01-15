package com.medical.dialysiscenter.tech;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.MeasureToday;
import com.medical.dialysiscenter.utils.MessageDialog;
import com.medical.dialysiscenter.utils.NotificationManager;
import com.medical.dialysiscenter.utils.TestModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPatient extends AppCompatActivity {
    private TextView tvPatientId, tvTotalBill;
    private EditText edtName, edtAmount, edtICUcharges, edtInjectionPrice;
    private TextView edtDate;
    private Button btnAdd, btnPrint;
    private boolean panel = false;
    private ImageButton btncalender;
    private RecyclerView listPatientHist;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
    private ProgressDialog dialog;
    static int countDialysr = 0;
    private CheckBox cbxPanel, cbxinjection;
    TestModel model = new TestModel();
    private Boolean panelPayment = false;
    static boolean usedDialyser = false;
    private MeasureToday measureToday = new MeasureToday();
    private int fee = 0, icuCharges = 0, injectionPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_patient );

        tvPatientId = (TextView) findViewById( R.id.titlePatientIdAP );
        edtName = (EditText) findViewById( R.id.edtNameAP );
        edtICUcharges = (EditText) findViewById( R.id.edtICUChargesAP );
        edtInjectionPrice = (EditText) findViewById( R.id.edtInjectionAmountAP );
        edtDate = (TextView) findViewById( R.id.edtDateAP );
        tvTotalBill = (TextView) findViewById( R.id.tvTotalBillToPayAP );
        cbxinjection = (CheckBox) findViewById( R.id.cbxInjectionAP );
        cbxPanel = (CheckBox) findViewById( R.id.cbxPanelAP );
        edtAmount = (EditText) findViewById( R.id.edtAmountAP );
        btnAdd = (Button) findViewById( R.id.btnAddTransactionAP );
        btnPrint = (Button) findViewById( R.id.btnPrintAP );

        dialog = new ProgressDialog( this );
        dialog.setTitle( "loading" );
        dialog.setCancelable( false );
        dialog.show();

        Intent it = getIntent();
        edtName.setText( it.getStringExtra( "name" ) );
        edtAmount.setText( it.getStringExtra( "amount" ) );
        tvPatientId.setText( it.getStringExtra( "pId" ) );
        getDialyserCount();
        edtDate.setText( measureToday.getCurrentDate() + " " );
        dialog.dismiss();
        fee = Integer.parseInt( 0 + edtAmount.getText().toString() );
        icuCharges = Integer.parseInt( 0 + edtICUcharges.getText().toString() );
        injectionPrice = Integer.parseInt( 0 + edtInjectionPrice.getText().toString() );
        calculateBill( fee, icuCharges, injectionPrice );

        btnPrint.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent( AddPatient.this, PrintBill.class );
                NotificationManager notif = new NotificationManager();
                String techName = notif.getTechName( userId );
                MeasureToday measureToday = new MeasureToday();
                int myBill = Integer.parseInt( tvTotalBill.getText().toString().trim() );

                if (cbxPanel.isChecked()) {
                    myBill = 0;
                }
                    it.putExtra( "pId", tvPatientId.getText().toString().trim() );
                    it.putExtra( "pName", edtName.getText().toString().trim() );
                    it.putExtra( "tName", techName.trim() );
                    it.putExtra( "injCharges", injectionPrice + "" );
                    it.putExtra( "icuCharges", icuCharges + "" );
                    it.putExtra( "fee", fee + "" );
                    it.putExtra( "totalCharges", myBill + "" );
                    it.putExtra( "currentDate", measureToday.getCurrentDate() );
                    if (checkinternet()) {
                        startActivity( it );
                    } else {
                        MessageDialog dialog = new MessageDialog( "Internet not connected", "Message", AddPatient.this );
                    }

            }
        } );
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
                } else {
                    panel = true;
                    edtAmount.setEnabled( true );
                    edtICUcharges.setEnabled( true );

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

        btnAdd.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkinternet()) {
                    dialog = new ProgressDialog( AddPatient.this );
                    String pId = tvPatientId.getText().toString().trim();
                    String name = edtName.getText().toString().trim();
                    String date = edtDate.getText().toString().trim();
                    String amount = tvTotalBill.getText().toString().trim();
                    //&& edtAmount.getText().toString().length() > 1 && edtName.getText().toString().length() > 1

                    AddTransaction transaction = new AddTransaction();
                    if (validateData( pId, name, amount, date )) {

                        panelPayment = cbxPanel.isChecked();
                        transaction.enterTransaction( userId, pId, name, date, cbxinjection.isChecked(), cbxPanel.isChecked(), amount, AddPatient.this );
                        findViewById( R.id.tvErrorAP ).setVisibility( View.GONE );
                    } else {
                        dialog.dismiss();
                        findViewById( R.id.tvErrorAP ).setVisibility( View.VISIBLE );
                    }
                } else {
                    MessageDialog dialog = new MessageDialog( "Internet not connected", "Message", AddPatient.this );

                }
            }
        } );
        FirebaseDatabase.getInstance().getReference().child( "Patients" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvPatientId.setText( "P-" + (snapshot.getChildrenCount() + 1) + "" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        } );

    }

    private boolean checkinternet() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService( Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void calculateBill(int fee, int icuCharges, int injectionPrice) {
        int bill = fee + icuCharges + injectionPrice;
        tvTotalBill.setText( bill + "" );
    }

    private int getDialyserCount() {
        FirebaseDatabase.getInstance().getReference().child( "Dialysers" ).child( "Admin" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild( "dCount" )) {
                    countDialysr = Integer.parseInt( snapshot.child( "dCount" ).getValue().toString() );
                    measureToday.setCount( countDialysr );

                    measureToday.setInjections( Integer.parseInt( snapshot.child( "injCount" ).getValue().toString() ) );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        } );
        return countDialysr;
    }

  /*  private void enterTransaction(String pId, String name, String date, String amount) {
        Calendar cal = Calendar.getInstance();
        String currentTime = cal.getTimeInMillis() + "";
        int day, month, year;
        day = cal.get( Calendar.DAY_OF_MONTH );
        String todayMillis = measureToday.getTodayinMillisec( day );

        Map transacData = new HashMap();
        transacData.put( "id", pId );
        transacData.put( "name", name.toLowerCase() );
        transacData.put( "amount", amount );
        transacData.put( "time", currentTime );
        model.getEarningValues( amount, todayMillis );

        FirebaseDatabase.getInstance().getReference().child( "Sales" ).child( todayMillis ).child( userId ).child( pId ).setValue( transacData ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    transacData.put( "tech", userId );
                    FirebaseDatabase.getInstance().getReference().child( "Patients" ).child( pId ).setValue( transacData ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                useDialyser( measureToday.getCount() );
                                MessageDialog message = new MessageDialog( "Transaction Added Successfuly", "Success", AddPatient.this );
                                NotificationManager notif = new NotificationManager();
                                notif.getTechName( userId );
                                String techName = notif.getTechName();
                                String messageText = "New Patient: " + techName + " treated " + pId + " " + name + ", get Rs." + amount + "/-";
                                notif.sendNotif( userId, messageText, measureToday.getStringDateInMillis( measureToday.getCurrentDate() ) );
                                model.addEarning( amount, todayMillis ,AddPatient.this);
                            }
                            dialog.dismiss();
                        }
                    } );
                } else {
                    MessageDialog message = new MessageDialog( "Transaction not added", "Failed", AddPatient.this );
                    dialog.dismiss();
                }
            }
        } );

    }




    private boolean useDialyser(int count) {
        Map map = new HashMap();
        map.put( "dCount", (count - 1) + "" );
        FirebaseDatabase.getInstance().getReference().child( "Dialysers" ).child( "Admin" ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    usedDialyser = true;
                }
            }
        } );
        return usedDialyser;
    }*/

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


}