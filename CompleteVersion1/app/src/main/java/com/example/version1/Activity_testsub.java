package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_testsub extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testsub);

        Intent intent=new Intent(this.getIntent());
        String s=intent.getStringExtra("text");
        TextView textView=(TextView)findViewById(R.id.textview);
        textView.setText(s);
    }
}
