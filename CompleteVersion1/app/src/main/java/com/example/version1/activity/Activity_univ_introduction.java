package com.example.version1.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

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
    TextView name, date, ideology, explain;
    ImageView iv;
    Toolbar toolbar;
    ArrayList<UniversityIntroduction> universityIntroductionArray;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_univ_introduction);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        univName = intent.getStringExtra("univName");

        UniversityIntroductionDB universityIntroductionDB = new UniversityIntroductionDB();
        universityIntroductionArray = universityIntroductionDB.getUniversityIntroductionFromDB();

        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        ideology = findViewById(R.id.ideology);
        explain = findViewById(R.id.explain);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        iv = findViewById(R.id.iv_image);

        Log.d("DB","찾기 전");
        for(int i = 0; i < universityIntroductionArray.size(); i++){
            if(universityIntroductionArray.get(i).getUniv_name().contains(univName)){
                Log.d("DB","Found");
                name.setText("이름: " +universityIntroductionArray.get(i).getUniv_name());
                date.setText("설립일: " +universityIntroductionArray.get(i).getDate());
                ideology.setText("이념: " +universityIntroductionArray.get(i).getIdeology());
                explain.setText("설명: " +universityIntroductionArray.get(i).getExplain());
                break;
            }
        }

        //db에서 univName에 해당하는 정보를 불러온다.
        String imageUrl = "http://3.114.244.9/"+univName+"/3.jpg";
        Glide.with(this).load(imageUrl).into(iv);

        button1.setOnClickListener(ClickListener1);
        button2.setOnClickListener(ClickListener2);

        toolbar = findViewById(R.id.app_toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(univName);
        title.setTextSize(28);
    }

    Button.OnClickListener ClickListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
            overridePendingTransition(R.anim.anim_slide_from_left, R.anim.anim_slide_to_right);
        }
    };

    Button.OnClickListener ClickListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Activity_univ_introduction.this, Activity_eachUniversityMap.class);
            //대학교 이름도 넘겨줌
            intent.putExtra("univName", univName);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.anim_slide_from_right, R.anim.anim_slide_to_left);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case 300:
                finish();
                overridePendingTransition(R.anim.anim_slide_from_left, R.anim.anim_slide_to_right);
                break;
        }
    }
}
