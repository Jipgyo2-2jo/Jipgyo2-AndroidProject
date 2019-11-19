package com.example.version1.database;

import android.os.AsyncTask;

import com.example.version1.domain.Universities;

import java.util.ArrayList;

public class UniversitiesAccessDBTask extends AsyncTask<Integer, Void, ArrayList<Universities>> {
    @Override
    protected ArrayList<Universities> doInBackground(Integer... integers) {

        UniversitiesAccessDB client = new UniversitiesAccessDB();

        ArrayList<Universities> w = client.getUniversitiesFromDB();

        return w;
    }
}
