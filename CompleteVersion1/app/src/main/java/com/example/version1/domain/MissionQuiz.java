package com.example.version1.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class MissionQuiz implements Serializable {
    int id;//구분하는 id값
    int type = 0;//그냥 미션이면 0, 포토존 미션이면1
    //미션이 발생할 위치
    Double latitude;
    Double longitude;
    int activated = 0;//미션이 활성화 되었을 경우 1, 아니면 0
    ArrayList<Quiz> quizArrayList;
    int rightAnswer = 0;

    public MissionQuiz() {
    }

    public MissionQuiz(int id, Double latitude, Double longitude, int activated, ArrayList<Quiz> quizArrayList) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.activated = activated;
        this.quizArrayList = quizArrayList;
    }

    public int getIsActivated() {
        return activated;
    }

    public void setIsActivated(int activated) {
        this.activated = activated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<Quiz> getQuizArrayList() {
        return quizArrayList;
    }

    public void setQuizArrayList(ArrayList<Quiz> quizArrayList) {
        this.quizArrayList = quizArrayList;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

    public int getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(int rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getTypeName() {
        if (type == 1) {
            return "기초 미션";
        } else {
            return "심화 미션";
        }
    }
}
