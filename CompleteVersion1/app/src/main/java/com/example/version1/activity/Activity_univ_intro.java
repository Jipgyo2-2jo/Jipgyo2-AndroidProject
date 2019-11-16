package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.version1.R;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

public class Activity_univ_intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_univ_intro);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_univ_intro.this, Activity_eachUniversityMap.class);
                startActivity(intent);
            }
        });
    }
}
