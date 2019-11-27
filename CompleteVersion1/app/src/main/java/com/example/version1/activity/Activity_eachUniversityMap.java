package com.example.version1.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.version1.R;
import com.example.version1.database.UniversityMissionQuizDB;
import com.example.version1.database.UniversityTourAccessDB;
import com.example.version1.database.UniversityTourPolylineDB;
import com.example.version1.domain.MissionQuiz;
import com.example.version1.domain.UniversityTour;
import com.example.version1.domain.UniversityTourPolyline;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class Activity_eachUniversityMap extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener {

    private static final String LOG_TAG = "Act_eachUniversitiesMap";
    private Button finishbutton;
    private Button handleButton;
    private String tmpstr;
    private String mi = "미션 목록";
    private int missionNum = 0;
    private int courseselect = 0;
    private int playmode = 0;
    private int tracking = 0;
    private MapPolyline polyline2;
    private MapPoint[] mPolyline2Points;
    private ArrayList<MapPolyline> polylineArrayList;
    private MapView mMapView;
    private MapPOIItem mCustomMarker;
    private Button button1;
    private Button buttonCourseSelect;
    private FrameLayout courseFrameLayout;
    private FrameLayout missionFrameLayout;
    private ArrayList<UniversityTour> universityTourarray;
    private ArrayList<MissionQuiz> missionQuizs;
    private ArrayList<MissionQuiz> missionQuizs2;
    private ArrayList<UniversityTourPolyline> universityTourPolylinearray;
    private CourseListFragment courseListFragment;
    private MissionListFragment missionListFragment;
    private MapPoint.GeoCoordinate mapPointGeo;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    // CalloutBalloonAdapter 인터페이스 구현
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override//디폴트 값
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText(universityTourarray.get(poiItem.getTag() - 1).get한줄평());
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        String univName = intent.getStringExtra("univName");

        //일단 DB에서 해당 학교의 장소 위치를 모두 받는 메소드를 실행한다.
        UniversityTourAccessDB universityTourAccessDB = new UniversityTourAccessDB();
        universityTourarray = universityTourAccessDB.getUniversityTourFromDB(univName);

        //DB에서 polyline을 가져온다.
        UniversityTourPolylineDB universityTourPolylineDB = new UniversityTourPolylineDB();
        universityTourPolylinearray = universityTourPolylineDB.getUniversityTourPolylineFromDB(univName);

        //그리고 미션에 대한 정보를 모두 받는 메소드도 실행한다.
        UniversityMissionQuizDB universityMissionQuizDB = new UniversityMissionQuizDB();
        missionQuizs = universityMissionQuizDB.getUniversityMissionQuizArrayFromDB(univName);
        //-------------------------------------------------------------

        setContentView(R.layout.activity_each_university_map);

        handleButton = findViewById(R.id.handle);
        finishbutton = findViewById(R.id.finishbutton);
        button1 = findViewById(R.id.button);
        button1.setOnClickListener(mClickListener);
        buttonCourseSelect = findViewById(R.id.buttonCourseSelect);
        buttonCourseSelect.setOnClickListener(buttonCourseSelectClickListener);
        courseFrameLayout = (FrameLayout) findViewById(R.id.courseFrameLayout);
        missionFrameLayout = (FrameLayout) findViewById(R.id.missionFrameLayout);
//        RadioButton mapnormal = findViewById(R.id.mapnormal);
//        RadioButton maphybrid = findViewById(R.id.maphybrid);
//        RadioButton mapsatellite = findViewById(R.id.mapsatellite);
        RadioGroup mapradiogroup = findViewById(R.id.mapradiogroup);

        mapradiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.mapnormal:
                        mMapView.setMapType(MapView.MapType.Standard);
                        break;
                    case R.id.maphybrid:
                        mMapView.setMapType(MapView.MapType.Hybrid);
                        break;
                    case R.id.mapsatellite:
                        mMapView.setMapType(MapView.MapType.Satellite);
                        break;
                }
            }
        });

        //어댑터를 이용해 코스 리스트 생성
        FragmentManager manager = getSupportFragmentManager();
        courseListFragment = (CourseListFragment) manager.findFragmentById(R.id.courselistfragment);
        for (int c = 0; c < universityTourPolylinearray.size(); c++) {
            //length랑 courseTime 다시 설정해야함
            courseListFragment.addItem(universityTourPolylinearray.get(c).getCourseType(),
                    "" + c, "00");
        }

        FragmentManager manager2 = getSupportFragmentManager();
        missionListFragment = (MissionListFragment) manager2.findFragmentById(R.id.missionlistfragment);

        //맵뷰 환경 설정
        mMapView = (MapView) findViewById(R.id.map_view);
        //기본 환경 설정
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCurrentLocationEventListener(this);
        //중심점 변경 + 줌 레벨 변경
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.28163800, 127.04539600), 2, true);
        //이전에 생성된 poiitems제거
        mMapView.removeAllPOIItems();
        // 구현한 CalloutBalloonAdapter 등록
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mMapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.player, new MapPOIItem.ImageOffset(38,0));
        mMapView.setCustomCurrentLocationMarkerImage(R.drawable.player, new MapPOIItem.ImageOffset(38,0));

        for (int i = 0; i < universityTourarray.size(); i++) {
            createCustomMarker(mMapView, universityTourarray.get(i));
        }

        polylineArrayList = new ArrayList<MapPolyline>();
        for (int ps = 0; ps < universityTourPolylinearray.size(); ps++) {
            //맵포인트 추가
            mPolyline2Points = new MapPoint[universityTourPolylinearray.get(ps).getLatitudeArray().size()];
            for (int p = 0; p < universityTourPolylinearray.get(ps).getLatitudeArray().size(); p++) {
                mPolyline2Points[p] = MapPoint.mapPointWithGeoCoord(universityTourPolylinearray.get(ps).getLatitudeArraypoint(p),
                        universityTourPolylinearray.get(ps).getLogitudeArraypoint(p));
            }
            double rand = Math.random();
            rand = rand*6;
            rand = Math.round(rand);

            //polyline생성 및 arraylist에 추가
            polyline2 = new MapPolyline(21);
            polyline2.setTag(2000);
            if(rand == 0){
                polyline2.setLineColor(Color.argb(128, 255, ps / universityTourPolylinearray.size() * 255, 0));
            }
            else if(rand == 1){
                polyline2.setLineColor(Color.argb(128, 0, 255, ps / universityTourPolylinearray.size() * 255));
            }
            else if(rand == 2){
                polyline2.setLineColor(Color.argb(128, ps / universityTourPolylinearray.size() * 255, 0, 255));
            }
            else if(rand == 3){
                polyline2.setLineColor(Color.argb(128, 255, 0, ps / universityTourPolylinearray.size() * 255));
            }
            else if(rand == 4){
                polyline2.setLineColor(Color.argb(128, ps / universityTourPolylinearray.size() * 255, 255, 0));
            }
            else if(rand == 5){
                polyline2.setLineColor(Color.argb(128, 0, ps / universityTourPolylinearray.size() * 255, 255));
            }
            polyline2.addPoints(mPolyline2Points);
            polylineArrayList.add(polyline2);
        }

        finishbutton.setOnClickListener(fClickListener);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }

    }

    //드로어의 미션을 선택하면 화면이 이동한다.
    public void onMissionSelected(MissionQuiz q) {
        //일단 basic mission으로 이동
        Intent intent = new Intent(Activity_eachUniversityMap.this, Activity_basic_mission.class);
        intent.putExtra("missionQuiz", q);
        startActivityForResult(intent, 001);
    }

    //코스가 선택되면 경로를 지도상에 그려줌
    public void onCourseSelected(String courseName, int position) {
        mMapView.removeAllPolylines();//나머지 polyline다 지워주고
        Toast.makeText(this, "선택: " + position, Toast.LENGTH_SHORT).show();
        mMapView.addPolyline(polylineArrayList.get(position));
        missionNum = universityTourPolylinearray.get(position).getMissionNum();
        courseselect = 1;
    }

    //코스를 선택하면 코스 선택 메뉴가 사라지고, 본격적인 투어를 제공하도록 한다.
    Button.OnClickListener buttonCourseSelectClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onClick(View v) {
            if(courseselect == 0){
                Toast.makeText(Activity_eachUniversityMap.this, "코스를 선택하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder alBuilder = new AlertDialog.Builder(Activity_eachUniversityMap.this);
            alBuilder.setMessage("선택한 코스로 투어를 시작하시겠습니까?\n투어 중 다른 코스로 변경하면\n클리어한 미션이 초기화됩니다.");
            alBuilder.setTitle("코스 결정");

            // "예" 버튼을 누르면 실행되는 리스너
            alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    courseFrameLayout.setVisibility(View.INVISIBLE);
                    missionFrameLayout.setVisibility(View.VISIBLE);

                    //카메라 확대
                    final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    final LocationListener gpsLocationListener = new LocationListener() {
                        public void onLocationChanged(Location location) {
                        }
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }
                        public void onProviderEnabled(String provider) {
                        }
                        public void onProviderDisabled(String provider) {
                        }
                    };

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);

                    mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()),1)), 200, new CancelableCallback() {
                        @Override
                        public void onFinish() {
                        }
                        @Override
                        public void onCancel() {
                        }
                    });

                    playmode = 1;
                    tmpstr = mi.concat("  "+ 0 +"/"+missionNum);
                    handleButton.setText(tmpstr);
                    //커스텀 토스트 메세지
                    Context context = getApplicationContext();
                    CharSequence txt = "투어를 시작합니다.";
                    int time = Toast.LENGTH_LONG;
                    Toast.makeText(context, txt, time).show();
                    Toast toast = Toast.makeText(context, txt, time);
                    LayoutInflater inflater = getLayoutInflater();
                    View view =
                            inflater.inflate(R.layout.custom_toastview,
                                    (ViewGroup)findViewById(R.id.containers));
                    TextView txtView = view.findViewById(R.id.txtview);
                    txtView.setText(txt);
                    toast.setView(view);
                    toast.show();

                    courseFrameLayout.setVisibility(View.INVISIBLE);
                    missionFrameLayout.setVisibility(View.VISIBLE);
                }
            });
            // "아니오" 버튼을 누르면 실행되는 리스너
            alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return; // 아무런 작업도 하지 않고 돌아간다
                }
            });
            alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
        }
    };

    //버튼을 누르면 설정 화면 전환
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(tracking == 0){
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                tracking = 1;
            }
            else{
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                tracking = 0;
            }
        }
    };

    //버튼을 누르면 투어종료 화면으로
    Button.OnClickListener fClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent=new Intent(Activity_eachUniversityMap.this, Activity_tour_finish.class);
            intent.putExtra("missionNum", missionNum);
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("투어를 종료하시겠습니까?\n클리어한 미션이 저장되지 않습니다.");

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

    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("투어를 종료하시겠습니까?\n클리어한 미션이 저장되지 않습니다.");

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
        playmode = 0;
        courseselect = 0;
    }

    private void createCustomMarker(MapView mapView, UniversityTour universityTourSculpture) {
        mCustomMarker = new MapPOIItem();
        mCustomMarker.setItemName(universityTourSculpture.get시설());//이름
        mCustomMarker.setTag(universityTourSculpture.getId_num());//구조물 아이디
        //구조물 위치
        mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(universityTourSculpture.getLatitude(), universityTourSculpture.getLonitude()));
        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

        mCustomMarker.setCustomImageResourceId(R.drawable.custom_map_present);//이미지(png파일로 하자)
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);

        mapView.addPOIItem(mCustomMarker);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        mapPointGeo = currentLocation.getMapPointGeoCoord();

        //gps와 건물의 거리가 (50m) 가까워지면 미션을 주는 처리, 미션 잠김 -> 활성화로 전환
        if(playmode == 1){
            activateMission(mapPointGeo);
        }
        else
            return;
    }

    public void activateMission(MapPoint.GeoCoordinate mapPointGeo){
        Location cl = new Location("1");//현재 위치
        cl.setLatitude(mapPointGeo.latitude);
        cl.setLongitude(mapPointGeo.longitude);

        Location gl = new Location("2");//미션이 발생하는 곳의 위치(유동적)
        for(int i = 0; i < missionQuizs.size(); i++){
            gl.setLatitude(missionQuizs.get(i).getLatitude());
            gl.setLongitude(missionQuizs.get(i).getLongitude());
            //50m보다 가까워질 경우 미션 활성화
            if(cl.distanceTo(gl) < 50 && missionQuizs.get(i).getIsActivated() == 0){
                missionQuizs.get(i).setIsActivated(1);
                //슬라이드 드로어에 미션 추가
                MapPOIItem tmppoiItem = mMapView.findPOIItemByTag(missionQuizs.get(i).getId());
                //이름을 미리 지정해서 넘겨준다.
                missionListFragment.addMission(1, ""+tmppoiItem.getItemName()+ " " +missionQuizs.get(i).getTypeName(),
                        missionQuizs.get(i).getRightAnswer() + "/"+missionQuizs.get(i).getQuizArrayList().size(), missionQuizs.get(i));
                missionListFragment.adapter.notifyDataSetChanged();
                //테스트용 토스트 메세지
                Toast.makeText(this, "미션 활성화", Toast.LENGTH_SHORT).show();
                //퀴즈 미션의 id와 건물(장소)의 id를 같도록 설정한다고 가정
                //tag로 marker를 가져옴
                //마커의 색 변화
                tmppoiItem.setCustomImageResourceId(R.drawable.custom_map_present2);
                mMapView.addPOIItem(tmppoiItem);
                final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
            }
        }
        //missionQuizs
        //미션이 활성화 될 건물이 있는지 확인
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

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Intent intent=new Intent(Activity_eachUniversityMap.this, Activity_loc_information.class);
        intent.putExtra("locationName", mapPOIItem.getItemName());

        startActivity(intent);
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
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(Activity_eachUniversityMap.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(Activity_eachUniversityMap.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(Activity_eachUniversityMap.this,
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(Activity_eachUniversityMap.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(Activity_eachUniversityMap.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Activity_eachUniversityMap.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Activity_eachUniversityMap.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_eachUniversityMap.this);
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

        switch (resultCode){
            case 100:
                int a = data.getIntExtra("correctNum", 0);
                MissionQuiz m = (MissionQuiz) data.getSerializableExtra("missionQuiz");
                Toast.makeText(this, "맞춘 정답 수: " + a, Toast.LENGTH_LONG).show();
                missionListFragment.modifyMission(a, m);
                int ansnum = missionListFragment.getanswers();
                missionListFragment.adapter.notifyDataSetChanged();
                tmpstr = mi.concat("  "+ ansnum +"/"+missionNum);
                handleButton.setText(tmpstr);
                //미션개수를 충족한 경우
                if(ansnum >= missionNum){
                    //투어종료버튼 활성화
                    finishbutton.setVisibility(View.VISIBLE);
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