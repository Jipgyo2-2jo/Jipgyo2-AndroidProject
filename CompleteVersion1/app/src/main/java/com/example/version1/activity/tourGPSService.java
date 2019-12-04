package com.example.version1.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.version1.R;
import com.example.version1.domain.MissionQuiz;

import java.util.ArrayList;

import static com.example.version1.activity.Activity_eachUniversityMap.CHANNEL_ID;

public class tourGPSService extends Service {
    private ArrayList<MissionQuiz> missionQuizs;
    private Messenger mClient = null;

    public static final int MSG_REGISTER_CLIENT = 1;
    //public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_ACTIVITY = 4;

    public tourGPSService() {
    }

    public IBinder tourBinder = new tourBinder();


    class tourBinder extends Binder {
        tourGPSService getService(){
            return tourGPSService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        Log.d("Service", "Bind됨");

        return mMessenger.getBinder();
    }

    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.w("test","ControlService - message what : "+msg.what +" , msg.obj "+ msg.obj);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;  // activity로부터 가져온
                    break;
            }
            return false;
        }
    }));

    @Override
    public boolean onUnbind(Intent intent){
        Log.d("Service", "UnBind됨");

        try {
            Bundle bundle = new Bundle();
            bundle.putInt("fromService", foundQuizs.size());
            bundle.putSerializable("foundQuizs", foundQuizs);
            Message msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
            msg.setData(bundle);
            mClient.send(msg);      // msg 보내기
        } catch (RemoteException e) {
        }

        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "Created 됨");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        missionQuizs = (ArrayList<MissionQuiz>) intent.getSerializableExtra("Missions");
        if(missionQuizs == null){
            Log.d("Mission위치", "GetExtra 실패");
        }

        Intent notificationIntent = new Intent(this, Activity_eachUniversityMap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("이학내학 실행중")
                .setContentText("투어가 진행중입니다")
                .setSmallIcon(R.drawable.schoolicon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            Log.d("GPS", "Permission Fail");
            return START_STICKY;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        final LocationListener gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("GPS in Service", "Location Update, 위도(" + location.getLatitude() + "), 경도(" + location.getLongitude() + "), provider(" + location.getProvider() + ")");

                activateMission(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Service", "위치 정보 상태 변경. 공급자: " + provider + ", 상태: " + status);
            }
            public void onProviderEnabled(String provider) {
                Log.d("Service", "위치 정보 사용 가능. 공급자: " + provider);
            }
            public void onProviderDisabled(String provider) {
                Log.d("Service", "위치 정보 사용 불가. 공급자: " + provider);
            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,
                1,
                gpsLocationListener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                1,
                gpsLocationListener);

        return START_STICKY;
    }

    ArrayList<MissionQuiz> foundQuizs = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.O)
    public void activateMission(Location location) {
        Location gl = new Location("2");
        for (MissionQuiz point : missionQuizs) {
            gl.setLatitude(point.getLatitude());
            gl.setLongitude(point.getLongitude());
            if (location.distanceTo(gl) < 40 && point.getIsActivated() == 0){
                Log.d("Service", "Found Mission");
                point.setIsActivated(1);
                foundQuizs.add(point);

                final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(700);
            }
        }
    }

    @Override
    public void onDestroy(){
        Log.d("Service", "Destroy");
    }
}
