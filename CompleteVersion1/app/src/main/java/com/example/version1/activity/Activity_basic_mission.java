package com.example.version1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.R;
import com.example.version1.domain.MissionQuiz;

import java.util.ArrayList;

public class Activity_basic_mission extends AppCompatActivity {

    MissionQuiz missionQuiz;
    private ArrayList<RadioGroup> rgArray;
    private ArrayList<LinearLayout> lloutArray;
    Button ansbutton;
    Button backbutton;
    int correctNum = 0;
    String missionName;
    int ansnumbers = 0;
    int ansnumbers1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_mission);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        missionQuiz = (MissionQuiz) intent.getSerializableExtra("missionQuiz");
        missionName = (String) intent.getStringExtra("missionName");

        TextView textView = findViewById(R.id.Clear);
        textView.setText(missionName);

        rgArray = new ArrayList<>();
        lloutArray = new ArrayList<>();
        //미션을 유동적으로 넣어준다.

        for(int i = 0; i < missionQuiz.getQuizArrayList().size(); i++) {
            Sub n_layout = new Sub(getApplicationContext());
            LinearLayout con = (LinearLayout) findViewById(R.id.con);
            TextView ques = n_layout.findViewById(R.id.ques);
            LinearLayout llout = n_layout.findViewById(R.id.anslayout);
            con.setDividerPadding(1000);
            lloutArray.add(llout);
            RadioGroup radioGroup = n_layout.findViewById(R.id.radiogroup);
            ques.setText(missionQuiz.getQuizArrayList().get(i).getQuestion());

            for (int j = 0; j < missionQuiz.getQuizArrayList().get(i).getAnswers().size(); j++) {
                RadioButton answertv = new RadioButton(this);
                answertv.setText(missionQuiz.getQuizArrayList().get(i).getAnswers().get(j));
                answertv.setTextColor(Color.rgb(139,134,135));
                answertv.setId(j+1);
                answertv.setChecked(false);
                radioGroup.addView(answertv);
            }
            con.addView(n_layout);
            rgArray.add(radioGroup);
        }

        ansbutton = findViewById(R.id.ansbutton);
        ansbutton.setOnClickListener(clickListener);
        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(clickListener2);

        //***********************************************************
        SharedPreferences sf = getSharedPreferences(missionName+"File",MODE_PRIVATE);
        ansnumbers = sf.getInt("ansnumbers", 0);
        ansnumbers1 = ansnumbers;
//        Toast.makeText(this, "정답 배열: "+ansnumbers, Toast.LENGTH_SHORT).show();
        //정답 여부를 표시한다.
        for (int i = 0; i < lloutArray.size(); i++) {
            rgArray.get(i).check(ansnumbers % 10);
            ansnumbers = ansnumbers / 10;
        }
        if(ansnumbers1 != 0){
            for(int i = 0; i < lloutArray.size(); i++) {
                //정답인경우
                if(rgArray.get(i).getCheckedRadioButtonId() == missionQuiz.getQuizArrayList().get(i).getRightAnswerone()){
                    lloutArray.get(i).setBackgroundResource(R.drawable.circle);
                    correctNum++;
                }
                else{
                    lloutArray.get(i).setBackgroundResource(R.drawable.xmark);
                }
            }
            ansnumbers = ansnumbers1;
        }
        //***********************************************************
    }

    //정답 여부를 표시한다.
    Button.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            correctNum = 0;
            ansnumbers = 0;
            for(int i = 0; i < lloutArray.size(); i++) {
                ansnumbers = ansnumbers + rgArray.get(i).getCheckedRadioButtonId()*(int)Math.pow(10,i);
                //정답인경우
                if(rgArray.get(i).getCheckedRadioButtonId() == missionQuiz.getQuizArrayList().get(i).getRightAnswerone()){
                    lloutArray.get(i).setBackgroundResource(R.drawable.circle);
                    correctNum++;
                }
                else{
                    lloutArray.get(i).setBackgroundResource(R.drawable.xmark);
                }
            }
        }
    };

    //지도로 돌아간다.
    Button.OnClickListener clickListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("correctNum", correctNum);
            intent.putExtra("missionQuiz", missionQuiz);
            correctNum = 0;
            setResult(100, intent);
            finish();
            //맞춘 정답 수를 넘기고 finish
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences(missionName+"File", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ansnumbers1 = ansnumbers;
        editor.putInt("ansnumbers",ansnumbers1);

        editor.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("correctNum", correctNum);
        intent.putExtra("missionQuiz", missionQuiz);
        Log.d("112211", "onBackPressed:"+correctNum);
        correctNum = 0;
        setResult(100, intent);
        finish();
        //맞춘 정답 수를 넘기고 finish
    }
}


