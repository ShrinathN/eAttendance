package com.iclub.eattendance.eattendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    // to be used as macros
    final byte BARCODE_SCAN = 1;
    final byte QRCODE_SCAN = 2;

    //all the variables to be used
    public String barcode = null;
    public String qrcode = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) //meaning the activity was redrawn, so saved data from the bundle must be retrieved
        {
            barcode = (String)savedInstanceState.getSerializable("barcode"); //get the barcode string from the saved bundle
            qrcode = (String)savedInstanceState.getSerializable("qrcode");//get the qrcode string from the saved bundle
        }

        //just some UI elements being declared
        final Button button_scanBarcode = (Button)findViewById(R.id.button_scanBarcode);
        final Button button_scanQr = (Button)findViewById(R.id.button_scanQr);
        final Button button_next = (Button)findViewById(R.id.button_next);

        //this handler-runnable combo will update the UI elements
        final Handler handlerToUpdateUi = new Handler(); //this handler will update the UI elements
        Runnable runnableToUpdateUi = new Runnable() {
            @Override
            public void run() {
                if (barcode != null) //if the barcode has already been scanned, the default value of string barcode is null
                    button_scanBarcode.setText("Rescan\n" + barcode); //set the button's text as
                if (qrcode != null) //if the qrcode has already been scanned, the default value of string qrcode is null
                    button_scanQr.setText("Rescan\n" + qrcode); //set the button's text as

                if ((barcode != null) && (qrcode != null)) //if both strings are not empty, hence meaning both details have been scanned, proceed to make the next button visible
                    button_next.setVisibility(View.VISIBLE); //makes the next activity button visible
                handlerToUpdateUi.postDelayed(this, 300); //recursion to loop the runnable indefinitely
            }
        };
        handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0);

        //this is the onclick listener for the barcode to identify the user
        button_scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToScanBarcode = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intentToScanBarcode, BARCODE_SCAN);
            }
        });

        //this is the onclick listener for QRcode s
        button_scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToScanQrCode = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intentToScanQrCode, QRCODE_SCAN);
            }
        });
    }

    //when the result of scanning activity is
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        if(resultCode != RESULT_CANCELED) { //only execute if the barcode scanning activity was not closed, and was successful
            if (requestCode == BARCODE_SCAN) { //if barcode was scanned
                barcode = resultIntent.getStringExtra("SCAN_RESULT"); //set string barcode as scanned string
            } else if (requestCode == QRCODE_SCAN) { //else if qrcode was scanned
                qrcode = resultIntent.getStringExtra("SCAN_RESULT"); //set string qrcode as scanned string
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saves activity data for resuming when the activity is redrawn (for example after an orientation change)
        outState.putSerializable("barcode", barcode);
        outState.putSerializable("qrcode", qrcode);
    }

}
