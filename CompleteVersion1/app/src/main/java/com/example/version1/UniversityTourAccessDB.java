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

public class UniversityTourAccessDB extends AppCompatActivity {

    ArrayList<UniversityTour> universityTourarray = new ArrayList<>();

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
                final String urlStr = "http://3.114.244.9/test.php";

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

                UniversityTour universityTour = new UniversityTour();

                universityTour.setId_num(Object.getInt("id_num"));
                universityTour.setLatitude(Object.getDouble("위도"));
                universityTour.setLonitude(Object.getDouble("경도"));
                universityTour.set시설(Object.getString("시설"));
                universityTour.set기본_사항(Object.getString("기본_사항"));
                universityTour.set한줄평(Object.getString("한줄평"));

                universityTourarray.add(universityTour);
            }

        } catch (Exception ex) {
            Log.d("UniversityTourAccessDB", "예외 발생함 : " + ex.toString());
        }
    }

    public ArrayList<UniversityTour> getUniversityTourFromDB(String univName){
        //임시로 대학 이름을 test로 설정
        final String urlStr = "http://3.114.244.9/" + univName + ".php";

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

        return universityTourarray;
    }
}
