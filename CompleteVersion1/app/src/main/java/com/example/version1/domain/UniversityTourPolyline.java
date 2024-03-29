package com.example.version1.domain;

import java.util.ArrayList;

public class UniversityTourPolyline {

    String courseType;
    ArrayList<Double> latitudeArray;
    ArrayList<Double> logitudeArray;
    int missionNum = 0;
    ArrayList<Integer> missionsIDs;
    int courseTime = 0;
    Double courseLength = 0.0;

    public UniversityTourPolyline() {
        this.latitudeArray = new ArrayList<Double>();
        this.logitudeArray = new ArrayList<Double>();
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public ArrayList<Double> getLatitudeArray() {
        return latitudeArray;
    }

    public Double getLatitudeArraypoint(int i) {
        return latitudeArray.get(i);
    }

    public void setLatitudeArray(ArrayList<Double> latitudeArray) {
        this.latitudeArray = latitudeArray;
    }

    public ArrayList<Double> getLogitudeArray() {
        return logitudeArray;
    }

    public Double getLogitudeArraypoint(int i) {
        return logitudeArray.get(i);
    }

    public void setLogitudeArray(ArrayList<Double> logitudeArray) {
        this.logitudeArray = logitudeArray;
    }

    public void addLatitudeArray(Double latitude) {
        this.latitudeArray.add(latitude);
    }

    public void addLogitudeArray(Double longitude) {
        this.logitudeArray.add(longitude);
    }

    public int getMissionNum() {
        return missionNum;
    }

    public void setMissionNum(int missionNum) {
        this.missionNum = missionNum;
    }

    public ArrayList<Integer> getMissionsIDs() {
        return missionsIDs;
    }

    public void setMissionsIDs(ArrayList<Integer> missionsIDs) {
        this.missionsIDs = missionsIDs;
    }

    public int getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(int courseTime) {
        this.courseTime = courseTime;
    }

    public Double getCourseLength() {
        return courseLength;
    }

    public void setCourseLength(Double courseLength) {
        this.courseLength = courseLength;
    }
}
