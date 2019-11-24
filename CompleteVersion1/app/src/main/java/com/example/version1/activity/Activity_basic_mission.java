package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.R;
import com.example.version1.domain.MissionQuiz;

public class Activity_basic_mission extends AppCompatActivity {

    //라디오버튼 그룹화하기 Arraylist<> 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_mission);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        MissionQuiz missionQuiz = (MissionQuiz) intent.getSerializableExtra("missionQuiz");

        Toast.makeText(this, "선택: " + missionQuiz.getQuizArrayList().get(0).getAnswers().size(), Toast.LENGTH_SHORT).show();

        //미션을 유동적으로 넣어준다.
        for(int i = 0; i < missionQuiz.getQuizArrayList().size(); i++){
            Sub n_layout = new Sub(getApplicationContext());
            LinearLayout con = (LinearLayout)findViewById(R.id.con);
            TextView ques = n_layout.findViewById(R.id.ques);
            ques.setText(missionQuiz.getQuizArrayList().get(i).getQuestion());
            con.addView(n_layout);

            for(int j = 0; j < missionQuiz.getQuizArrayList().get(i).getAnswers().size(); j++){
                Sub2 n_layout2 = new Sub2(getApplicationContext());
                RadioButton answertv = n_layout2.findViewById(R.id.answertv);
                answertv.setText(missionQuiz.getQuizArrayList().get(i).getAnswers().get(j));
                //answertv.isChecked();
                answertv.setChecked(true);
                con.addView(n_layout2);
            }
        }




    }

}
