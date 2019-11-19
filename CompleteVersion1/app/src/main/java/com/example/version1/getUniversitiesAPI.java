package com.example.version1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import domain.Universities;

public class getUniversitiesAPI {
    final static String openURL = "http://3.114.244.9/test2.php";
    ArrayList<Universities> universities = new ArrayList<>();


    public ArrayList<Universities> getJson() {

        try {
            URL url = new URL(openURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String result = "";
            result = getStringFromInputStream(in);
            Log.d("resultTest", result);
            parsing(result);

            urlConnection.disconnect();
            in.close();

        } catch (MalformedURLException e) {

            System.err.println("Malformed URL");

            e.printStackTrace();
            return null;

        } catch (JSONException e) {

            System.err.println("JSON parsing error");

            e.printStackTrace();

            return null;

        } catch (IOException e) {

            System.err.println("URL Connection failed");

            e.printStackTrace();

            return null;

        }

        return universities;
    }

    private void parsing(String result) throws JSONException {

        JSONArray jarray1 = new JSONObject(result).getJSONArray("start");

        Log.d("jarray", jarray1.toString());

        int size = jarray1.length();
        for (int i = 0; i < size; i++) {
            JSONObject JObject = null;
            JObject = jarray1.getJSONObject(i);

            //   {
            //            "id_num": "0002",
            //            "학교종류": "대학교",
            //            "시도": "강원",
            //            "학교명": "강릉원주대학교",
            //            "학교명영문": "Gangneung-Wonju National University",
            //            "본분교": "제2캠퍼스",
            //            "학교상태": "기존",
            //            "설립": "국립",
            //            "주소": "강원도 원주시 흥업면 남원로 150 (흥업리, 강릉원주대학교원주캠퍼스)",
            //            "전화번호": "033-760-8114",
            //            "홈페이지": "www.gwnu.ac.kr",
            //            "경도": "127.922635300000000000000000000000",
            //            "위도": "37.304696700000000000000000000000"
            //        }
            String id_num = JObject.getString("id_num");
            String 시도 = JObject.getString("시도");
            String 학교명 = JObject.getString("학교명");
            String 학교명영문 = JObject.getString("학교명영문");
            String 본분교 = JObject.getString("본분교");
            String 설립 = JObject.getString("설립");
            String 주소 = JObject.getString("주소");
            String 전화번호 = JObject.getString("전화번호");
            String 홈페이지 = JObject.getString("홈페이지");
            double latitude = JObject.getDouble("위도");
            double lonitude = JObject.getDouble("경도");

            Universities university = new Universities(id_num, 시도, 학교명, 학교명영문, 본분교, 설립, 주소, 전화번호, 홈페이지, latitude, lonitude);
            universities.add(university);
        }

    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}


