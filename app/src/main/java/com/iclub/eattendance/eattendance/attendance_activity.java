package com.iclub.eattendance.eattendance;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class attendance_activity extends AppCompatActivity {

    //macros
    final boolean USE_SSL = false;
    final String DEBUG_TAG = "DEBUG_TAG";
    final String CLASS_ATTENDANCE_SUBMISSION_URL_SSL = "https://192.168.0.110/www/attendance.php";
    final String CLASS_ATTENDANCE_SUBMISSION_URL = "http://192.168.0.110/www/attendance.php";


    //global data to be used
    public String responseFromTheServer; //the JSON response from the server
    public JSONArray jsonArray; //json array that contains and parses the JSON data
    public int jsonIndex = 0; //current counter
    public int presentCounter = 0; //number of students present
    public int absentCounter = 0; //number of students absent
    public int totalJsonEntries = 0; //aka total number of students in the class
    public boolean studentAttendanceArray[] = new boolean[100]; //attendance for 100 students in a class at most
    public String toastString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_activity);

        //defining all the UI widgets to be used
        final TextView label_studentsPresent = (TextView) findViewById(R.id.label_studentsPresent);
        final TextView label_studentsAbsent = (TextView) findViewById(R.id.label_studentsAbsent);
        final TextView label_studentsInfo = (TextView) findViewById(R.id.label_studentsInfo);
        final Button button_absent = (Button) findViewById(R.id.button_absent);
        final Button button_present = (Button) findViewById(R.id.button_present);
        final Button button_back = (Button) findViewById(R.id.button_back);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //getting intent from previous activity
        Intent intentFromMainActivity = getIntent();
        responseFromTheServer = intentFromMainActivity.getStringExtra("responseFromTheServer"); //getting the JSON response
        try {
            jsonArray = new JSONArray(responseFromTheServer); //parsing the JSON
            totalJsonEntries = jsonArray.length(); //getting the number of entries in the JSON
            progressBar.setMax(totalJsonEntries); //setting the progress bar's max as the number of students
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.toString()); //debug exception
        }


        //runnable and handler to update UI widgets
        final Handler handlerToUpdateUi = new Handler();
        final Runnable runnableToUpdateUi = new Runnable() {
            @Override
            public void run() {
                //checks if all the students in the list have been addressed already
                if (jsonIndex == totalJsonEntries) {
                    button_absent.setVisibility(View.INVISIBLE);
                    button_present.setVisibility(View.INVISIBLE);
                    uploadAttendance();
                } else {
                    button_absent.setVisibility(View.VISIBLE);
                    button_present.setVisibility(View.VISIBLE);
                }

                if(toastString != null)
                {
                    Toast.makeText(attendance_activity.this, toastString, Toast.LENGTH_LONG).show();
                    toastString = null;
                }

                //calculating the number of students absent and present
                int tempCounter;
                presentCounter = 0;
                absentCounter = 0;
                for (tempCounter = 0; tempCounter < jsonIndex; tempCounter++) {
                    if (studentAttendanceArray[tempCounter]) //if the person is present
                        presentCounter++; //increment present counter
                    else
                        absentCounter++; //increment absent counter
                }
                //setting the number of students absent and present
                label_studentsPresent.setText(Integer.toString(presentCounter) + " students present");
                label_studentsAbsent.setText(Integer.toString(absentCounter) + " students absent");
                progressBar.setProgress(jsonIndex); //sets the progress bar's current progress
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(jsonIndex); //gets the current students name
                    label_studentsInfo.setText(jsonObject.getString("name") + "\n" + Integer.toString(jsonObject.getInt("regno"))); //sets the name and the regno on the label_studentsInfo textview
                } catch (Exception e) {
                    Log.d(DEBUG_TAG, e.toString()); //debug exception
                }

                handlerToUpdateUi.postDelayed(this, 150); //starts the runnable using the handler, recursively loops forever
            }
        };
        handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0); //starts the handler with the runnable for the first time





        //onclick listener for absent button
        button_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentAttendanceArray[jsonIndex] = false; //sets the person as absent
                jsonIndex++; //increments the json index, aka moves to the next person in the JSON list
            }
        });

        //onclick listener for present button
        button_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentAttendanceArray[jsonIndex] = true; //sets the person as present
                jsonIndex++; //increments the json index, aka moves to the next person in the JSON list
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonIndex != 0) //only if the index is not zero
                    jsonIndex--; //decrements the json index, aka moves to the previous person in the JSON list. Meant to serve as an way to make immediate ammends
            }
        });
    }

    //this is to disable back press, restricting the user from going back to the login screen
    @Override
    public void onBackPressed() {
    }

    //this function will upload the attendance to the server
    public void uploadAttendance() {
        String stringToSend = ""; //string ready to be written to
        for (int tempCounter = 0; tempCounter < totalJsonEntries; tempCounter++) //adds the attendance to the string
        {
            if (studentAttendanceArray[tempCounter]) //meaning the student is present
                stringToSend = stringToSend + "P"; //a simple 'P' if the student is present
            else
                stringToSend = stringToSend + "A"; //an 'A' character if the student is absent
        }
        try {
            if (USE_SSL) {
                URL url = new URL(CLASS_ATTENDANCE_SUBMISSION_URL_SSL + "?attendance_list=" + stringToSend); //setting the URL to make the GET request
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.connect(); //connect to the server
                httpsURLConnection.disconnect(); //disconnect
            } else { //if SSL is not needed
                URL url = new URL(CLASS_ATTENDANCE_SUBMISSION_URL + "?attendance_list=" + stringToSend); //setting the URL to make the GET request
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET"); //setting the method of the request
                httpURLConnection.connect(); //connect to the URL
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                responseFromTheServer = bufferedReader.readLine(); //receive the response from the server
                Log.d(DEBUG_TAG, responseFromTheServer);
                httpURLConnection.disconnect(); //disconnect from the server
            }
            //==================This portion will need to be edited if the app is being updated to take attendance for all the classes==================
            if (responseFromTheServer.compareToIgnoreCase("ALREADY_PRESENT") == 0) { //this means today's attendance has already been taken
                toastString = "Attendance for today has already been taken";
            }
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.toString());
        }
    }
}

