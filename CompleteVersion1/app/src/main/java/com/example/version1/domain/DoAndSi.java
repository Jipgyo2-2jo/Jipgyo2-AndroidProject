package com.example.version1.domain;

import java.io.Serializable;

public class DoAndSi implements Serializable {
    String name;
    double latitude;
    double lonitude;
    int zoomlevel;
    int imgid;

    public DoAndSi(String name, double latitude, double lonitude, int zoomlevel, int imgid){
        this.name = name;
        this.latitude = latitude;
        this.lonitude = lonitude;
        this.zoomlevel = zoomlevel;
        this.imgid = imgid;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLonitude() {
        return lonitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLonitude(double lonitude) {
        this.lonitude = lonitude;
    }

    public int getZoomlevel() {
        return zoomlevel;
    }

    public void setZoomlevel(int zoomlevel) {
        this.zoomlevel = zoomlevel;
    }

    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }
}
