package com.example.version1.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

public class CourseListFragment extends ListFragment {

    CourseListViewAdapter adapter ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Adapter 생성 및 Adapter 지정. (ListFragment에서 제공)
        adapter = new CourseListViewAdapter() ;
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        // get TextView's Text.
        CourseListViewItem item = (CourseListViewItem) l.getItemAtPosition(position) ;

        String courseName = item.getCourseName() ;
        String courseLength = item.getCourseLength() ;
        String courseTime = item.getCourseTime() ;

        Activity_eachUniversityMap activity = (Activity_eachUniversityMap) getActivity();
        activity.onCourseSelected(courseName, position);

        // TODO : use item data.
    }

    public void addItem(String courseName, String courseLength, String courseTime) {
        adapter.addItem(courseName, courseLength, courseTime) ;
    }
}
