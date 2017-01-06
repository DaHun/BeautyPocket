package com.sopt.BeautyPocket.map;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.model.BrandLocation;
import com.sopt.BeautyPocket.model.Store;
import com.sopt.BeautyPocket.network.NetworkService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by user on 2016-12-30.
 */

public class LocationService extends Service {

    //위치알림

    private LocationManager locationManager;
    boolean gps_enabled;
    boolean network_enabled;
    Context context;
    LatLng currentLoc;

    NetworkService networkService;
    Store nearStore;

    Intent intent;
    PendingIntent proximityIntent;

    Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("서비스:", "시작");
        //위치관리자 준비
        //위치관리자 구함
        gps_enabled = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //네트워크
        networkService = ApplicationController.getInstance().getNetworkService();

       /* Location lastLocation = null;
        *//*if(gps_enabled) {
            Log.d("쥐피에스","쥐피에스나야~~");
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else if(network_enabled){
            Log.d("네트워크","네트워크나야~~~");
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
      */
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, gpsListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, netListener);







    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("서비스:", "종료");

        locationManager.removeUpdates(gpsListener);
        locationManager.removeUpdates(netListener);
    }

    public void updateWithNewLocation(Location location, String provider) {

        Location loc;

        if (gps_enabled) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                loc = location;
            } else {

                long gpsGenTime = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getTime();
                long curTime = System.currentTimeMillis();
                if ((curTime - gpsGenTime) > 5000) {
                    loc = location;
                    gps_enabled = false;
                }

            }

        } else {
            loc = location;
        }
    }

    LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gps_enabled = true;
            Log.d("계속", "계속 위치가 움직이고있어요!");
            updateWithNewLocation(location, "gps");
            currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            nearStore();


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    LocationListener netListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("계속", "계속 위치가 움직이고있어요!");
            updateWithNewLocation(location, "network");
            currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

            nearStore();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public void nearStore() {


        requestAsyncTask asyncTask = new requestAsyncTask();
        asyncTask.execute();


//        Log.d("계속", "서버요청 들어왔어여");
//        //네트워크 서버로 내위치전송 그럼 서버에서 가장 가까운 위치에 있는 화장품 가게를 쏴줌
//        //그값을 받고 addProximityAlert를 쏨
//
//        // 브로드캐스트 리시버가 메시지를 받을 수 있도록 설정
//        // 액션이 com.androidhuman.exmple.Location인 브로드캐스트 메시지를 받도록 설정
//        nearStore = new Store();
//        Log.d("내 위치", String.valueOf(currentLoc.latitude) + String.valueOf(currentLoc.longitude));
//        BrandLocation myLocation = new BrandLocation(ApplicationController.user_id, currentLoc.latitude, currentLoc.longitude);
//        //Log.d("내 유저 아이디", ApplicationController.user_id);
//        Call<Store> getAlarm = networkService.getAlarm(myLocation);
//        try {
//            Response<Store> response = getAlarm.execute();
//
//            nearStore = response.body();
//            intent = new Intent(LocationService.this, LocationReceiver.class);
//            proximityIntent = PendingIntent.getBroadcast(LocationService.this, 0, intent, 0);
//            locationManager.addProximityAlert(nearStore.getStore_latitude(), nearStore.getStore_longitude(), 1500f, -1, proximityIntent);
//            Toast.makeText(getApplicationContext(), "알람성공", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        getAlarm.enqueue(new Callback<Store>() {
//            @Override
//            public void onResponse(Call<Store> call, Response<Store> response) {
//                if (response.isSuccessful()) {
//                    nearStore = response.body();
//                    intent = new Intent(LocationService.this, LocationReceiver.class);
//                    proximityIntent = PendingIntent.getBroadcast(LocationService.this, 0, intent, 0);
//                    locationManager.addProximityAlert(nearStore.getStore_latitude(), nearStore.getStore_longitude(), 1500f, -1, proximityIntent);
//                    Toast.makeText(getApplicationContext(), "알람성공", Toast.LENGTH_SHORT).show();
//
//
//                    // ProximityAlert 등록
//
//                    //매장이름
//                    /*intent.putExtra("brandname",nearStore.getStore_name());*/
//                    //Log.d("위치알람 받아온 위도경도출력", String.valueOf(nearStore.getStore_latitude()+nearStore.getStore_longitude()));
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<Store> call, Throwable t) {
//                //Toast.makeText(getApplicationContext(), "알람실패", Toast.LENGTH_SHORT).show();
//                Log.d("실패원인", t.getMessage());
//
//            }
//
//        });


    }


    public class requestAsyncTask extends AsyncTask<String,Void,String> {

        public String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            nearStore = new Store();


        }

        @Override
        protected String doInBackground(String... params) {
            BrandLocation myLocation = new BrandLocation(ApplicationController.user_id, currentLoc.latitude, currentLoc.longitude);

            Call<Store> getAlarm = networkService.getAlarm(myLocation);
            try {
                Response<Store> response = getAlarm.execute();

                nearStore = response.body();
                ApplicationController.nearStore = nearStore;


            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }


        @Override
        protected void onPostExecute(String s) {

            intent = new Intent(LocationService.this, LocationReceiver.class);
            //매장이름
            proximityIntent = PendingIntent.getBroadcast(LocationService.this, 0, intent, 0);
            locationManager.addProximityAlert(nearStore.getStore_latitude(), nearStore.getStore_longitude(), 1000f, -1, proximityIntent);


        }
    }




}