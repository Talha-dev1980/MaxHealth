package com.medical.dialysiscenter.tech;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.medical.dialysiscenter.R;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class PrintBill extends AppCompatActivity {
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString( "00001101-0000-1000-8000-00805F9B34FB" );
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private TextView tvbill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_print_bill );
        mScan = (Button) findViewById( R.id.Scan );
        tvbill = (TextView) findViewById( R.id.tvBillPreview );
        Intent it = getIntent();
        String pID = it.getStringExtra( "pId" );
        String pName = it.getStringExtra( "pName" );
        String tName = it.getStringExtra( "tName" );
        String injCharges = it.getStringExtra( "injCharges" );
        String icuCharges = it.getStringExtra( "icuCharges" );
        String fee = it.getStringExtra( "fee" );
        String totalCharges = it.getStringExtra( "totalCharges" );
        String currentDate = it.getStringExtra( "currentDate" );


        tvbill.setText( createBillPreview( pID, pName, tName, injCharges, icuCharges, fee, totalCharges, currentDate ) + "" );
        mScan.setOnClickListener( new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                checkPrinter();
            }
        } );

        mPrint = (Button) findViewById( R.id.mPrint );
        mPrint.setOnClickListener( new View.OnClickListener() {
            public void onClick(View mView) {
                long start = System.currentTimeMillis();
                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            String BILL = createBillPreview( pID, pName, tName, injCharges, icuCharges, fee, totalCharges, currentDate ) + "";
                            os.write( BILL.getBytes() );
                            int gs = 29;
                            os.write( intToByteArray( gs ) );
                            int h = 104;
                            os.write( intToByteArray( h ) );
                            int n = 162;
                            os.write( intToByteArray( n ) );

                            // Setting Width
                            int gs_width = 29;
                            os.write( intToByteArray( gs_width ) );
                            int w = 119;
                            os.write( intToByteArray( w ) );
                            int n_width = 2;
                            os.write( intToByteArray( n_width ) );


                        } catch (Exception e) {
                            Log.e( "PrintBill", "Exe ", e );
                        }
                    }
                };
                t.start();

            }
        } );

        mDisc = (Button) findViewById( R.id.dis );
        mDisc.setOnClickListener( new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        } );

    }

    private void checkPrinter() {
        if (mBluetoothAdapter == null) {
            Log.e( "Message", "Blutooth adapter is null" );
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE );
                startActivityForResult( enableBtIntent,
                        REQUEST_ENABLE_BT );
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent( PrintBill.this,
                        DeviceListActivity.class );
                startActivityForResult( connectIntent,
                        REQUEST_CONNECT_DEVICE );
            }
        }
    }

    private String createBillPreview(String pID, String pName, String tName, String injCharges, String icuCharges, String fee, String totalCharges, String currentDate) {
        String BILL = "";
        BILL = "\n    SK Renal Care  \n";
        BILL = BILL

                + "    Dialysis center     \n";
        BILL = BILL +

                "    " + currentDate + "     \n";
        BILL = BILL
                + "------------------------------\n";

        BILL = BILL + " " + pID + " " + pName + "\n" + " Treated by " + "" + tName;
        BILL = BILL + "\n";
        //BILL = BILL + String.format("Injection:%d, price:%d","Injection","750/-");

        BILL = BILL + " ICU Charges" + "\t\t" + " " + icuCharges + "/-\n";
        BILL = BILL + " Injection" + "\t\t" + " " + injCharges + "/-\n";

        BILL = BILL + " Fee" + "\t\t\t" + " " + fee + "/-\n";

        BILL = BILL
                + "------------------------------\n";

        BILL = BILL + " Total Bill" + "\t\t" + " " + totalCharges + "/-\n";

        BILL = BILL
                + "------------------------------";
        BILL = BILL + "\n Thankyou for visiting us\n For more info +92332 5354254";
        BILL = BILL + "\n\n\n ";
        return BILL;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e( "Tag", "Exe ", e );
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e( "Tag", "Exe ", e );
        }
        setResult( RESULT_CANCELED );
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult( mRequestCode, mResultCode, mDataIntent );

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                try {


                    if (mResultCode == Activity.RESULT_OK) {
                        Bundle mExtra = mDataIntent.getExtras();
                        String mDeviceAddress = mExtra.getString( "DeviceAddress" );
                        Log.v( TAG, "Coming incoming address " + mDeviceAddress );
                        mBluetoothDevice = mBluetoothAdapter
                                .getRemoteDevice( mDeviceAddress );
                        mBluetoothConnectProgressDialog = ProgressDialog.show( this,
                                "Connecting...", mBluetoothDevice.getName()
                                , true, true );
                        Thread mBlutoothConnectThread = new Thread( this::run );
                        mBlutoothConnectThread.start();
                        // pairToDevice(mBluetoothDevice); This method is replaced by
                        // progress dialog with thread
                    }
                } catch (Exception e) {
                    Log.v( TAG, "No paired printer " + e.getMessage() );

                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent( PrintBill.this,
                            DeviceListActivity.class );
                    startActivityForResult( connectIntent, REQUEST_CONNECT_DEVICE );
                } else {
                    Log.e( "Message", "" );
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v( TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress() );
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord( applicationUUID );
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage( 0 );
        } catch (IOException eConnectException) {
            Log.d( TAG, "CouldNotConnectToSocket", eConnectException );
            closeSocket( mBluetoothSocket );
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d( TAG, "SocketClosed" );
        } catch (IOException ex) {
            Log.d( TAG, "CouldNotCloseSocket" );
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText( PrintBill.this, "Printer Connected", Toast.LENGTH_SHORT ).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate( 4 ).putInt( value ).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println( "Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex( b[k] ) );
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate( 2 );
        buffer.putInt( val );
        buffer.flip();
        return buffer.array();
    }

}

