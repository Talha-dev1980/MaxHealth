package com.medical.dialysiscenter.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MessageDialog {


    public MessageDialog() {
    }

    public MessageDialog(String message, String title, Context context) {

        new AlertDialog.Builder( context ).setMessage( message ).setTitle( title ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        } ).setCancelable( false ).create().show();
    }

    public void showMessageAndGoBack(String message, String title, Context context) {
        new AlertDialog.Builder( context ).setMessage( message ).setTitle( title ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        } ).setCancelable( false ).create().show();
    }
}
