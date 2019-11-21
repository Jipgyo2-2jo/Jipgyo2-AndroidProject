package com.example.version1.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.version1.R;

public class Activity_loc_information extends AppCompatActivity {

    TextView locname;
    ImageView locimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_information);

        locname = (TextView) findViewById(R.id.locName);
        locimage = (ImageView) findViewById(R.id.locImage);

        //웹에서 이미지 불러오기로 가져온다.
        CustomListFragment customListFrgmt = (CustomListFragment) getSupportFragmentManager().findFragmentById(R.id.customlistfragment);
        customListFrgmt.addItem(ContextCompat.getDrawable(this, R.drawable.help2),
                "New Box", "New Account Box Black 36dp") ;
    }
}
