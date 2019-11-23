package com.example.version1.activity;

import com.example.version1.domain.MissionQuiz;

public class MissionListViewItem {
    int imageType = 1; //1일 때 켜진 전구, 0일 때 꺼진 전구
    String missionType;
    String correctness;
    MissionQuiz q;

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }

    public String getCorrectness() {
        return correctness;
    }

    public void setCorrectness(String correctness) {
        this.correctness = correctness;
    }

    public MissionQuiz getQ() {
        return q;
    }

    public void setQ(MissionQuiz q) {
        this.q = q;
    }
}
