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

import org.json.JSONArray;
import org.json.JSONObject;

public class attendance_activity extends AppCompatActivity {

    //macros
    final String DEBUG_TAG = "DEBUG_TAG";

    //global data to be used
    public String responseFromTheServer;
    public JSONArray jsonArray;
    public int jsonIndex = 0;
    public int presentCounter = 0;
    public int absentCounter = 0;
    public int totalJsonEntries = 0;
    public boolean studentAttendanceArray[] = new boolean[100];

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
            jsonArray = new JSONArray(responseFromTheServer);
            totalJsonEntries = jsonArray.length();
            progressBar.setMax(totalJsonEntries);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.toString());
        }


        //runnable and handler to update UI
        final Handler handlerToUpdateUi = new Handler();
        final Runnable runnableToUpdateUi = new Runnable() {
            @Override
            public void run() {
                label_studentsPresent.setText(Integer.toString(presentCounter) + " students present");
                label_studentsAbsent.setText(Integer.toString(absentCounter) + " students absent");
                progressBar.setProgress(jsonIndex);
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(jsonIndex);
                    label_studentsInfo.setText(jsonObject.getString("name") + "\n" + Integer.toString(jsonObject.getInt("regno")));
                } catch (Exception e) {
                    Log.d(DEBUG_TAG, e.toString());
                }
                handlerToUpdateUi.postDelayed(this, 150);
            }
        };
        handlerToUpdateUi.postDelayed(runnableToUpdateUi, 0);


        //onclick listener for absent button
        button_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                absentCounter++;
                studentAttendanceArray[jsonIndex] = false;
                jsonIndex++;
            }
        });

        //onclick listener for present button
        button_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentCounter++;
                studentAttendanceArray[jsonIndex] = true;
                jsonIndex++;
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonIndex--;
            }
        });
    }
}
