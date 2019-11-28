package com.example.version1.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

import com.example.version1.domain.MissionQuiz;

public class MissionListFragment extends ListFragment{
    MissionListViewAdapter adapter ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Adapter 생성 및 Adapter 지정. (ListFragment에서 제공)
        adapter = new MissionListViewAdapter() ;
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        // get TextView's Text.
        MissionListViewItem item = (MissionListViewItem) l.getItemAtPosition(position) ;

        int bulb = item.getImageType();
        String missionType = item.getMissionType() ;
        String correctness = item.getCorrectness() ;
        MissionQuiz q = item.getQ();

        // MissionQuiz를 이용하여 intent를 이용하여 mission을 띄울 수 있도록 한다.
        Activity_eachUniversityMap activity = (Activity_eachUniversityMap) getActivity();
        activity.onMissionSelected(q, missionType);
    }

    public void addMission(int bulb, String missionType, String correctness, MissionQuiz q) {
        adapter.addMission(bulb, missionType, correctness, q) ;
    }

    public void modifyMission(int correctNum, MissionQuiz q){
        adapter.modifyMission(correctNum, q);
    }


    public int getanswers() {
        return adapter.getanswers();
    }
}
