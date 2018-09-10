package com.iclub.eattendance.eattendance;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class OverviewActivity extends AppCompatActivity {

    //macros
    final String DEBUG_TAG = "DEBUG_TAG";
    final String ATTENDANCE_PATH = "/www/attendance.php";
    final String REATTENDANCE_LIST_PATH = "/www/reattendance_list.php";
    final String REATTENDANCE_PATH = "/www/reattendance.php";
    final String LOGIN_PATH = "/www/login.php";
    final String LIST_STUDENTS_PATH = "/www/list_students.php";

    final String REATTENDANCE = "REATTENDANCE";
    final String NEWATTENDANCE = "NEWATTENDANCE";

    //global variables
    public String qrcode = null;
    public String barcode = null;
    public String server = null;
    public boolean USE_SSL = false;
    public int totalJsonEntries = 0; //aka total number of students in the class
    public boolean studentAttendanceArray[] = new boolean[100]; //attendance for 100 students in a class at most
    public JSONArray jsonArrayForStudentList;
    public JSONArray jsonArrayForAttendanceList;
    public String responseFromTheServer;
    public String ATTENDANCE_TYPE = null;
    public String stringToSend = "";
    public int jsonIndex = 0; //current counter
    public boolean networkThreadNotRunning = false;

    private List<Student> studentList = new ArrayList<>();
    private RecyclerView SRec;
    private RecyclerView.Adapter Sadap;
    private RecyclerView.LayoutManager Slay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intentFromAttendanceActivity = getIntent();

        qrcode = intentFromAttendanceActivity.getStringExtra("qrcode");
        barcode = intentFromAttendanceActivity.getStringExtra("barcode");
        server = intentFromAttendanceActivity.getStringExtra("server");
        USE_SSL = intentFromAttendanceActivity.getBooleanExtra("USE_SSL", false);
        //this determines if attendance has already been taken today and is just being updated or is being taken for the first time2
        ATTENDANCE_TYPE = intentFromAttendanceActivity.getStringExtra("ATTENDANCE_TYPE");

        if (ATTENDANCE_TYPE.compareToIgnoreCase(NEWATTENDANCE) == 0) //attendance being taken for the first time today
        {
            //these two details are available only if the last activity has sent them with the intent
            responseFromTheServer = intentFromAttendanceActivity.getStringExtra("responseFromTheServer"); //getting the JSON response
            studentAttendanceArray = intentFromAttendanceActivity.getBooleanArrayExtra("studentAttendanceArray");
            try { //try to parse the JSON from the last activity
                jsonArrayForStudentList = new JSONArray(responseFromTheServer);
                totalJsonEntries = jsonArrayForStudentList.length(); //setting the total number of JSON entries

                SRec = (RecyclerView) findViewById(R.id.my_rec); //recycler view
                SRec.setHasFixedSize(true);
                Slay = new LinearLayoutManager(getApplicationContext());
                SRec.setLayoutManager(Slay);
                SRec.setItemAnimator(new DefaultItemAnimator());

                Sadap = new MyAdapter(studentList);
                SRec.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
                SRec.setAdapter(Sadap);

                populateRecyclerView();

            } catch (Exception e) {
                Log.d(DEBUG_TAG, e.toString());
            }
        }
        else if(ATTENDANCE_TYPE.compareToIgnoreCase(REATTENDANCE) == 0) //attendance has already been taken today and is just being updated
        {
            Log.d(DEBUG_TAG, "REATTENDANCE SECTION");
            try {
                obtainDetails(server + LIST_STUDENTS_PATH + "?staff_id=" + barcode + "&class_id=" + qrcode);
                while(!networkThreadNotRunning){};

                //will parse the json from the server
                jsonArrayForStudentList = new JSONArray(responseFromTheServer);
                totalJsonEntries = jsonArrayForStudentList.length(); //setting the total number of JSON entries


                SRec = (RecyclerView) findViewById(R.id.my_rec); //recycler view
                SRec.setHasFixedSize(true);
                Slay = new LinearLayoutManager(getApplicationContext());
                SRec.setLayoutManager(Slay);
                SRec.setItemAnimator(new DefaultItemAnimator());

                Sadap = new MyAdapter(studentList);
                SRec.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
                SRec.setAdapter(Sadap);

                //now we'll obtain the student attendance for today, that is already on the server
                obtainDetails(server + REATTENDANCE_LIST_PATH + "?staff_id=" + barcode + "&class_id=" + qrcode);
                while(!networkThreadNotRunning){};

                //will parse the json from the server
                jsonArrayForAttendanceList = new JSONArray(responseFromTheServer);

                for(jsonIndex = 0; jsonIndex < totalJsonEntries; jsonIndex++)
                {
                    JSONObject jsonObjectForStudentName = jsonArrayForStudentList.getJSONObject(jsonIndex);
                    JSONObject jsonObjectForAttendanceList = jsonArrayForAttendanceList.getJSONObject(0);
                    if(jsonObjectForAttendanceList.getString(jsonObjectForStudentName.getString("name").trim().toString()).compareToIgnoreCase("P") == 0) //if the student is present
                        studentAttendanceArray[jsonIndex] = true;
                    else if(jsonObjectForAttendanceList.getString(jsonObjectForStudentName.getString("name").trim().toString()).compareToIgnoreCase("A") == 0) //if the student is absent
                        studentAttendanceArray[jsonIndex] = false;
                }
                populateRecyclerView();
            }
            catch(Exception e)
            {
                Log.d(DEBUG_TAG, e.toString());
            }
        }
    }

    private void populateRecyclerView() {

        int jsonIndex = 0;
        try {
            for (jsonIndex = 0; jsonIndex < totalJsonEntries; jsonIndex++) {
                JSONObject jsonObject = jsonArrayForStudentList.getJSONObject(jsonIndex); //gets the current students name
                Student student = new Student(jsonObject.getString("name"),
                        jsonObject.getString("regno"),
                        studentAttendanceArray[jsonIndex], jsonIndex);
                studentList.add(student);
            }
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.overviewsubmitmenu, menu);
        return true;
    }


    //this is to disable back press, restricting the user from going back to the login screen
    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.button_submit) {
            uploadAttendance();
        }
        return true;
    }


    //if supplied with a valid serverPath, this method will obtain the data from the server and store it in the responseFromTheServer variable
    public void obtainDetails(final String serverPath)
    {
        networkThreadNotRunning = false;
        Runnable runnableToMakeHTTPRequest = new Runnable() {
            @Override
            public void run() {
                try
                {
                    if (USE_SSL) { //TODO: Fix HTTPS/SSL operation
                        URL url = new URL(serverPath); //setting the URL to make the GET request
                        Log.d(DEBUG_TAG, "Connecting to " + url.toString());
                        Log.d(DEBUG_TAG, "RUNNING WITH SSL");
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.connect(); //connect to the server
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        responseFromTheServer = bufferedReader.readLine(); //receive the response from the server
                        Log.d(DEBUG_TAG, responseFromTheServer);
                        bufferedReader.close();
                        httpsURLConnection.disconnect(); //disconnect
                    } else { //if SSL is not needed
                        URL url = new URL(serverPath); //setting the URL to make the GET request
                        Log.d(DEBUG_TAG, "Connecting to " + url.toString());
                        Log.d(DEBUG_TAG, "RUNNING WITHOUT SSL");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("GET"); //setting the method of the request
                        httpURLConnection.connect(); //connect to the URL
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        responseFromTheServer = bufferedReader.readLine(); //receive the response from the server
                        Log.d(DEBUG_TAG, responseFromTheServer);
                        bufferedReader.close();
                        httpURLConnection.disconnect(); //disconnect from the server
                    }
                }
                catch (Exception e)
                {
                    Log.d(DEBUG_TAG, e.toString());
                }
                networkThreadNotRunning = true;
            }
        };
        Thread threadToMakeHTTPRequest = new Thread(runnableToMakeHTTPRequest);
        threadToMakeHTTPRequest.start();
    }


    public void uploadAttendance() {
        if (ATTENDANCE_TYPE.compareTo(NEWATTENDANCE) == 0) //attendance being taken for the first time today
        {
            //making a string to send
            for(jsonIndex = 0; jsonIndex < totalJsonEntries; jsonIndex++)
                stringToSend = stringToSend + (studentList.get(jsonIndex).getstatus()?"P":"A"); // P for present, and A for absent
            obtainDetails(server + ATTENDANCE_PATH + "?staff_id=" + barcode + "&class_id=" + qrcode + "&attendance=" + stringToSend);
            while(!networkThreadNotRunning){};
            Log.d(DEBUG_TAG,Integer.toString(responseFromTheServer.compareTo("OK")));
            if(responseFromTheServer.compareTo("OK") == 0)
            {
                Log.d(DEBUG_TAG,"INSIDE THIS");
                Intent intentToStartResultActivity = new Intent(this, ResultActivity.class);
                intentToStartResultActivity.putExtra("resultString", "The attendance was successfully entered into the database");
                startActivity(intentToStartResultActivity);
            }
        }
        else if (ATTENDANCE_TYPE.compareTo(REATTENDANCE) == 0) //attendance is being updated
        {

        }
    }
}
