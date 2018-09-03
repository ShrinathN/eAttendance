//Name: Shrinath Nimare
//Description: This activity is the main and launcher activity for this application. This will enable the user to enter their details using a simple barcode reader application
package com.iclub.eattendance.eattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.iclub.eattendance.eattendance.ServerSelectorAlert;

//currently set to testing URL

public class MainActivity extends AppCompatActivity {
    // to be used as macros
    final boolean USE_SSL = false;
    final String DEBUG_TAG = "DEBUG_TAG";
    //==============HAVE TO CHANGE==============
    final String CLASS_INFO_SUBMISSION_URL = "http://192.168.0.110/www/login.php";
    final String CLASS_INFO_SUBMISSION_URL_SSL = "https://192.168.0.110/www/login.php";
    final byte BARCODE_SCAN = 1;
    final byte QRCODE_SCAN = 2;

    //all the variables to be used
    public String barcode = null;
    public String qrcode = null;
    public String toastString = null; //used to make toasts from inside a thread

    //just some UI elements being declared
    Button button_scanBarcode;
    Button button_scanQr;
    Button button_next;

    //this handler-runnable combo will update the UI elements (on call only)
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

            if (toastString != null) {
                Toast.makeText(MainActivity.this, toastString, Toast.LENGTH_LONG).show();
                toastString = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) //meaning the activity was redrawn, so saved data from the bundle must be retrieved
        {
            barcode = (String) savedInstanceState.getSerializable("barcode"); //get the barcode string from the saved bundle
            qrcode = (String) savedInstanceState.getSerializable("qrcode");//get the qrcode string from the saved bundle
            handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0); //redraw UI
        }


        button_scanBarcode = (Button) findViewById(R.id.button_scanBarcode);
        button_scanQr = (Button) findViewById(R.id.button_scanQr);
        button_next = (Button) findViewById(R.id.button_next);


        //this is the onclick listener for the barcode to identify the user
        button_scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToScanBarcode = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intentToScanBarcode, BARCODE_SCAN);

            }
        });

        //this is the onclick listener for the next button, the button that calls the method that sends over the staff and class data, and receives the list of students in the class
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable serverConnectRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String responseFromTheServer; //this will contain the response from the server ofc
                        try {
                            if (USE_SSL) { //TODO: Fix HTTPS/SSL operation
                                URL url = new URL(CLASS_INFO_SUBMISSION_URL_SSL + "?staff_id=" + barcode + "&class_id=" + qrcode); //setting the URL to make the GET request
                                Log.d(DEBUG_TAG, "Connecting to " + url.toString());
                                Log.d(DEBUG_TAG, "RUNNING WITH SSL");
                                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                                httpsURLConnection.connect(); //connect to the server
                                httpsURLConnection.disconnect(); //disconnect
                            } else { //if SSL is not needed
                                URL url = new URL(CLASS_INFO_SUBMISSION_URL + "?staff_id=" + barcode + "&class_id=" + qrcode); //setting the URL to make the GET request
                                Log.d(DEBUG_TAG, "Connecting to " + url.toString());
                                Log.d(DEBUG_TAG, "RUNNING WITHOUT SSL");
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("GET"); //setting the method of the request
                                httpURLConnection.connect(); //connect to the URL
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                responseFromTheServer = bufferedReader.readLine(); //receive the response from the server
                                Log.d(DEBUG_TAG, responseFromTheServer);
                                httpURLConnection.disconnect(); //disconnect from the server
                            }
                            if (responseFromTheServer.compareToIgnoreCase("ERROR_ID") == 0) { //if ID is invalid
                                toastString = "Invalid ID";
                            } else if (responseFromTheServer.compareToIgnoreCase("ERROR_CLASS") == 0) { //if class is invalid
                                toastString = "Invalid Class";
                            } else if (responseFromTheServer.compareToIgnoreCase("ERROR_ATTENDANCE_ALREADY_BEEN_TAKEN_FOR_TODAY") == 0) { //this means the attendance has been already taken for today, go to reattendance activity
                                Intent intentToStartReattendanceActivity = new Intent(MainActivity.this, Reattendance_activity.class);
                                intentToStartReattendanceActivity.putExtra("responseFromTheServer", responseFromTheServer);
                                intentToStartReattendanceActivity.putExtra("barcode", barcode);
                                intentToStartReattendanceActivity.putExtra("qrcode", qrcode);
                                startActivity(intentToStartReattendanceActivity);
                            } else {
                                Intent intentToStartAttendanceActivity = new Intent(MainActivity.this, attendance_activity.class);
                                intentToStartAttendanceActivity.putExtra("responseFromTheServer", responseFromTheServer);
                                intentToStartAttendanceActivity.putExtra("barcode", barcode);
                                intentToStartAttendanceActivity.putExtra("qrcode", qrcode);
                                startActivity(intentToStartAttendanceActivity);
                            }
                        } catch (Exception e) {
                            Log.d(DEBUG_TAG, "!!!ERROR!!!-" + e.toString());
                        }
                    }
                };
                Thread thread = new Thread(serverConnectRunnable);
                thread.start(); //starts a background thread to execute the runnable
            }
        });

        //this is the onclick listener for QRcode scan button
        button_scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToScanQrCode = new Intent("com.google.zxing.client.android.SCAN"); //intent to scan
                startActivityForResult(intentToScanQrCode, QRCODE_SCAN); //starting activity for a result
            }
        });
    }

    //when the result of scanning activity is
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (resultCode != RESULT_CANCELED) { //only execute if the barcode scanning activity was not closed, and was successful
            if (requestCode == BARCODE_SCAN) { //if barcode was scanned
                barcode = resultIntent.getStringExtra("SCAN_RESULT"); //set string barcode as scanned string
            } else if (requestCode == QRCODE_SCAN) { //else if qrcode was scanned
                qrcode = resultIntent.getStringExtra("SCAN_RESULT"); //set string qrcode as scanned string
            }
            handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saves activity data for resuming when the activity is redrawn (for example after an accidental or intentional orientation change)
        outState.putSerializable("barcode", barcode);
        outState.putSerializable("qrcode", qrcode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.serverselectormenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.setservermenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.serverselectorlayout,null))
                    .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
        return true;
    }

}

