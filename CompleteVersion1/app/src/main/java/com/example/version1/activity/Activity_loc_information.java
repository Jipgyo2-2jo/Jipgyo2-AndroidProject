package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.version1.R;
import com.example.version1.domain.UniversityTour;

public class Activity_loc_information extends AppCompatActivity {

    TextView locname;
    ImageView locimage;
    UniversityTour universityTour;
    String univName;
    TextView text1;
    TextView text2;
    TextView text3;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_information);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        universityTour = (UniversityTour) intent.getSerializableExtra("locationDesc");
        univName = intent.getStringExtra("univName");

        locname = (TextView) findViewById(R.id.locName);
        locimage = (ImageView) findViewById(R.id.locImage);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        button1 = findViewById(R.id.button1);

        text1.setText(universityTour.get기본_사항());
        text2.setText(universityTour.get주요학과());
        text3.setText(universityTour.get특징());

        locname.setText(universityTour.get시설());

        String imageUrl = "http://3.114.244.9/" + univName + "/" + universityTour.getId_num() + ".jpg";
        Glide.with(this).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(imageUrl).into(locimage);

        button1.setOnClickListener(button1ClickListener);
    }

    Button.OnClickListener button1ClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };
}

