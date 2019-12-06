package com.example.version1.database;

import android.util.Log;

import com.example.version1.domain.UniversityIntroduction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UniversityIntroductionDB {
    ArrayList<UniversityIntroduction> universityIntroductionarray = new ArrayList<>();

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

                UniversityIntroduction universityIntroduction = new UniversityIntroduction();

                universityIntroduction.setUniv_name(Object.getString("name"));
                universityIntroduction.setIdeology(Object.getString("이념"));
                universityIntroduction.setDate(Object.getString("년도"));
                universityIntroduction.setExplain(Object.getString("설명"));

                universityIntroductionarray.add(universityIntroduction);
            }

        } catch (Exception ex) {
            Log.d("UnivIntroAccessDB", "예외 발생함 : " + ex.toString());
        }
    }

    public ArrayList<UniversityIntroduction> getUniversityIntroductionFromDB(){
        //임시로 대학 이름을 test로 설정
        final String urlStr = "http://3.114.244.9/school_explain.php";

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

        return universityIntroductionarray;
    }
}
