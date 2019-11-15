package com.example.version1;

import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import domain.Universities;

public class UniversitiesAccessDB extends AppCompatActivity {

    ArrayList<Universities> Universitiesarray = new ArrayList<>();
    TextView textView;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universitytourdbtest);

        textView = findViewById(R.id.textView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String urlStr = "http://3.114.244.9/test2.php";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        request(urlStr);
                    }
                }).start();
            }
        });
    }

    public void request(String urlStr) {
        String output = "";
        try {
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                int resCode = conn.getResponseCode();//웹서버에 페이지 요청method
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    output +=line;
                }
                reader.close();
                conn.disconnect();
            }

            JSONObject jsonObject = new JSONObject(output);

            JSONArray Array = jsonObject.getJSONArray("start");

            for(int i=0;i<Array.length();i++){
                JSONObject Object = Array.getJSONObject(i);

                Universities universities = new Universities();

                universities.setId_num(Object.getString("id_num"));
                universities.set시도(Object.getString("시도"));
                universities.set학교명(Object.getString("학교명"));
                universities.set학교명영문(Object.getString("학교명영문"));
                universities.set본분교(Object.getString("본분교"));
                universities.set설립(Object.getString("설립"));
                universities.set주소(Object.getString("주소"));
                universities.set전화번호(Object.getString("전화번호"));
                universities.set홈페이지(Object.getString("홈페이지"));
                universities.setLatitude(Object.getDouble("위도"));
                universities.setLonitude(Object.getDouble("경도"));

                Universitiesarray.add(universities);
            }

        } catch (Exception ex) {
            Log.d("UniversitiesAccessDB","예외 발생함 : " + ex.toString());
        }
    }
    public ArrayList<Universities> getUniversitiesFromDB(){

        final String urlStr = "http://3.114.244.9/test2.php";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                request(urlStr);
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            // TODO: handle exception
        }

        return Universitiesarray;
    }

}

