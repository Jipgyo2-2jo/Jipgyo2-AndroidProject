package com.example.version1.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.version1.R;

import net.daum.mf.map.api.MapView;

public class Activity_photo_mission extends AppCompatActivity {

    String photoName;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_mission);

        Intent intent = getIntent();
        photoName = intent.getStringExtra("시설");
        id = intent.getIntExtra("id", 0);
        TextView Clear = findViewById(R.id.Clear);
        Clear.setText(photoName);

        Button backbutton = findViewById(R.id.button3);
        backbutton.setOnClickListener(clickListener);

        ImageView iv = findViewById(R.id.photo);
        String univName = intent.getStringExtra("univName");

        String imageUrl = "http://3.114.244.9/" + univName + "_photo/" + id + ".png";
        Glide.with(this).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).load(imageUrl).into(iv);
    }

    Button.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };
}
