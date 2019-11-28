package com.example.version1.database;

import android.util.Log;

import com.example.version1.domain.UniversityTour;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UniversityTourAccessDB {

    ArrayList<UniversityTour> universityTourarray = new ArrayList<>();

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
                universityTour.setLongitude(Object.getDouble("경도"));
                universityTour.set시설(Object.getString("시설"));
                universityTour.set기본_사항(Object.getString("기본_사항"));
                universityTour.set한줄평(Object.getString("한줄평"));

                int loctype = 0;
                loctype = loctype + Object.getInt("식당") * 1;
                loctype = loctype + Object.getInt("매점") * 10;
                loctype = loctype + Object.getInt("카페") * 100;
                loctype = loctype + Object.getInt("기숙사") * 1000;
                universityTour.setLoctype(loctype);

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
