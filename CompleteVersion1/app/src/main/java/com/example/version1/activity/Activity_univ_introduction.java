package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.version1.R;
import com.example.version1.database.UniversityIntroductionDB;
import com.example.version1.domain.UniversityIntroduction;

import java.util.ArrayList;

public class Activity_univ_introduction extends AppCompatActivity {

    Button button1;
    Button button2;
    String univName;
    TextView tv;
    ImageView iv;
    ArrayList<UniversityIntroduction> universityIntroductionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_univ_introduction);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        univName = intent.getStringExtra("univName");

        UniversityIntroductionDB universityIntroductionDB = new UniversityIntroductionDB();
        universityIntroductionArray = universityIntroductionDB.getUniversityIntroductionFromDB();

        tv = findViewById(R.id.clear);

        for(int i = 0; i < universityIntroductionArray.size(); i++){
            if(universityIntroductionArray.get(i).getUniv_name().equals(univName)){
                tv.setText(universityIntroductionArray.get(i).getExplain());
                break;
            }
        }

        //db에서 univName에 해당하는 정보를 불러온다.
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        tv = findViewById(R.id.clear);
        iv = findViewById(R.id.iv_image);

        String imageUrl = "http://3.114.244.9/"+univName+"/3.jpg";
        Glide.with(this).load(imageUrl).into(iv);

        button1.setOnClickListener(ClickListener1);
        button2.setOnClickListener(ClickListener2);

    }

    Button.OnClickListener ClickListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };

    Button.OnClickListener ClickListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Activity_univ_introduction.this, Activity_eachUniversityMap.class);
            //대학교 이름도 넘겨줌
            intent.putExtra("univName", univName);
            startActivityForResult(intent, 1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case 300:
                finish();
                break;
        }
    }
}
