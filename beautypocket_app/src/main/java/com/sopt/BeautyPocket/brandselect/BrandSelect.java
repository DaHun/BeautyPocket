package com.sopt.BeautyPocket.brandselect;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kakao.auth.Session;
import com.sopt.BeautyPocket.CustomDialog;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.calender.SaleCalendarActivity;
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.map.LocationService;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.register.RegisterActivity;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

public class BrandSelect extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tv_pocketcount;
    TextView tv_myname;

    /** 네비게이션 드로어 **/
    LinearLayout btn_logout;
    LinearLayout nav_home;
    LinearLayout nav_wishlist;
    LinearLayout nav_notice;
    LinearLayout nav_customer;
    LinearLayout nav_setting;

    //위치알람버튼
    ImageView btn_alarm;

    ImageView[] imageviews = new ImageView[15];
    int[] img_id = {R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4, R.id.img_5, R.id.img_6,
            R.id.img_7, R.id.img_8, R.id.img_9, R.id.img_10, R.id.img_11, R.id.img_12, R.id.img_13, R.id.img_14, R.id.img_15};
    int brand_id;
    CustomDialog logoutDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_brand_select);
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

                logoutDialog = new CustomDialog(BrandSelect.this, logoutCancelListener, logoutOkListener);
                logoutDialog.show();
                logoutDialog.setTitle("로그아웃");
                logoutDialog.setContent("로그아웃 하시겠습니까?");
            }
        });


        for (int i = 0; i < 15; i++) {
            imageviews[i] = (ImageView) findViewById(img_id[i]);
            imageviews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < 15; i++) {
                        if (v.getId() == img_id[i]) {
                            brand_id = i;
                            Intent intent = new Intent(BrandSelect.this, RegisterActivity.class);
                            intent.putExtra("brand_id", brand_id);
                            startActivity(intent);
                            break;
                        }
                    }
                }
            });
        }

    }
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
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.brand_select, menu);
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

    public void top_click8(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home8:
                Intent top_intent1 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish8:
                Intent top_intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall8:
                ApplicationController.mapAll = true;
                Intent top_intent3 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar8:
                Intent top_intent4 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent4);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more8:
                Intent top_intent5 = new Intent(getApplicationContext(), MoreActivity.class);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent5);
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }
}