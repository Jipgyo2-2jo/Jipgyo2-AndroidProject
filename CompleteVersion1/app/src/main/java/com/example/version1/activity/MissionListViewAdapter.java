package com.example.version1.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.version1.R;
import com.example.version1.domain.MissionQuiz;

import java.util.ArrayList;

public class MissionListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<MissionListViewItem> missionlistViewItemList = new ArrayList<MissionListViewItem>() ;

    // ListViewAdapter의 생성자
    public MissionListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return missionlistViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_missionlist, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView bulb = (ImageView) convertView.findViewById(R.id.bulb) ;
        TextView missionType = (TextView) convertView.findViewById(R.id.missionType) ;
        TextView correctness = (TextView) convertView.findViewById(R.id.correctness) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        MissionListViewItem missionListViewItem = missionlistViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        if(missionListViewItem.getImageType() == 1){
            bulb.setImageResource(R.drawable.bulbyes);//켜진 전구
        }else{
            bulb.setImageResource(R.drawable.bulbno);//꺼진 전구
        }
        missionType.setText(missionListViewItem.getMissionType());
        correctness.setText(missionListViewItem.getCorrectness());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return missionlistViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addMission(int bulb, String missionType, String correctness, MissionQuiz q) {
        MissionListViewItem item = new MissionListViewItem();

        item.setImageType(bulb);
        item.setMissionType(missionType);
        item.setCorrectness(correctness);
        item.setQ(q);
        missionlistViewItemList.add(0, item);
    }

    public void modifyMission(int correctNum, MissionQuiz q) {

        //q에 해당하는 미션 찾음. id를 이용한다.
        for(int i = 0; i < missionlistViewItemList.size(); i++){
            if(missionlistViewItemList.get(i).getQ().getId() == q.getId()){
                if(missionlistViewItemList.get(i).getQ().getQuizArrayList().size() == correctNum){
                    //모두 맞춘 경우 bulb를 이용하여 꺼짐으로 표시한다.
                    missionlistViewItemList.get(i).setImageType(0);
                }
                else
                    missionlistViewItemList.get(i).setImageType(1);
                //맞춘 개수도 업데이트해준다.
                missionlistViewItemList.get(i).setCorrectness(correctNum+"/"+missionlistViewItemList.get(i).getQ().getQuizArrayList().size());
            }
        }
    }

    public int getanswers() {
        int j = 0;
        for(int i = 0; i < missionlistViewItemList.size(); i++){
            if(missionlistViewItemList.get(i).getImageType() == 0){
               j++;
            }
        }
        return j;
    }
}
