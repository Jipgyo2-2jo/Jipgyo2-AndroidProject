package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.version1.R;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class Activity_tour_finish extends AppCompatActivity {
    private Button to_main;
    private Button to_tour;
    private Button to_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_finish);

        to_main = (Button)findViewById(R.id.to_main);
        to_tour = (Button)findViewById(R.id.to_tour);
        to_back = (Button)findViewById(R.id.to_back);

        Toast.makeText(this, "Tour Finish 실행", Toast.LENGTH_SHORT).show();
    }

    public void to_main_Button_Clicked(View V){
        Intent intent = new Intent(this, Activity_universitiesMap.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public void to_tour_Button_Clicked(View V){
        Intent intent = new Intent(this, Activity_eachUniversityMap.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public void to_back_Button_Clicked(View V){
        finish();
    }
}
