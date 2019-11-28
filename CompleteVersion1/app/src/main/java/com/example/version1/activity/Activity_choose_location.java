package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.R;
import com.example.version1.database.UniversityTourAccessDB;

public class Activity_choose_location extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        Button button1=(Button)findViewById(R.id.to_tour);
        Button button2=(Button)findViewById(R.id.to_main);
        Button button3=(Button)findViewById(R.id.finish);
        final EditText editText=(EditText)findViewById(R.id.edittext);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_choose_location.this, Activity_universitiesMap.class);
                intent.putExtra("text",String.valueOf(editText.getText()));
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_choose_location.this, UniversityTourAccessDB.class);
                intent.putExtra("text",String.valueOf(editText.getText()));
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_choose_location.this, Activity_eachUniversityMap.class);
                intent.putExtra("text",String.valueOf(editText.getText()));
                startActivity(intent);
            }
        });
    }
}

