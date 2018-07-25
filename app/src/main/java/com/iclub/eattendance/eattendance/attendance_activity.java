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
    final String DEBUG_TAG = "DEBUG_TAG";

    //global data to be used
    public String responseFromTheServer; //the JSON response from the server
    public JSONArray jsonArray; //json array that contains and parses the JSON data
    public int jsonIndex = 0; //current counter
    public int presentCounter = 0; //number of students present
    public int absentCounter = 0; //number of students absent
    public int totalJsonEntries = 0; //aka total number of students in the class
    public boolean studentAttendanceArray[] = new boolean[100]; //attendance for 100 students in a class at most

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
                if(jsonIndex == totalJsonEntries)
                {
                    Toast.makeText(attendance_activity.this, "Reached the end", Toast.LENGTH_LONG).show();
                }

                //calculating the number of students absent and present
                int tempCounter;
                presentCounter = 0;
                absentCounter = 0;
                for(tempCounter = 0; tempCounter < jsonIndex; tempCounter++)
                {
                    if(studentAttendanceArray[tempCounter]) //if the person is present
                        presentCounter++; //increment present counter
                    else
                        absentCounter++; //increment absent counter
                }
                //setting the number of students absent and present
                label_studentsPresent.setText(Integer.toString(presentCounter) + " students present");
                label_studentsAbsent.setText(Integer.toString(absentCounter) + " students absent");
                progressBar.setProgress(jsonIndex); //sets the progress bar's current progress
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(jsonIndex); //gets the current
                    label_studentsInfo.setText(jsonObject.getString("name") + "\n" + Integer.toString(jsonObject.getInt("regno"))); //sets the name and the regno on the label_studentsInfo textview
                } catch (Exception e) {
                    Log.d(DEBUG_TAG, e.toString()); //debug exception
                }

                handlerToUpdateUi.postDelayed(this,  150); //starts the runnable using the handler, recursively loops forever
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
                if(jsonIndex != 0) //only if the index is not zero
                jsonIndex--; //decrements the json index, aka moves to the previous person in the JSON list. Meant to serve as an way to make immediate ammends
            }
        });
    }

    //this is to disable back press, restricting the user from going back to the login screen
    @Override
    public void onBackPressed() {
    }
}

