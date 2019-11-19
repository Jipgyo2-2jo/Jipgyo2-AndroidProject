package com.example.version1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import domain.Universities;

public class Activity_choose_school extends AppCompatActivity implements sBtnAdapter.ListBtnClickListener {
    ArrayList<Universities> Universitiesarray = new ArrayList<>();
    TextView textView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_school);

        final String urlStr = "http://3.114.244.9/test2.php";

        ListView listview ;
        sBtnAdapter adapter;
        ArrayList<sBtnItem> items = new ArrayList<>();

        // items 로드.
        loadItemsFromDB(items) ;

        // Adapter 생성
        adapter = new sBtnAdapter(this, R.layout.slistview_button_item, items, this) ;
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.slistView);
        listview.setAdapter(adapter);
    }


    public boolean loadItemsFromDB(ArrayList<sBtnItem> list) {
        ArrayList<Universities> universities = new ArrayList<>();
        getUniversitiesAPITask t = new getUniversitiesAPITask();
        try
        {
            universities = t.execute().get();
        }

        catch (InterruptedException e) {
            e.printStackTrace();

        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        //menu = menus.get(0);
        Log.d("universities", universities.get(0).get학교명());

        sBtnItem item;

        if (list == null) {
            list = new ArrayList<sBtnItem>() ;
        }

        for(int i = 0; i<universities.size(); i++) {
            // 아이템 생성.
            item = new sBtnItem();
            item.setText(universities.get(i).get학교명() + "  전화번호: " + universities.get(i).get전화번호());
            item.setUniversities(universities.get(i));
            list.add(item);
        }

        return true ;
    }

}