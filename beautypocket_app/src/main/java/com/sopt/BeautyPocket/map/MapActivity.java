package com.sopt.BeautyPocket.map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.kakao.auth.Session;
import com.sopt.BeautyPocket.BackPressCloseHandler;
import com.sopt.BeautyPocket.CustomDialog;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.brandselect.BrandSelect;
import com.sopt.BeautyPocket.calender.SaleCalendarActivity;
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.main.ItemData;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.model.BrandLocation;
import com.sopt.BeautyPocket.model.Store;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** 네비게이션 드로어 **/
    LinearLayout btn_logout;
    LinearLayout nav_home;
    LinearLayout nav_wishlist;
    LinearLayout nav_notice;
    LinearLayout nav_customer;
    LinearLayout nav_setting;

    //위치알람버튼
    ImageView btn_alarm;

    //지도 관련 변수선언
    public final static int ZOOM_LEVEL = 15;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    String best = null;
    Criteria crit;
    boolean gps_enabled;
    boolean network_enabled;

    ArrayList<Integer> a;


    MapFragment mapFragment;
    //상세보기
    TextView tv_storename;
    TextView tv_storeaddress;
    TextView tv_storetel;
    ImageView btn_call;
    ImageView btn_newmap;

    private Marker centerMarker;
    private MarkerOptions centerMarkerOptions;

    private MarkerOptions poiMarkerOptions;
    Marker prevMarker;

    //네트워크
    NetworkService networkService;
    //위치 정보를 담을 리스트
    ArrayList<Store> nearStoreList;
    int brand_id;

    TextView tv_pocketcount;
    TextView tv_myname;

    CustomDialog logoutDialog;

    ImageView btn_mapselect;

    BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        //네비게이션 레이아웃, 버튼
        btn_logout = (LinearLayout)findViewById(R.id.btn_logout);
        nav_home = (LinearLayout)findViewById(R.id.nav_home);
        nav_home.setOnClickListener(navClickListener);
        nav_wishlist = (LinearLayout)findViewById(R.id.nav_wishlist);
        nav_wishlist.setOnClickListener(navClickListener);
        nav_notice = (LinearLayout)findViewById(R.id.nav_notice);
        nav_notice.setOnClickListener(navClickListener);
        nav_customer = (LinearLayout)findViewById(R.id.nav_customer);
        nav_customer.setOnClickListener(navClickListener);
        nav_setting = (LinearLayout)findViewById(R.id.nav_setting);
        nav_setting.setOnClickListener(navClickListener);

        //위치알람
        btn_alarm = (ImageView) findViewById(R.id.btn_alarm);

        //위시갯수
        tv_pocketcount = (TextView) findViewById(R.id.tv_pocketcount);
        //네비게이션뷰
        tv_myname = (TextView) findViewById(R.id.tv_myname);
        tv_myname.setText(ApplicationController.user_name);
        tv_pocketcount.setText(String.valueOf(ApplicationController.count));
        btn_mapselect = (ImageView)findViewById(R.id.btn_mapselect);
        if(ApplicationController.mapAll == false){
            btn_mapselect.setImageResource(R.drawable.whereami);
        }else if(ApplicationController.mapAll == true){
            btn_mapselect.setImageResource(R.drawable.whereami_selected);

        }

        //위치알람 여부 검사
        if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == false) {
            btn_alarm.setImageResource(R.drawable.off_ring);
        } else if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == true) {
            btn_alarm.setImageResource(R.drawable.on_ring);
        }

        //위치알람버튼
        btn_alarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == false) {
                    Intent intent = new Intent(getApplicationContext(), LocationService.class);
                    Log.d("위치알림켜버렸다~!", "안녕");
                    btn_alarm.setImageResource(R.drawable.on_ring);
                    startService(intent);
                } else if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == true) {
                    Intent intent = new Intent(getApplicationContext(), LocationService.class);
                    btn_alarm.setImageResource(R.drawable.off_ring);
                    Log.d("위치알림꺼버렸다~!", "잘가");
                    stopService(intent);
                }

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog = new CustomDialog(MapActivity.this, logoutCancelListener, logoutOkListener);
                logoutDialog.show();
                logoutDialog.setTitle("로그아웃");
                logoutDialog.setContent("로그아웃 하시겠습니까?");
            }
        });

        //브랜드 아이디 받아오기
        if (ApplicationController.mapAll == false) {
            final Intent intent = getIntent();
            brand_id = intent.getExtras().getInt("brand_id");
        }


        //네트워크
        networkService = ApplicationController.getInstance().getNetworkService();
        //위치관리자 준비
        //위치관리자 구함
        gps_enabled = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //상세보기 텍스트뷰
        tv_storename = (TextView) findViewById(R.id.tv_storename);
        tv_storeaddress = (TextView) findViewById(R.id.tv_storeaddress);
        tv_storetel = (TextView) findViewById(R.id.tv_storetel);
        btn_call = (ImageView) findViewById(R.id.btn_call);
        btn_newmap = (ImageView) findViewById(R.id.btn_newmap);

        btn_newmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MapActivity.this, MapActivity.class);
                intent1.putExtra("brand_id", brand_id);
                startActivity(intent1);
                finish();
            }
        });


        new NetworkAsyncTask().execute("a");

        //구글맵 준비
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map);


        centerMarkerOptions = new MarkerOptions();
        centerMarkerOptions.title("내 위치");
        centerMarkerOptions.snippet("이동중");
        centerMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location));
        poiMarkerOptions = new MarkerOptions();

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
            updateWithNewLocation(location, "gps");
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            //centerMarker.setPosition(currentLoc);


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
            updateWithNewLocation(location, "network");
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

         /* centerMarker.setPosition(currentLoc);*/

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


    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, gpsListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, netListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(gpsListener);
        locationManager.removeUpdates(netListener);
    }


    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @SuppressWarnings("MissingPermission")
        @Override
        public void onMapReady(final GoogleMap map) {
            googleMap = map;
            Location lastLocation = null;
            if (gps_enabled) {
                Log.d("쥐피에스", "쥐피에스나야~~");
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (network_enabled) {
                Log.d("네트워크", "네트워크나야~~~");
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            LatLng lastLatLng;
            if (lastLocation != null) {
                lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                lastLatLng = new LatLng(37.606320, 127.041808);
            }

            Log.d("mylocation", "Last location: " + lastLatLng.latitude + ", " + lastLatLng.longitude);


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, ZOOM_LEVEL));

            centerMarkerOptions.position(lastLatLng);

            centerMarker = googleMap.addMarker(centerMarkerOptions);
            centerMarker.showInfoWindow();
            for (Store st : nearStoreList) {
                /*poiMarkerOptions.position(new LatLng(Double.valueOf(st.getStore_latitude()).doubleValue(),Double.valueOf(st.getStore_longitude()).doubleValue()))
                        .title(st.getStore_name())
                        .icon()
                */
                poiMarkerOptions.position(new LatLng(Double.valueOf(st.getStore_latitude()).doubleValue(), Double.valueOf(st.getStore_longitude()).doubleValue()));
                poiMarkerOptions.title(st.getStore_name());
                poiMarkerOptions.snippet(st.getStore_address());

                Marker newMarker = googleMap.addMarker(poiMarkerOptions);
                //포문 돌려서 이프로 브랜드아이디갑 일치하면 그거랑 맞는 마커 아이콘 이어주기

                newMarker.setIcon(BitmapDescriptorFactory.fromResource(ApplicationController.itemDataList2.get(st.getBrand_id()).getImg_marker()));


                st.setMarker(newMarker);
                st.getMarker().showInfoWindow();
            }


            //            마커 윈도우 클릭 시 이벤트 처리
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    for (int i = 0; i < nearStoreList.size(); i++) {
                        if (marker.getId().equals(nearStoreList.get(i).getMarker().getId())) {
                            Log.d("마커를 눌렀을때", nearStoreList.get(i).getStore_name());

                            tv_storename.setText(nearStoreList.get(i).getStore_name());
                            tv_storeaddress.setText(nearStoreList.get(i).getStore_address());
                            tv_storetel.setText(nearStoreList.get(i).getSotre_tel());
                            final int finalI = i;
                            btn_call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nearStoreList.get(finalI).getSotre_tel()));
                                    startActivity(intent);

                                }
                            });

                        }
                    }
                }
            });
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d("마커를 눌렀을때", "눌렸다");

                    for (int i = 0; i < nearStoreList.size(); i++) {
                        if (marker.getId().equals(nearStoreList.get(i).getMarker().getId())) {
                            Log.d("마커를 눌렀을때", nearStoreList.get(i).getStore_name());

                            tv_storename.setText(nearStoreList.get(i).getStore_name());
                            tv_storeaddress.setText(nearStoreList.get(i).getStore_address());
                            tv_storetel.setText(nearStoreList.get(i).getSotre_tel());
                            final int finalI = i;
                            btn_call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nearStoreList.get(finalI).getSotre_tel()));
                                    startActivity(intent);

                                }
                            });

                        }
                    }
                    return false;
                }


            });


        }
    };
    private View.OnClickListener logoutCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logoutDialog.dismiss();
        }
    };

    private View.OnClickListener logoutOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Session.getCurrentSession().close();
            Intent intent = new Intent(MapActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            logoutDialog.dismiss();
        }
    };

    LinearLayout.OnClickListener navClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.nav_home :
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.nav_wishlist :
                    Intent intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.nav_notice :
                    Intent intent3 = new Intent(getApplicationContext(), Notice.class);
                    startActivity(intent3);
                    finish();
                    break;
                case R.id.nav_customer :
                    Intent intent4 = new Intent(getApplicationContext(), GogagCenter.class);
                    startActivity(intent4);
                    finish();
                    break;
                case R.id.nav_setting :
                    Intent intent5 = new Intent(getApplicationContext(), MoreActivity.class);
                    startActivity(intent5);
                    finish();
                    break;
            }
        }
    };

    public Boolean isServiceRunning(String serviceName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(getApplicationContext(), BrandSelect.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {

        public final static String TAG = "NetworkAsyncTask";
        public final static int TIME_OUT = 10000;

        ProgressDialog progressDialog;
        String address;
        Location lastLocation;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MapActivity.this, "로딩중", "잠시만 기다려주세요.");


        }

        @Override
        protected String doInBackground(String... strings) {
            address = strings[0];
            String a = "apple";

            Location lastLocation = null;
            if (gps_enabled) {
                Log.d("쥐피에스", "쥐피에스나야~~");
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (network_enabled) {
                Log.d("네트워크", "네트워크나야~~~");
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            LatLng lastLatLng;
            if (lastLocation != null) {
                lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                lastLatLng = new LatLng(37.606320, 127.041808);
            }

            Log.d("mylocation", "Last location: " + lastLatLng.latitude + ", " + lastLatLng.longitude);


            nearStoreList = new ArrayList<Store>();


            if (ApplicationController.mapAll == false) {
                BrandLocation myLocation = new BrandLocation(brand_id, lastLatLng.latitude, lastLatLng.longitude);
                Log.d("내브랜드아이디", String.valueOf(brand_id));
                Call<ArrayList<Store>> getBrandMap = networkService.getBrandMap(myLocation);
                getBrandMap.enqueue(new Callback<ArrayList<Store>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Store>> call, Response<ArrayList<Store>> response) {
                        if (response.isSuccessful()) {
                            nearStoreList = response.body();
                            mapFragment.getMapAsync(mapReadyCallback);

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Store>> call, Throwable t) {
                    }
                });
            } else if (ApplicationController.mapAll = true) {

                BrandLocation myLocation = new BrandLocation(ApplicationController.user_id, lastLatLng.latitude, lastLatLng.longitude);
                Log.d("내 유저 아이디", ApplicationController.user_id);
                Call<ArrayList<Store>> getTotalMap = networkService.getTotalMap(myLocation);
                getTotalMap.enqueue(new Callback<ArrayList<Store>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Store>> call, Response<ArrayList<Store>> response) {
                        if (response.isSuccessful()) {
                            nearStoreList = response.body();
                            Log.d("모든 브랜드", response.body().toString());
                            mapFragment.getMapAsync(mapReadyCallback);



                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<Store>> call, Throwable t) {
                    }
                });


            }


            return a;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("온포스트", "들어는왔다");


            progressDialog.dismiss();
        }


    }

    public void top_click3(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home3:
                Intent top_intent1 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish3:
                Intent top_intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall3:
                if(ApplicationController.mapAll == false){
                    ApplicationController.mapAll = true;
                    Intent top_intent5 = new Intent(getApplicationContext(), MapActivity.class);
                    top_intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    top_intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(top_intent5);
                    overridePendingTransition(0, 0);
                    finish();
                }
                break;
            case R.id.btn_up_calendar3:
                Intent top_intent3 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more3:
                Intent top_intent4 = new Intent(getApplicationContext(), MoreActivity.class);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent4);
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }
}
