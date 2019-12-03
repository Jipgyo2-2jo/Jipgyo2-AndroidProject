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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.version1.R;
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

public class Activity_universitiesMap extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener{

    private static final String LOG_TAG = "Act_universitiesMap";
    ArrayList<DoAndSi> doAndSiarray = new ArrayList<>();
    ArrayList<Universities> Universitiesarray = new ArrayList<>();
    ArrayList<MapPOIItem> mapPOIItemsDoAndSi = new ArrayList<>();
    ArrayList<MapPOIItem> mapPOIItemsUniv = new ArrayList<>();

    ArrayList<MapPOIItem> currentPOIs = new ArrayList<>();

    private MapView mMapView;
    private MapPOIItem mCustomMarker;
    private Button buttonschool;
    private Button buttonback;
    String selectedSchoolName;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int COME_BACK_TO_MAIN = 1001;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    private RelativeLayout rela;

    //for 검색기능
    private MenuItem searchItem;
    private SlidingDrawer slidingDrawer;
    private ListView listview ;
    Queue<Universities> searchedUniv = new LinkedList<Universities>();
    sBtnAdapter adapter;
    private boolean cameback = false;

    //다른 액티비티에서 돌아왔을때 현재 상태 저장
    private MapPOIItem recent_location;

    // CalloutBalloonAdapter 인터페이스 구현
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override//디폴트 값
        public View getCalloutBalloon(MapPOIItem poiItem) {
            mCalloutBalloon.findViewById(R.id.ballonlayout).setBackgroundResource(R.drawable.ballon);
            if(poiItem.getUserObject().getClass().equals(DoAndSi.class))
                return null;
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
            Universities a;
            a = (Universities) poiItem.getUserObject();
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText(a.get학교명영문());
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            mCalloutBalloon.findViewById(R.id.ballonlayout).setBackgroundResource(R.drawable.icon2);
            return null;
        }
    }

    @Override
    //검색부분 구현
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        Log.d("Activity_universities", "실행");
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
            slidingDrawer.open();
            ((sBtnAdapter)listview.getAdapter()).getFilter().filter(newText);

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_universitiesmap);

        Intent intent = getIntent();
        //MainActivity에서 시도정보와 학교정보 받아옴
        doAndSiarray = (ArrayList<DoAndSi>) intent.getSerializableExtra("doAndSiarray");
        Universitiesarray = (ArrayList<Universities>) intent.getSerializableExtra("Universitiesarray");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.d("onStart", "시작");
        setContentView(R.layout.activity_universitiesmap);
        ArrayList<sBtnItem> list = new ArrayList<>();

        mMapView = (MapView) findViewById(R.id.map_view);
        buttonschool = (Button) findViewById(R.id.buttonschool);
        buttonback = (Button) findViewById(R.id.buttonback);
        rela = (RelativeLayout) findViewById(R.id.relativelayout);

        buttonschool.setOnClickListener(buttonSchoolClickListener);
        buttonback.setOnClickListener(buttonbackClickListener);

        //기본 환경 설정
        mMapView.setMapType(MapView.MapType.Satellite);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        // 중심점 변경 + 줌 레벨 변경
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36, 127.80), 11, false);

        slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
        listview = (ListView) findViewById(R.id.slistView);

        Log.d("recent location", "null");
        for (int i = 0; i < doAndSiarray.size(); i++) {
            createCustomMarkerDoAndSi(mMapView, doAndSiarray.get(i));
        }
        // 구현한 CalloutBalloonAdapter 등록
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        for (int j = 0; j < Universitiesarray.size(); j++) {
            Log.d("0123", "onCreate: " + j);
            createCustomMarkerUniversities(mMapView, Universitiesarray.get(j));
        }

        if(cameback){
            moveToPOIItem(mMapView, recent_location);
            cameback = false;
        }

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }

        sBtnItem item;

        for (int i = 0; i < Universitiesarray.size(); i++) {
            //listview 아이템 생성
            item = new sBtnItem();
            Universities temp = Universitiesarray.get(i);
            item.setUnivname(temp.get학교명());
            item.setUnivnum(temp.get전화번호());
            item.setUniversities(temp);
            list.add(item);
        }

        // Adapter 생성
        adapter = new sBtnAdapter(this, R.layout.slistview_button_item, list);
        // 리스트뷰 참조 및 Adapter달기
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Item", i + "Clicked");
                for (final MapPOIItem item : mapPOIItemsUniv) {
                    if (item.getUserObject().equals(((sBtnItem) listview.getAdapter().getItem(i)).getUniversities())) {
                        moveToPOIItem(mMapView, item);

                        slidingDrawer.close();
                        return;
                    }
                }
            }
        });

    }

    //sharedreference를 이용하여 다시 구현하자.
/*    @Override
    protected void onRestart() {
        super.onRestart();
        ArrayList<sBtnItem> list = new ArrayList<>();
        setContentView(R.layout.activity_universitiesmap);
        mMapView = (MapView) findViewById(R.id.map_view);
        //기본 환경 설정
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCurrentLocationEventListener(this);
        // 중심점 변경 + 줌 레벨 변경
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(35.570, 128.150), 11, true);

        mMapView.addPOIItems(mapPOIItemsDoAndSi.toArray(new MapPOIItem[mapPOIItemsDoAndSi.size()]));
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        buttonschool = (Button) findViewById(R.id.buttonschool);
        buttonback = (Button) findViewById(R.id.buttonback);
        rela = (RelativeLayout) findViewById(R.id.relativelayout);
        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingdrawer);
        listview = (ListView)findViewById(R.id.slistView);

        buttonschool.setOnClickListener(buttonSchoolClickListener);
        buttonback.setOnClickListener(buttonbackClickListener);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        sBtnItem item;

        for(int i = 0; i < Universitiesarray.size(); i++){
            //listview 아이템 생성
            item = new sBtnItem();
            Universities temp = Universitiesarray.get(i);
            item.setUnivname(temp.get학교명());
            item.setUnivnum(temp.get전화번호());
            item.setUniversities(temp);
            list.add(item);
        }

        // Adapter 생성
        adapter = new sBtnAdapter(this, R.layout.slistview_button_item, list) ;
        // 리스트뷰 참조 및 Adapter달기
        listview.setAdapter(adapter);
    }*/

    @Override
    public void onResume(){
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }

    //검색버튼 눌렀을때
    private void createCustomMarkerSearched(MapView mapView, String name){
        int count = 0;

        for(Universities u : Universitiesarray){
            if(u.get학교명().contains(name)){
                count++;
                searchedUniv.add(u);
            }
        }

        //없는경우
        if(count==0){
            Toast.makeText(getApplicationContext(), "Not Found: " + name, Toast.LENGTH_LONG).show();
            return;
        }

        //1개인경우
        else {
            Toast.makeText(getApplicationContext(), count + "개 대학", Toast.LENGTH_LONG).show();
            Universities searched = searchedUniv.poll();
            for (final MapPOIItem item : mapPOIItemsUniv) {
                if (item.getUserObject().equals(searched)) {
                    moveToPOIItem(mapView, item);
                    return;
                }
            }
        }
    }

    private void createCustomMarkerDoAndSi(MapView mapView, DoAndSi doAndSi) {
        mCustomMarker = new MapPOIItem();
        mCustomMarker.setItemName(doAndSi.getName());
        mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(doAndSi.getLatitude(), doAndSi.getLonitude()));//맵 포인트
        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mCustomMarker.setCustomImageResourceId(doAndSi.getImgid());//이미지(png파일로 하자)
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);
        mCustomMarker.setShowCalloutBalloonOnTouch(false);
        mCustomMarker.setUserObject(doAndSi);
        mapView.addPOIItem(mCustomMarker);
        mapPOIItemsDoAndSi.add(mCustomMarker);
        currentPOIs = mapPOIItemsDoAndSi;
    }

    private void createCustomMarkerUniversities(MapView mapView, Universities university) {
        mCustomMarker = new MapPOIItem();
        mCustomMarker.setItemName(university.get학교명());
        mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(university.getLatitude(), university.getLonitude()));//맵 포인트
        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mCustomMarker.setCustomImageResourceId(R.drawable.schoolicon);//이미지(png파일로 하자)
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);
        mCustomMarker.setUserObject(university);
        mapPOIItemsUniv.add(mCustomMarker);
    }

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
    public void moveToPOIItem(MapView mapView, final MapPOIItem mapPOIItem){
        Log.d("moveToPOIItem", "실행");

        String siDo;
        int zoomLevel;

        if (mapPOIItem.getUserObject().getClass().equals(DoAndSi.class)){ //선택된게 시도 poiItem
            mapView.setMapType(MapView.MapType.Hybrid);
            Log.d(mapPOIItem.getItemName(), "시도 POIItemSelected: ");
            siDo = mapPOIItem.getItemName();
            zoomLevel = 9;
        }

        else { //선택된게 학교 poiItem
            mapView.setMapType(MapView.MapType.Standard);
            Log.d(mapPOIItem.getItemName(), "학교 POIItemSelected: ");
            siDo = ((Universities)mapPOIItem.getUserObject()).get시도();
            zoomLevel = 3;

            selectedSchoolName = String.valueOf(mapPOIItem.getItemName());
            buttonschool.setVisibility(View.VISIBLE);

            //카메라 확대
            mapView.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mapPOIItem.getMapPoint(), zoomLevel)), 200, new CancelableCallback() {
                @Override
                public void onFinish() {
                    Toast.makeText(getBaseContext(), "Animation to " + mapPOIItem.getItemName() + " complete", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancel() {            }
            });
        }

        ArrayList<MapPOIItem> poiitems = new ArrayList<>();

        for(MapPOIItem poiItem: mapPOIItemsUniv) {
            if(((Universities)poiItem.getUserObject()).get시도().equals(siDo)){
                Log.d("add", poiItem.getItemName());
                currentPOIs.remove(poiItem);
                poiitems.add(poiItem);
            }
        }

        Log.d("addPOIItems", "실행전");
        mapView.addPOIItems(poiitems.toArray(new MapPOIItem[poiitems.size()]));
        mapView.removePOIItems(currentPOIs.toArray(new MapPOIItem[currentPOIs.size()]));
        currentPOIs = poiitems;
        Log.d("addPOIItems", "실행후");

        if (mapPOIItem.getUserObject().getClass().equals(DoAndSi.class)) //선택된게 시도 poiItem
            mapView.fitMapViewAreaToShowAllPOIItems();

        buttonback.setVisibility(View.VISIBLE);
    }

    @Override//전국 지도 화면에서 예를 들어 경기도를 누르면 경기도를 카메라 확대를 하고 다른 마커들도 보이도록 한다.
    public void onPOIItemSelected(MapView mapView, final MapPOIItem mapPOIItem) {
        recent_location = mapPOIItem;
        moveToPOIItem(mapView, mapPOIItem);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d("CalloutBalloons", "touched");
        Toast.makeText(this, "Clicked " + mapPOIItem.getItemName(), Toast.LENGTH_SHORT).show();
        selectedSchoolName = String.valueOf(mapPOIItem.getItemName());
        //버튼 생김
        buttonschool.setVisibility(View.VISIBLE);
        //카메라 확대
/*        mapView.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mapPOIItem.getMapPoint(), 3)), 200, new CancelableCallback() {
            @Override
            public void onFinish() {
            }
            @Override
            public void onCancel() {
            }
        });*/
    }

    Button.OnClickListener buttonSchoolClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent=new Intent(Activity_universitiesMap.this, Activity_univ_introduction.class);
            intent.putExtra("univName", selectedSchoolName);
            startActivityForResult(intent, COME_BACK_TO_MAIN);
        }
    };

    Button.OnClickListener buttonbackClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            buttonback.setVisibility(View.INVISIBLE);
            buttonschool.setVisibility(View.INVISIBLE);
            mMapView.setMapType(MapView.MapType.Satellite);
            mMapView.removeAllPOIItems();
            mMapView.addPOIItems(mapPOIItemsDoAndSi.toArray(new MapPOIItem[mapPOIItemsDoAndSi.size()]));
            currentPOIs = mapPOIItemsDoAndSi;
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
        Log.d("CalloutBalloons", "wrong touched");

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
//                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
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
//            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

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

            case COME_BACK_TO_MAIN:
                Log.d("onActivityResult", "main으로 돌아옴");

                cameback = true;

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}