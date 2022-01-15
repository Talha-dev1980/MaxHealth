package com.medical.dialysiscenter.admin.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.dialysiscenter.R;
import com.medical.dialysiscenter.utils.MeasureToday;
import com.medical.dialysiscenter.utils.MessageDialog;
import com.medical.dialysiscenter.utils.NotificationManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EquipmentsFrag extends Fragment implements AdapterView.OnItemSelectedListener {

    private EquipmentDataModelView dashboardViewModel;
    private EditText edtQty, edtDate;
    private Button btnAdd;
    private ImageButton btnCal;
    private TextView tvStock, tvStockError, tvInjectionStock;
    private int mYear, mMonth, mDay;
    private MeasureToday measureToday;
    private ProgressDialog dialog;
    private DatabaseReference mReference;
    private int avQty = 0, injectCount = 0;
    private String userId;
    private Spinner spEquipment;
    private String[] equipments = {"Dialyser", "Injection"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider( this ).get( EquipmentDataModelView.class );
        View root = inflater.inflate( R.layout.fragment_equipment, container, false );
        try {
            edtDate = (EditText) root.findViewById( R.id.edtDateEquip );
            edtQty = (EditText) root.findViewById( R.id.edtQuantityEquip );
            btnAdd = (Button) root.findViewById( R.id.btnAddStock );
            spEquipment = (Spinner) root.findViewById( R.id.spEquipment );
            btnCal = (ImageButton) root.findViewById( R.id.btnCalDateEquip );
            tvStockError = (TextView) root.findViewById( R.id.tvStockError );
            tvStock = (TextView) root.findViewById( R.id.tvStockEquip );
            tvInjectionStock = (TextView) root.findViewById( R.id.tvinjectionStock );
            mReference = FirebaseDatabase.getInstance().getReference().child( "Dialysers" ).child( "Admin" );
            dialog = new ProgressDialog( getActivity() );
            dialog.setTitle( "Loading" );
            dialog.setCancelable( false );
            dialog.show();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_spinner_item, equipments );
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spEquipment.setAdapter( adapter );
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            btnCal.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDateChooser();
                }
            } );

            btnAdd.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.setTitle( "Adding" );
                    dialog.show();
                    String date = edtDate.getText().toString().trim();
                    date = measureToday.getStringDateInMillis( date );
                    String equipmentType = equipments[spEquipment.getSelectedItemPosition()];
                    int qty = Integer.parseInt( edtQty.getText().toString().trim() );
                    if (date.length() != 0 && qty >= 1) {
                        edtDate.setText( measureToday.getCurrentDate() );
                        edtQty.setText( "" );
                        enterStock( date, qty, equipmentType );
                    } else {
                        dialog.dismiss();
                        edtQty.setError( "Add valid quantity" );
                    }
                }
            } );
            measureToday = new MeasureToday();

            edtDate.setText( measureToday.getCurrentDate() );

            fetchStockCount();
        } catch (Exception e) {
            Log.e( "EquipmentFrag", e.getMessage() );
        }
        return root;
    }

    private void fetchStockCount() {

        mReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild( "dCount" )) {
                    measureToday.setCount( Integer.parseInt( snapshot.child( "dCount" ).getValue().toString() ) );
                    tvStock.setText( snapshot.child( "dCount" ).getValue().toString() + "" );

                    int qty = Integer.parseInt( snapshot.child( "dCount" ).getValue().toString() + "" );
                    dialog.dismiss();
                    if (Integer.parseInt( snapshot.child( "dCount" ).getValue().toString() + "" ) <= 20) {
                        tvStockError.setVisibility( View.VISIBLE );

                        tvStockError.append( "Add more dialysers, few of them left" );
                        NotificationManager notif = new NotificationManager();
                        notif.sendNotif( userId, "Low Stock " + qty + " dialysers left add more now", measureToday.getStringDateInMillis( measureToday.getCurrentDate() ) );

                    } else {
                        tvStockError.setVisibility( View.GONE );
                    }

                } else {
                    dialog.dismiss();
                    MessageDialog m = new MessageDialog( "NO dialysers available", "Empty", getActivity() );
                }
                if (snapshot.hasChild( "injCount" )) {
                    measureToday.setInjections( Integer.parseInt( snapshot.child( "injCount" ).getValue().toString() ) );
                    tvInjectionStock.setText( snapshot.child( "injCount" ).getValue().toString() + "" );
                    if (Integer.parseInt( snapshot.child( "injCount" ).getValue().toString() + "" ) <= 20) {
                        tvStockError.setVisibility( View.VISIBLE );
                        tvStockError.append( "Add more injections, few of them left" );
                        NotificationManager notif = new NotificationManager();
                        notif.sendNotif( userId, "Low Stock " + measureToday.getInjections() + " injection left add more now", measureToday.getStringDateInMillis( measureToday.getCurrentDate() ) );

                    } else {
                        tvStockError.setVisibility( View.GONE );
                    }
                } else {
                    dialog.dismiss();
                    MessageDialog m = new MessageDialog( "NO injections available", "Empty", getActivity() );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void enterStock(String date, int qty, String equipmentType) {
        Map stockMap = new HashMap();
        stockMap.put( "date", date );
        injectCount = measureToday.getInjections();
        avQty = Integer.parseInt( tvStock.getText().toString().trim() );
        int stock = 0;
        if (equipmentType.equals( "Dialyser" )) {
            stockMap.put( "dCount", (avQty + qty) + "" );
            stock = avQty + qty;
        } else {
            stockMap.put( "injCount", (injectCount + qty) + "" );
            stock = injectCount + qty;
        }
        int finalStock = stock;
        FirebaseDatabase.getInstance().getReference().child( "Dialysers" ).child( "Admin" ).updateChildren( stockMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    NotificationManager notif = new NotificationManager();
                    String message = "Stock added " + qty + " " + equipmentType + "  added. Currently " + finalStock + " are available";
                    notif.sendNotif( userId, message, measureToday.getStringDateInMillis( measureToday.getCurrentDate() ) );
                    Toast.makeText( getActivity(), "Stock Added Successfully...", Toast.LENGTH_SHORT ).show();
                } else {
                    MessageDialog messageDialog = new MessageDialog( "Stock not added", "Failed", getActivity() );
                }
            }
        } );
    }

    private void openDateChooser() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get( Calendar.YEAR );
        mMonth = c.get( Calendar.MONTH );
        mDay = c.get( Calendar.DAY_OF_MONTH );


        DatePickerDialog datePickerDialog = new DatePickerDialog( getActivity(),
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}