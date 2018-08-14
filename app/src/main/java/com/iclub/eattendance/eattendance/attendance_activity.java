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

public class attendance_activity extends AppCompatActivity {

    //macros
    final boolean USE_SSL = false;
    final String DEBUG_TAG = "DEBUG_TAG";
    final String CLASS_ATTENDANCE_SUBMISSION_URL_SSL = "https://192.168.0.110/www/attendance.php";
    final String CLASS_ATTENDANCE_SUBMISSION_URL = "http://192.168.0.110/www/attendance.php";

    //defining all the UI widgets to be used
    TextView label_studentsPresent;
    TextView label_studentsAbsent;
    TextView label_studentsInfo;
    Button button_absent;
    Button button_present;
    Button button_back;
    ProgressBar progressBar;

    //global data to be used
    public String responseFromTheServer; //the JSON response from the server
    public JSONArray jsonArray; //json array that contains and parses the JSON data
    public int jsonIndex = 0; //current counter
    public int presentCounter = 0; //number of students present
    public int absentCounter = 0; //number of students absent
    public int totalJsonEntries = 0; //aka total number of students in the class
    public boolean studentAttendanceArray[] = new boolean[100]; //attendance for 100 students in a class at most
    public String toastString = null;
    public String qrcode = null;
    public String barcode = null;

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
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_activity);

        if (savedInstanceState != null) //meaning the activity was redrawn, so saved data from the bundle must be retrieved
        {
            studentAttendanceArray = (boolean[])savedInstanceState.getSerializable("studentAttendanceArray");
            jsonIndex = (int)savedInstanceState.getSerializable("jsonIndex");
            handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0); //redraw UI
        }


        label_studentsPresent = (TextView) findViewById(R.id.label_studentsPresent);
        label_studentsAbsent = (TextView) findViewById(R.id.label_studentsAbsent);
        label_studentsInfo = (TextView) findViewById(R.id.label_studentsInfo);
        button_absent = (Button) findViewById(R.id.button_absent);
        button_present = (Button) findViewById(R.id.button_present);
        button_back = (Button) findViewById(R.id.button_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        //getting intent from previous activity
        Intent intentFromMainActivity = getIntent();
        responseFromTheServer = intentFromMainActivity.getStringExtra("responseFromTheServer"); //getting the JSON response
        qrcode = intentFromMainActivity.getStringExtra("qrcode");
        barcode = intentFromMainActivity.getStringExtra("barcode");
        try {
            jsonArray = new JSONArray(responseFromTheServer); //parsing the JSON
            totalJsonEntries = jsonArray.length(); //getting the number of entries in the JSON
            progressBar.setMax(totalJsonEntries); //setting the progress bar's max as the number of students
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.toString()); //debug exception
        }

        handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0); //redraw UI
        //onclick listener for absent button
        button_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentAttendanceArray[jsonIndex] = false; //sets the person as absent
                jsonIndex++; //increments the json index, aka moves to the next person in the JSON list
                handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0); //starts the handler with the runnable for the first time
            }
        });

        //onclick listener for present button
        button_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentAttendanceArray[jsonIndex] = true; //sets the person as present
                jsonIndex++; //increments the json index, aka moves to the next person in the JSON list
                handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0); //starts the handler with the runnable for the first time
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonIndex != 0) //only if the index is not zero
                    jsonIndex--; //decrements the json index, aka moves to the previous person in the JSON list. Meant to serve as an way to make immediate ammends
                handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0); //starts the handler with the runnable for the first time
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
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saves activity data for resuming when the activity is redrawn (for example after an accidental or intentional orientation change)
        outState.putSerializable("studentAttendanceArray", studentAttendanceArray);
        outState.putSerializable("jsonIndex", jsonIndex);
    }
}

