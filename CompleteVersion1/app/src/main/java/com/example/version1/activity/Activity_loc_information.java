package com.example.version1.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;

import android.view.View;
import android.widget.ImageView;

import com.example.version1.R;

public class Activity_loc_information extends AppCompatActivity {

    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImage = findViewById(R.id.iv_image);

        // Glide로 이미지 표시하기
        String imageUrl = "http://3.114.244.9/아주대학교/" + 2 + ".jpg";
        Glide.with(this).load(imageUrl).into(ivImage);
    }
}
