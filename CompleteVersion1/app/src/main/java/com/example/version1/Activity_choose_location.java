package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_choose_location extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        Button button1=(Button)findViewById(R.id.button1);
        Button button2=(Button)findViewById(R.id.button2);
        Button button3=(Button)findViewById(R.id.button3);
        final EditText editText=(EditText)findViewById(R.id.edittext);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_choose_location.this, com.example.version1.Activity_universitiesMap.class);
                intent.putExtra("text",String.valueOf(editText.getText()));
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_choose_location.this, com.example.version1.UniversityTourAccessDB.class);
                intent.putExtra("text",String.valueOf(editText.getText()));
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_choose_location.this, com.example.version1.Activity_eachUniversityMap.class);
                intent.putExtra("text",String.valueOf(editText.getText()));
                startActivity(intent);
            }
        });
    }
}

