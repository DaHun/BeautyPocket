package com.sopt.BeautyPocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kakao.auth.Session;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.login.KakaoSignupActivity;
import com.sopt.BeautyPocket.login.Login;
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.login.LoginDBHelper;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.model.User;
import com.sopt.BeautyPocket.network.NetworkService;

import java.security.MessageDigest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends Activity{

    Handler handler;
    //GPS체크

    //자동로그인
    LoginDBHelper loginDBHelper;
    Login login_auto;
    Intent intent;

    LocationManager locationManager;
    CustomDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getAppKeyHash();

        /**
         *  카카오세션 열려있으면(로그인) -> KaKaoSignupActivity -> MainActivity
         *  닫혀있으면 LoginActivity
         */

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(Session.getCurrentSession().isOpened()) {
                    intent = new Intent(getApplicationContext(), KakaoSignupActivity.class);
                }
                else {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }
                startActivity(intent);
                finish();

            }
        };


        //GPS on/off 확인

        try{if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alertCheckGPS();
        }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            handler.sendEmptyMessageDelayed(0, 2000);//GPS가 켜져있으면 넘어감
        }}catch(Exception e){
            handler.sendEmptyMessageDelayed(0, 2000);
        }
        /*handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(Session.getCurrentSession().isOpened()) {
                    intent = new Intent(getApplicationContext(), KakaoSignupActivity.class);
                }
                else {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }
                startActivity(intent);
                finish();

            }
        };*/

        //handler.sendEmptyMessageDelayed(0, 2000);
    }
    //GPS가 안켜져있을 시에 설정 대화상자
    //GPS가 안켜져있을 시에 설정 대화상자
    private void alertCheckGPS(){
        dialog = new CustomDialog(this,cancelListener, okListener);
        dialog.show();
        dialog.setTitle("GPS");
        dialog.setContent("GPS가 꺼져있습니다. GPS를 켜주세요!");
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(SplashActivity.this, "GPS를 설정하지 않으면 어플이 종료됩니다.", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            moveTaskToBack(true);
            finish();

        }
    };

    private View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moveConfigGPS();
            dialog.dismiss();
        }
    };
    //GPS설정화면으로 이동
    private void moveConfigGPS(){
        Intent gpsOptionsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);

    }
    @Override
    protected void onRestart(){
        super.onRestart();
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alertCheckGPS();
        }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            handler.sendEmptyMessageDelayed(0, 1000);//GPS가 켜져있으면 넘어감
        }
    }

}
