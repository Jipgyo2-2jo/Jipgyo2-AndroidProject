package com.example.version1.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable {
    String question;
    ArrayList<String> answers;
    int rightAnswerone;

    public Quiz(String question, ArrayList<String> answers, int rightAnswerone){
        this.question = question;
        this.answers = answers;
        this.rightAnswerone = rightAnswerone;
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

    public int getRightAnswerone() {
        return rightAnswerone;
    }

    public void setRightAnswerone(int rightAnswerone) {
        this.rightAnswerone = rightAnswerone;
    }
}
