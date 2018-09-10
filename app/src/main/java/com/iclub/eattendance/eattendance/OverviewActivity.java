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
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity {

    //macros
    final String DEBUG_TAG = "DEBUG_TAG";
//    final String ATTENDANCE_TYPE = "ATTENDANCE_TYPE";
    final String REATTENDANCE = "REATTENDANCE";
    final String NEWATTENDANCE = "NEWATTENDANCE";

    //global variables
    public String qrcode = null;
    public String barcode = null;
    public String server = null;
    public boolean USE_SSL = false;
    public int totalJsonEntries = 0; //aka total number of students in the class
    public boolean studentAttendanceArray[] = new boolean[100]; //attendance for 100 students in a class at most
    public JSONArray jsonArray;
    public String responseFromTheServer;
    public String ATTENDANCE_TYPE = null;


    private List<Student> studentList = new ArrayList<>();
    private RecyclerView SRec;
    private RecyclerView.Adapter Sadap;
    private RecyclerView.LayoutManager Slay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intentFromAttendanceActivity = getIntent();

        responseFromTheServer = intentFromAttendanceActivity.getStringExtra("responseFromTheServer"); //getting the JSON response
        qrcode = intentFromAttendanceActivity.getStringExtra("qrcode");
        barcode = intentFromAttendanceActivity.getStringExtra("barcode");
        server = intentFromAttendanceActivity.getStringExtra("server");
        USE_SSL = intentFromAttendanceActivity.getBooleanExtra("USE_SSL", false);
        studentAttendanceArray = intentFromAttendanceActivity.getBooleanArrayExtra("studentAttendanceArray");
        ATTENDANCE_TYPE = intentFromAttendanceActivity.getStringExtra("ATTENDANCE_TYPE");

        if(ATTENDANCE_TYPE.compareToIgnoreCase(NEWATTENDANCE) == 0) //attendance being taken for the first time today
        try
        {
            jsonArray = new JSONArray(responseFromTheServer);
            totalJsonEntries = jsonArray.length();
        }
        catch (Exception e)
        {
            Log.d(DEBUG_TAG, e.toString());
        }


        SRec = (RecyclerView) findViewById(R.id.my_rec);

        SRec.setHasFixedSize(true);

        Slay = new LinearLayoutManager(getApplicationContext());
        SRec.setLayoutManager(Slay);
        SRec.setItemAnimator(new DefaultItemAnimator());

        Sadap = new MyAdapter(studentList);
        SRec.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        SRec.setAdapter(Sadap);

        getStudentDetails();
    }

    private void getStudentDetails(){

        int jsonIndex = 0;
        try {
            for (jsonIndex = 0; jsonIndex < totalJsonEntries; jsonIndex++) {
                JSONObject jsonObject = jsonArray.getJSONObject(jsonIndex); //gets the current students name
                Student student = new Student(jsonObject.getString("name"),
                        jsonObject.getString("regno"),
                        studentAttendanceArray[jsonIndex], jsonIndex);
                studentList.add(student);
            }
//            setContentView(R.layout.overviewactivitybaselayout);
        }
        catch(Exception e)
        {
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

    public void uploadAttendance()
    {
    }
}
