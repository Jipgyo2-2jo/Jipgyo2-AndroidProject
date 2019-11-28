package com.example.version1.domain;

public class UniversityTour {
    private int id_num;
    private Double latitude;
    private Double longitude;
    private String 시설;
    private String 기본_사항;
    private String 한줄평;
    private int loctype;

    public int getId_num() {
        return id_num;
    }

    public void setId_num(int id_num) {
        this.id_num = id_num;
    }

    public String get시설() {
        return 시설;
    }

    public void set시설(String 시설) {
        this.시설 = 시설;
    }

    public String get기본_사항() {
        return 기본_사항;
    }

    public void set기본_사항(String 기본_사항) {
        this.기본_사항 = 기본_사항;
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

    public String get한줄평() {
        return 한줄평;
    }

    public void set한줄평(String 한줄평) {
        this.한줄평 = 한줄평;
    }

    public int getLoctype() {
        return loctype;
    }

    public void setLoctype(int loctype) {
        this.loctype = loctype;
    }
}
