package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.version1.R;
import com.example.version1.database.UniversityIntroductionDB;
import com.example.version1.database.UniversityTourAccessDB;
import com.example.version1.domain.UniversityIntroduction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class Activity_univ_introduction extends AppCompatActivity {

    Button button1;
    Button button2;
    String univName;
    ArrayList<UniversityIntroduction> universityIntroductionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_univ_introduction);

        UniversityIntroductionDB universityIntroductionDB = new UniversityIntroductionDB();
        universityIntroductionArray = universityIntroductionDB.getUniversityIntroductionFromDB(univName);

        /*
        universityIntroductionArray의 정보를 화면에 출력하도록 함
        */

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        univName = intent.getStringExtra("univName");

        //db에서 univName에 해당하는 정보를 불러온다.
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button1.setOnClickListener(ClickListener1);
        button2.setOnClickListener(ClickListener2);

    }

    Button.OnClickListener ClickListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };

    Button.OnClickListener ClickListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Activity_univ_introduction.this, Activity_eachUniversityMap.class);
            //대학교 이름도 넘겨줌
            intent.putExtra("univName", univName);
            startActivity(intent);
        }
    };

}
