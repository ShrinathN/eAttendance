package com.iclub.eattendance.eattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //gets the intent from
        Intent intent = getIntent();
        String resultString = intent.getStringExtra("resultString");
        TextView label_result = (TextView)findViewById(R.id.label_result);

        label_result.setText(resultString);

    }

    //to close the activity on back button press
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
