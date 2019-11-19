package com.example.version1.activity;

import android.Manifest;

import android.annotation.TargetApi;
import android.app.AlertDialog;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

import android.transition.Slide;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;


import com.example.version1.R;
import com.example.version1.database.UniversitiesAccessDB;
import com.example.version1.domain.DoAndSi;
import com.example.version1.domain.Universities;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Activity_universitiesMap extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, sBtnAdapter.ListBtnClickListener {

    private static final String LOG_TAG = "Act_universitiesMap";
    ArrayList<DoAndSi> doAndSiarray = new ArrayList<>();
    ArrayList<Universities> Universitiesarray = new ArrayList<>();
    ArrayList<MapPOIItem> mapPOIItemsDoAndSi = new ArrayList<>();
    ArrayList<MapPOIItem> mapPOIItemsUniv = new ArrayList<>();

    int touchnum = 0;
    int zoomlevelevent = 1;//1이면 넓은 화면, 0이면 좁은 화면
    private MapView mMapView;
    private MapPOIItem mCustomMarker;
    private Button buttonschool;
    private Button buttonback;
    String selectedSchoolName;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    private RelativeLayout rela;
    //for 검색기능
    public MenuItem searchItem;

    // CalloutBalloonAdapter 인터페이스 구현
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override//디폴트 값
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.ic_launcher_foreground);
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText("Custom CalloutBalloon");
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    //검색부분 구현
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        Log.d("Activity_universities: ", "실행");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setQueryHint("학교명 검색");
            searchView.setOnQueryTextListener(queryTextListener);
            SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
            if(null!=searchManager){
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
            searchView.setIconifiedByDefault(true);
        }
        return true;
    }

    //검색 리스너
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public boolean onQueryTextSubmit(String query) {
            // TODO Auto-generated method stub

            createCustomMarkerSearched(mMapView, query);
            return false;
        }

        //검색시 실시간으로 바뀌게 하는 기능
        @Override
        public boolean onQueryTextChange(String newText) {
            // TODO Auto-generated method stub
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universitiesmap);

        Intent intent = getIntent();
        //MainActivity에서 시도정보와 학교정보 받아옴
        doAndSiarray = (ArrayList<DoAndSi>) intent.getSerializableExtra("doAndSiarray");
        Universitiesarray = (ArrayList<Universities>)intent.getSerializableExtra("Universitiesarray");

        mMapView = (MapView) findViewById(R.id.map_view);
        buttonschool = (Button) findViewById(R.id.buttonschool);
        buttonback = (Button) findViewById(R.id.buttonback);
        rela = (RelativeLayout) findViewById(R.id.relativelayout);

        buttonschool.setOnClickListener(buttonSchoolClickListener);
        buttonback.setOnClickListener(buttonbackClickListener);

        //기본 환경 설정
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCurrentLocationEventListener(this);
        // 중심점 변경 + 줌 레벨 변경
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(35.570, 128.150), 11, true);

        // 구현한 CalloutBalloonAdapter 등록
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        for(int i = 0; i < doAndSiarray.size(); i++){
            createCustomMarkerDoAndSi(mMapView, doAndSiarray.get(i));
        }

        for(int j = 0; j < Universitiesarray.size(); j++){
            Log.d("0123", "onCreate: "+j);
            createCustomMarkerUniversities(mMapView, Universitiesarray.get(j));
        }

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //검색버튼 눌렀을때
    private void createCustomMarkerSearched(MapView mapView, String name){
        int count = 0;
        ListView listview ;
        sBtnAdapter adapter;
        ArrayList<sBtnItem> list = new ArrayList<>();
        SlidingDrawer slidingDrawer;

        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingdrawer);
        Queue<Universities> searchedUniv = new LinkedList<Universities>();

        //검색버튼 누른경우 학교목록에서 검색
        for(int i = 0; i < Universitiesarray.size(); i++){
            if(Universitiesarray.get(i).get학교명().contains(name)) {
                searchedUniv.offer(Universitiesarray.get(i));
                count++;
            }
        }
        Toast.makeText(getApplicationContext(), "Searched: " + Integer.toString(count) + "개 학교", Toast.LENGTH_LONG).show();

        //없는경우
        if(count==0){
            Toast.makeText(getApplicationContext(), "Not Found: " + name, Toast.LENGTH_LONG).show();
            return;
        }
        //1개인경우
        else if (count == 1) {
            Intent intent = new Intent(Activity_universitiesMap.this, Activity_univ_introduction.class);
            intent.putExtra("univName", searchedUniv.poll().get학교명());
            //sharedpreference를 이용하여 view설정을 저장한 후에 remove를 하도록 해서 나중에 resume이 되더라도 설정값을 이용하여 다시 보여줄 수 있도록 한다.
            //혹은 그냥 하나의 맵 뷰에 모든 기능 넣음
            //어차피 학교에 대한 간단한 설명을 하고 다시 맵 뷰를 보여주기 때문에 메인화면을 resume할 때 맵 뷰 재생성만 해주면 될 것 같다.
            //rela.removeAllViewsInLayout();
            startActivity(intent);
        }
        //여러개인경우
        else{
            sBtnItem item;

            while(searchedUniv.isEmpty()==false){
                //listview 아이템 생성
                item = new sBtnItem();
                Universities temp = searchedUniv.poll();
                item.setUnivname(temp.get학교명());
                item.setUnivnum(temp.get전화번호());
                item.setUniversities(temp);
                list.add(item);
            }

            // Adapter 생성
            adapter = new sBtnAdapter(this, R.layout.slistview_button_item, list, this) ;

            // 리스트뷰 참조 및 Adapter달기
            listview = (ListView) findViewById(R.id.slistView);
            listview.setAdapter(adapter);
        }

        slidingDrawer.animateOpen();
    }

    private void createCustomMarkerDoAndSi(MapView mapView, DoAndSi doAndSi) {
        mCustomMarker = new MapPOIItem();
        mCustomMarker.setItemName(doAndSi.getName());
        mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(doAndSi.getLatitude(), doAndSi.getLonitude()));//맵 포인트
        mCustomMarker.setTag(doAndSi.getZoomlevel());
        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mCustomMarker.setCustomImageResourceId(R.drawable.custom_map_present);//이미지(png파일로 하자)
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);

        mapView.addPOIItem(mCustomMarker);
        mapPOIItemsDoAndSi.add(mCustomMarker);
    }

    private void createCustomMarkerUniversities(MapView mapView, Universities university) {
        mCustomMarker = new MapPOIItem();
        mCustomMarker.setItemName(university.get학교명());
        mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(university.getLatitude(), university.getLonitude()));//맵 포인트

        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mCustomMarker.setCustomImageResourceId(R.drawable.custom_map_present);//이미지(png파일로 하자)
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);

        mapPOIItemsUniv.add(mCustomMarker);
    }

//    private void showAll() {
//        int padding = 20;
//        float minZoomLevel = 7;
//        float maxZoomLevel = 10;
//        MapPointBounds bounds = new MapPointBounds(CUSTOM_MARKER_POINT, DEFAULT_MARKER_POINT);
//        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
//    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        //gps와 건물의 거리가 가까워지면 미션을 주는 처리, 미션 잠김 -> 활성화로 전환

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }
//-------------------------------------------------------------------------
    @Override//전국 지도 화면에서 예를 들어 경기도를 누르면 경기도를 카메라 확대를 하고 다른 마커들도 보이도록 한다.
    public void onPOIItemSelected(MapView mapView, final MapPOIItem mapPOIItem) {
        if (touchnum == 0){
            touchnum = 1;
            return;
        }
        else if (zoomlevelevent == 1) {
            Log.d(mapPOIItem.getItemName(), "onPOIItemSelected: ");
            mapView.removePOIItems(mapPOIItemsUniv.toArray(new MapPOIItem[mapPOIItemsUniv.size()]));

            for (int j = 0; j < Universitiesarray.size(); j++) {
                //시, 도가 일치하는 마커만 보여줌
                if (Universitiesarray.get(j).get시도().equals(mapPOIItem.getItemName())) {
                    Log.d("" + mapPOIItem.getItemName(), "add: " + j);
                    mapView.addPOIItem(mapPOIItemsUniv.get(j));
                }
            }
            zoomlevelevent = 0;
            buttonback.setVisibility(View.VISIBLE);
            mapView.removePOIItems(mapPOIItemsDoAndSi.toArray(new MapPOIItem[mapPOIItemsDoAndSi.size()]));

        }

        //카메라 확대
        mapView.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mapPOIItem.getMapPoint(), mapPOIItem.getTag())), 200, new CancelableCallback() {
            @Override
            public void onFinish() {
                Toast.makeText(getBaseContext(), "Animation to " + mapPOIItem.getItemName() + " complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }
        });

        touchnum = 0;
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Toast.makeText(this, "Clicked " + mapPOIItem.getItemName(), Toast.LENGTH_SHORT).show();
        selectedSchoolName = String.valueOf(mapPOIItem.getItemName());
        //카메라 확대
        mapView.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mapPOIItem.getMapPoint(), 3)), 200, new CancelableCallback() {
            @Override
            public void onFinish() {
            }
            @Override
            public void onCancel() {
            }
        });
        //버튼 생김
        buttonschool.setVisibility(View.VISIBLE);
    }

    Button.OnClickListener buttonSchoolClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent=new Intent(Activity_universitiesMap.this, Activity_univ_introduction.class);
            intent.putExtra("univName", selectedSchoolName);

            startActivity(intent);
        }
    };

    Button.OnClickListener buttonbackClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            buttonback.setVisibility(View.INVISIBLE);
            mMapView.removePOIItems(mapPOIItemsUniv.toArray(new MapPOIItem[mapPOIItemsUniv.size()]));
            mMapView.addPOIItems(mapPOIItemsDoAndSi.toArray(new MapPOIItem[mapPOIItemsDoAndSi.size()]));
            zoomlevelevent = 1;
            mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(MapPoint.mapPointWithGeoCoord(35.570, 128.150), 11)), 200, new CancelableCallback() {
                @Override
                public void onFinish() {
                }
                @Override
                public void onCancel() {
                }
            });
        }
    };

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
//--------------------------------------------------------------------------------
    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
        /*Log.d("01", "onMapViewZoomLevelChanged:" + zoomLevel);
        if(zoomLevel < 10 && zoomlevelevent == 0){
            Log.d("02", "onMapViewZoomLevelChanged:" + zoomLevel);
            //도, 시 마커들을 지우고
            mapView.removePOIItems(mapPOIItemsDoAndSi.toArray(new MapPOIItem[mapPOIItemsDoAndSi.size()]));
            //학교 마커들을 생성한다.
            mapView.addPOIItems(mapPOIItemsUniv.toArray(new MapPOIItem[mapPOIItemsUniv.size()]));
            Log.d("02", "onMapViewZoomLevelChanged:" + zoomLevel);
            zoomlevelevent = 1;
        }
        else if(zoomLevel > 9 && zoomlevelevent == 1){
            Log.d("03", "onMapViewZoomLevelChanged:" + zoomLevel);
            mapView.removePOIItems(mapPOIItemsUniv.toArray(new MapPOIItem[mapPOIItemsUniv.size()]));
            mapView.addPOIItems(mapPOIItemsDoAndSi.toArray(new MapPOIItem[mapPOIItemsDoAndSi.size()]));
            zoomlevelevent = 0;
        }*/
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    //----------------------------------------------------------------------------
    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }


    //ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                Log.d("MapRotation", Float.toString(mMapView.getMapRotationAngle()));
                mMapView.setMapRotationAngle(0, false);
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(Activity_universitiesMap.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(Activity_universitiesMap.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(Activity_universitiesMap.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음, 위치에 따른 맵 뷰 설정도 가능하다.
            Log.d("MapRotation", Float.toString(mMapView.getMapRotationAngle()));
            mMapView.setMapRotationAngle(0, false);
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(Activity_universitiesMap.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(Activity_universitiesMap.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Activity_universitiesMap.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Activity_universitiesMap.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_universitiesMap.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}