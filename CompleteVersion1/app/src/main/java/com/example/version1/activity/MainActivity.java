package com.example.version1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.R;
import com.example.version1.database.UniversitiesAccessDB;
import com.example.version1.database.UniversitiesAccessDBTask;
import com.example.version1.domain.DoAndSi;
import com.example.version1.domain.Universities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private TextView current_task;
    ArrayList<DoAndSi> doAndSiarray = new ArrayList<>();
    ArrayList<Universities> Universitiesarray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Log.d("MainActivity ", "실행");

//        current_task = (TextView) findViewById(R.id.current_task);
        doAndSiarray = setAndgetDoAndSiFromDB();

        UniversitiesAccessDB universitiesAccessDB = new UniversitiesAccessDB();
        Universitiesarray = universitiesAccessDB.getUniversitiesFromDB();

        Toast.makeText(getApplicationContext(), "Mainactivity 실행", Toast.LENGTH_LONG).show();

        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        Intent intent = new Intent(this, Activity_universitiesMap.class);

        intent.putExtra("Universitiesarray", Universitiesarray);
        intent.putExtra("doAndSiarray", doAndSiarray);

        Log.d("MainActivity ", "종료");
        startActivity(intent);

        finish();
    }

    //도, 시를 가져와서 일차적으로 이 것들만 가져오도록 한다.
    private ArrayList<DoAndSi> setAndgetDoAndSiFromDB() {
//        current_task.setText("시도정보 불러오는중...");
        Log.d("MainActivity ", "시도정보 불러오는중...");
        ArrayList<DoAndSi> doAndSiarray = new ArrayList<>();
        doAndSiarray.add(new DoAndSi("서울", 37.5642135, 127.0016985, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("경기남부", 37.290301, 127.095697, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("경기북부", 37.746260, 127.081964, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("인천", 37.516495, 126.715548, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("강원", 37.8304115, 128.2260705, 9, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("충북", 36.991615, 127.717028, 9, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("충남", 36.547203, 126.954132, 9, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("대전", 36.342518, 127.395548, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("전북", 35.594455, 127.170825, 9, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("전남", 34.929944, 127.001457, 9, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("광주", 35.145689, 126.839936, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("경북", 36.511197, 128.705964, 9, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("경남", 35.487832, 128.485218, 9, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("대구", 35.829030, 128.558030, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("울산", 35.540098, 129.296991, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("부산", 35.147905, 129.034805, 8, R.drawable.daegu));
        doAndSiarray.add(new DoAndSi("제주", 33.378994, 126.521648, 9, R.drawable.daegu));

        return doAndSiarray;
    }

    public void loadItemsFromDB(ArrayList<Universities> list) {
//        current_task.setText("대학교 정보 불러오는중...");
        Log.d("MainActivity ", "대학교 정보 불러오는중...");
        UniversitiesAccessDBTask t = new UniversitiesAccessDBTask();
        try
        {
            list = t.execute().get();
        }

        catch (InterruptedException e) {
            e.printStackTrace();

        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        //menu = menus.get(0);
        Log.d("list", list.get(0).get학교명());
        return;
    }

}
