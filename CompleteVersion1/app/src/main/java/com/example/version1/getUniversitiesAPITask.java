package com.example.version1;

import android.os.AsyncTask;

import java.util.ArrayList;

import domain.Universities;

public class getUniversitiesAPITask extends AsyncTask<Integer, Void, ArrayList<Universities>> {

    @Override
    protected ArrayList<Universities> doInBackground(Integer... integers) {

        getUniversitiesAPI client = new getUniversitiesAPI();

        ArrayList<Universities> w = client.getJson();

        return w;
    }
}
