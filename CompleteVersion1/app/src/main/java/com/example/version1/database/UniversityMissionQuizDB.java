package com.example.version1.database;

import android.util.Log;

import com.example.version1.domain.MissionQuiz;
import com.example.version1.domain.Quiz;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UniversityMissionQuizDB {

    ArrayList<MissionQuiz> universityMissionQuizArray = new ArrayList<>();

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

                    output += line;
                }
                reader.close();
                conn.disconnect();
            }

            JSONObject jsonObject = new JSONObject(output);

            JSONArray Array = jsonObject.getJSONArray("start");

            ArrayList<Quiz> quizArrayList;
            int i;
            int j = 0;
            int tmpId = 0;
            for (i = j; i < Array.length(); i++) {
                JSONObject Object = Array.getJSONObject(i);

                //새로운 id인 경우 (db에서는 type)새로운 missionquiz를 생성한다.
                MissionQuiz missionQuiz = new MissionQuiz();
                missionQuiz.setId(Object.getInt("type"));
                tmpId = Object.getInt("type");
                missionQuiz.setLatitude(Object.getDouble("latitude"));
                missionQuiz.setLongitude(Object.getDouble("longitude"));
                quizArrayList = new ArrayList<>();

                //나머지 quiz를 넣어준다.
                ArrayList<String> stary = new ArrayList<>();//임시 string arraylist
                for (int k = 1; k < 6; k++) {
                    String tempStr;
                    tempStr = Object.getString(k + "");
                    if (!tempStr.equals("null")) {
                        stary.add(tempStr);
                    } else {
                        break;
                    }
                }
                Quiz quiz = new Quiz(Object.getString("퀴즈"), stary, Object.getInt("정답"));
                quizArrayList.add(quiz);

                //이미 들어간 id인 경우 같은 missionquiz에 새로운 quiz를 넣는다.
                for (j = i + 1; j < Array.length(); j++) {
                    Object = Array.getJSONObject(j);
                    if (tmpId == Object.getInt("type")) {
                        //나머지 quiz를 넣어준다.
                        stary = new ArrayList<>();
                        for (int k = 1; k < 6; k++) {
                            String tempStr;
                            tempStr = Object.getString(k + "");
                            if (!tempStr.equals("null")) {
                                stary.add(tempStr);
                            } else {
                                break;
                            }
                        }
                        quiz = new Quiz(Object.getString("퀴즈"), stary, Object.getInt("정답"));
                        quizArrayList.add(quiz);
                        i = j;
                    } else {
                        i = j - 1;
                        break;
                    }
                }
                missionQuiz.setQuizArrayList(quizArrayList);
                universityMissionQuizArray.add(missionQuiz);
            }
        } catch (Exception ex) {
            Log.d("MissionQuizDB", "예외 발생함 : " + ex.toString());
        }
    }

    public ArrayList<MissionQuiz> getUniversityMissionQuizArrayFromDB(String univName) {
        //임시로 대학 이름을 test로 설정
        final String urlStr = "http://3.114.244.9/" + univName + "_mission.php";

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

        return universityMissionQuizArray;
    }
}
