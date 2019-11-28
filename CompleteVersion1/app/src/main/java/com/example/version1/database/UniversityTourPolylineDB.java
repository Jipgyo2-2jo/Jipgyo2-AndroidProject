package com.example.version1.database;

import android.util.Log;

import com.example.version1.domain.UniversityTourPolyline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UniversityTourPolylineDB {

    ArrayList<UniversityTourPolyline> universityTourPolylineArray = new ArrayList<>();

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

            String tempString1;
            String tempString2;
            ArrayList<Integer> integerArrayList;
            for(int i=0;i<Array.length();i++){
                JSONObject Object = Array.getJSONObject(i);

                UniversityTourPolyline universityTourPolyline = new UniversityTourPolyline();
                universityTourPolyline.setCourseType(Object.getString("course"));
                universityTourPolyline.setMissionNum(Object.getInt("mission_num"));
                universityTourPolyline.setCourseTime(Object.getInt("time"));
                universityTourPolyline.setCourseLength(Object.getDouble("distance"));

                for (int j = 0; j < 110; j = j+2) {//100은 maxsize
                    //null이 나올 때 까지 반복해서 읽음
                    tempString1 = Object.getString(j + "");
                    tempString2 = Object.getString(j + 1 + "");

                    if (!tempString1.equals("null")) {
                        //짝수는 위도 홀수는 경도 set
                        universityTourPolyline.addLatitudeArray(Double.valueOf(tempString1));
                        universityTourPolyline.addLogitudeArray(Double.valueOf(tempString2));
                    }
                    else {
                        break;
                    }
                }

                integerArrayList = new ArrayList<>();
                for (int k = 0; k < 20; k++) {//20은 maxsize
                    //null이 나올 때 까지 반복해서 읽음
                    tempString1 = Object.getString("mission" + k);

                    if (!tempString1.equals("0")) {
                        //짝수는 위도 홀수는 경도 set
                        integerArrayList.add(Integer.valueOf(tempString1));
                    }
                    else {
                        break;
                    }
                }
                universityTourPolyline.setMissionsIDs(integerArrayList);
                universityTourPolylineArray.add(universityTourPolyline);
            }

        } catch (Exception ex) {
            Log.d("UnivTourPolylineDB", "예외 발생함 : " + ex.toString());
        }
    }

    public ArrayList<UniversityTourPolyline> getUniversityTourPolylineFromDB(String univName){
        //임시로 대학 이름을 test로 설정
        final String urlStr = "http://3.114.244.9/" + univName + "_course.php";

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

        return universityTourPolylineArray;
    }
}
