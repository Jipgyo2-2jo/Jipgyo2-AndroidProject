package com.example.version1.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class MissionQuiz implements Serializable {
    int id;//구분하는 id값
    int type;//기초 미션이면 1, 심화 미션이면 2
    //미션이 발생할 위치
    Double latitude;
    Double logitude;
    int activated = 0;//미션이 활성화 되었을 경우 1, 아니면 0
    ArrayList<Quiz> quizArrayList;
    int rightAnswer = 0;

    public MissionQuiz(int id, int type, Double latitude, Double logitude, int activated, ArrayList<Quiz> quizArrayList) {
        this.id = id;
        this.type = type;
        this.latitude = latitude;
        this.logitude = logitude;
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

    public Double getLogitude() {
        return logitude;
    }

    public void setLogitude(Double logitude) {
        this.logitude = logitude;
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
