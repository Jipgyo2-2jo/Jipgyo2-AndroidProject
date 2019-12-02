package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.version1.R;
import com.example.version1.domain.UniversityTour;

public class Activity_loc_information extends AppCompatActivity {

    TextView locname;
    ImageView locimage;
    UniversityTour universityTour;
    String univName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_information);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        universityTour = (UniversityTour) intent.getSerializableExtra("locationDesc");
        univName = intent.getStringExtra("univName");

        locname = (TextView) findViewById(R.id.locName);
        locimage = (ImageView) findViewById(R.id.locImage);

        locname.setText(universityTour.get시설());

        String imageUrl = "http://3.114.244.9/"+univName+"/"+universityTour.getId_num()+".jpg";
        Glide.with(this).load(imageUrl).into(locimage);
    }
}
