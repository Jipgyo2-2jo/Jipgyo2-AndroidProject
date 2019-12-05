package com.example.version1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Activity_tour_finish extends AppCompatActivity {

    Button buttonBack;
    Button buttonCourse;
    Button buttonMain;
    Set<String> schoolClearSet;
    long ran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_finish);

        Intent intent = getIntent();
        String univName = intent.getStringExtra("univName");
        int missionNum = intent.getIntExtra("missionNum", 0);
        ran = intent.getLongExtra("randnum", 0);

        TextView randnum = findViewById(R.id.randnum);
        buttonBack = findViewById(R.id.button1);
        buttonCourse = findViewById(R.id.button2);
        buttonMain = findViewById(R.id.button3);
        TextView textView = findViewById(R.id.Clear);
        textView.setText("미션"+missionNum+"개 Clear!");

        schoolClearSet = new HashSet<>();
        SharedPreferences sharedPreferences = getSharedPreferences("SchoolClearFile", MODE_PRIVATE);
        schoolClearSet = sharedPreferences.getStringSet("SchoolClearSet", schoolClearSet);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("SchoolClearSet");
        schoolClearSet.add(univName);
        editor.putStringSet("SchoolClearSet", schoolClearSet);
        editor.commit();

        buttonBack.setOnClickListener(buttonBackClickListener);
        buttonCourse.setOnClickListener(buttonCourseClickListener);
        buttonMain.setOnClickListener(buttonMainClickListener);

        randnum.setText(""+ran);
    }

    Button.OnClickListener buttonBackClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };

    Button.OnClickListener buttonCourseClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            setResult(200, intent);
            finish();
        }
    };

    Button.OnClickListener buttonMainClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            setResult(300, intent);
            finish();
        }
    };

}
