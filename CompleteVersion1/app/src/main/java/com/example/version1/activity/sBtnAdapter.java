package com.example.version1.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.version1.R;

import java.util.ArrayList;

public class sBtnAdapter extends ArrayAdapter implements Filterable {
    private ArrayList<sBtnItem> listViewItemList = new ArrayList<>();
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<sBtnItem> filteredItemList = listViewItemList ;
    private Filter listFilter;

    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의.
    public interface ListBtnClickListener {
//        void onListBtnClick(int position) ;
    }

    // 생성자로부터 전달된 resource id 값을 저장.
    int resourceId ;
    // 생성자로부터 전달된 ListBtnClickListener  저장.
    private sBtnAdapter.ListBtnClickListener listBtnClickListener ;

    // ListViewBtnAdapter 생성자. 마지막에 ListBtnClickListener 추가.
    sBtnAdapter(Context context, int resource, ArrayList<sBtnItem> list, sBtnAdapter.ListBtnClickListener clickListener) {
        super(context, resource, list) ;

        // resource id 값 복사. (super로 전달된 resource를 참조할 방법이 없음.)
        this.resourceId = resource ;

        this.listBtnClickListener = clickListener ;
        listViewItemList = list;
    }

    // 새롭게 만든 Layout을 위한 View를 생성하는 코드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position ;
        final Context context = parent.getContext();

        // 생성자로부터 저장된 resourceId(listview_btn_item)에 해당하는 Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resourceId/*R.layout.listview_btn_item*/, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
//        final ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        final TextView Univname = (TextView) convertView.findViewById(R.id.univ_name);
        final TextView Univnum = (TextView) convertView.findViewById(R.id.univ_num);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        sBtnItem listViewItem = filteredItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
//        iconImageView.setImageDrawable(listViewItem.getIcon());
        Univname.setText(listViewItem.getUnivname());
        Univnum.setText(listViewItem.getUnivnum());

        // button1 클릭 시 TextView(textView1)의 내용 변경.
        Button button1 = (Button) convertView.findViewById(R.id.button1);
        button1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
/*                EatenMenu m = new EatenMenu();
                m.setCalorie(l.get(pos).getSpecificmenu().getCalorie());
                m.setName(l.get(pos).getSpecificmenu().getName());
                m.setRating(0f);
                m.setSpecificMenuID(l.get(pos).getSpecificmenu().getSpecificMenuID());
                m.setCount(Integer.valueOf(counteditText.getText().toString()));

                int remc = UserDailyInfo.getRemainingCalorie();
                UserDailyInfo.setRemainingCalorie(remc-(m.getCalorie()*m.getCount()));
                RecordDietActivity.eatenmenu.add(m);

             //   Intent intent = new Intent(this, )
                ((Activity)context).finish();*/
            }

        });

        return convertView;
    }

    @Override
    public Object getItem(int position){
        return filteredItemList.get(position);
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return filteredItemList.size() ;
    }

    @Override
    public Filter getFilter(){
        if(listFilter == null){
            listFilter = new ListFilter();
        }

        return listFilter;
    }

    private class ListFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0){
                results.values = listViewItemList;
                results.count = listViewItemList.size();
            }
            else{
                ArrayList<sBtnItem> itemList = new ArrayList<>();

                for(sBtnItem item : listViewItemList){
                    if(item.getUnivname().contains(constraint.toString()))
                        itemList.add(item);
                }

                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){

            // update listview by filtered data list.
            filteredItemList = (ArrayList<sBtnItem>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}
