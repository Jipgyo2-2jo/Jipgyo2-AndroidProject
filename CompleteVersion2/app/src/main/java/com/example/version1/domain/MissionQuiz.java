package com.example.version1.domain;

import java.util.ArrayList;

public class MissionQuiz {
    int id;
    int type;//기초 미션이면 1, 심화 미션이면 2
    String question;
    ArrayList<String> answers;
    //미션이 발생할 위치
    Double latitude;
    Double logitude;
    int activated = 0;//미션이 활성화 되었을 경우 1, 아니면 0

    public MissionQuiz(int id, int type, String question, ArrayList<String> answers, Double latitude, Double logitude) {
        this.id = id;
        this.type = type;
        this.question = question;
        this.answers = answers;
        this.latitude = latitude;
        this.logitude = logitude;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
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
}
