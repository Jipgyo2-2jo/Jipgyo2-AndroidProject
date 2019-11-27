package com.example.version1.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.R;
import com.example.version1.domain.MissionQuiz;

import java.util.ArrayList;

public class Activity_basic_mission extends AppCompatActivity {

    MissionQuiz missionQuiz;
    private ArrayList<RadioGroup> rgArray;
    private ArrayList<TextView> ansArray;
    Button ansbutton;
    Button backbutton;

    int correctNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_mission);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        missionQuiz = (MissionQuiz) intent.getSerializableExtra("missionQuiz");

        rgArray = new ArrayList<>();
        ansArray = new ArrayList<>();
        //미션을 유동적으로 넣어준다.
        for(int i = 0; i < missionQuiz.getQuizArrayList().size(); i++) {
            Sub n_layout = new Sub(getApplicationContext());
            LinearLayout con = (LinearLayout) findViewById(R.id.con);
            TextView ques = n_layout.findViewById(R.id.ques);

            ques.setTextColor(Color.WHITE);
            TextView ans = n_layout.findViewById(R.id.ans);
            ans.setTextColor(Color.WHITE);
            ans.setId(i);
            ansArray.add(ans);
            RadioGroup radioGroup = n_layout.findViewById(R.id.radiogroup);
            ques.setText(missionQuiz.getQuizArrayList().get(i).getQuestion());

            for (int j = 0; j < missionQuiz.getQuizArrayList().get(i).getAnswers().size(); j++) {
                RadioButton answertv = new RadioButton(this);
                answertv.setText(missionQuiz.getQuizArrayList().get(i).getAnswers().get(j));
                answertv.setTextColor(Color.WHITE);
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
    }

    //정답 여부를 표시한다.
    Button.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            for(int i = 0; i < ansArray.size(); i++) {
                //정답인경우
                if(rgArray.get(i).getCheckedRadioButtonId() == missionQuiz.getQuizArrayList().get(i).getRightAnswerone()){
                    ansArray.get(i).setVisibility(View.VISIBLE);
                    ansArray.get(i).setText("정답!");

                    correctNum++;

                }
                else{
                    ansArray.get(i).setVisibility(View.VISIBLE);
                    ansArray.get(i).setText("오답!");
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

}
