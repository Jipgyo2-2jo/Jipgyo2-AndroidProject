package com.example.version1.activity;

import android.graphics.drawable.Drawable;

import com.example.version1.domain.Universities;

public class sBtnItem {
    private Universities universities;

    public Universities getUniversities() {
        return universities;
    }

    public void setUniversities(Universities universities) {
        this.universities = universities;
    }

    private Drawable iconDrawable ;
    private String Univname, Univnum;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setUnivname(String text) {
        Univname = text ;
    }
    public void setUnivnum(String text) {
        Univnum = text ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getUnivname() {
        return this.Univname ;
    }
    public String getUnivnum() {
        return this.Univnum ;
    }
}
