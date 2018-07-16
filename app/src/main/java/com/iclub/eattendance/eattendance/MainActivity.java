package com.iclub.eattendance.eattendance;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // to be used as macros
    final byte BARCODE_SCAN = 1;
    final byte QRCODE_SCAN = 2;

    //all the variables to be used
    //related to ui updating
    public boolean uiUpdateRequest = false;
    public byte uiUpdateRequestType = 0;
    public String barcode = null;
    public String qrcode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //just some UI elements being declared
        final Button button_scanBarcode = (Button)findViewById(R.id.button_scanBarcode);
        final Button button_scanQr = (Button)findViewById(R.id.button_scanQr);
        final Button button_next = (Button)findViewById(R.id.button_next);

        final Handler handlerToUpdateUi = new Handler(); //this handler will update the UI elements
        Runnable runnableToUpdateUi = new Runnable() {
            @Override
            public void run() {
                if(uiUpdateRequest) // run only if the UI update request variable is true
                {
                    uiUpdateRequest = false; //set the variable as false, ie reset it
                    if(uiUpdateRequestType == BARCODE_SCAN)
                    {
                        button_scanBarcode.setText("Rescan\n" + barcode);
                    }
                    else if(uiUpdateRequestType == QRCODE_SCAN)
                    {
                        button_scanQr.setText("Rescan\n" + qrcode);
                    }
                    if((barcode != null) && (qrcode != null)) //if both strings are not empty, hence meaning both details have been scanned, proceed to make the next button visible
                        button_next.setVisibility(View.VISIBLE);
                }
                handlerToUpdateUi.postDelayed(this,300);
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

        //this is the onclick listener for QRcode
        button_scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToScanQrCode = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intentToScanQrCode, QRCODE_SCAN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        uiUpdateRequest = true;
        if(requestCode == BARCODE_SCAN)
        {
            uiUpdateRequestType = BARCODE_SCAN;
            barcode = resultIntent.getStringExtra("SCAN_RESULT");
        }
        else if(requestCode == QRCODE_SCAN)
        {
            uiUpdateRequestType = QRCODE_SCAN;
            qrcode = resultIntent.getStringExtra("SCAN_RESULT");
        }
    }


}
