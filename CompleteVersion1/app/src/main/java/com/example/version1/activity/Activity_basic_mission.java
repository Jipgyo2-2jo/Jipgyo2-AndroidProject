package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.R;
import com.example.version1.domain.MissionQuiz;

public class Activity_basic_mission extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_mission);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        MissionQuiz missionQuiz = (MissionQuiz) intent.getSerializableExtra("missionQuiz");

        Toast.makeText(this, "선택: "+missionQuiz.getId(), Toast.LENGTH_SHORT).show();
    }

}
