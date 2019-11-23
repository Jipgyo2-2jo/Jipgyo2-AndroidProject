package com.example.version1.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable {
    String question;
    ArrayList<String> answers;

    public Quiz(String question, ArrayList<String> answers) {
        this.question = question;
        this.answers = answers;
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
}
