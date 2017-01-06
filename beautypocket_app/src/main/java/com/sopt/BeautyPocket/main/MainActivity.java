package com.sopt.BeautyPocket.main;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kakao.auth.Session;
import com.sopt.BeautyPocket.BackPressCloseHandler;
import com.sopt.BeautyPocket.CustomDialog;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.brandselect.BrandSelect;
import com.sopt.BeautyPocket.calender.SaleCalendarActivity;
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.main.sale.SaleListActivity;
import com.sopt.BeautyPocket.map.LocationService;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.model.Brand;
import com.sopt.BeautyPocket.model.MainSale;
import com.sopt.BeautyPocket.model.WishList;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.register.RegisterActivity;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** 네비게이션 드로어 **/
    LinearLayout btn_logout;
    LinearLayout nav_home;
    LinearLayout nav_wishlist;
    LinearLayout nav_notice;
    LinearLayout nav_customer;
    LinearLayout nav_setting;

    // 위치알람버튼
    ImageView btn_alarm;

    TextView tv_pocketcount;
    TextView tv_myname;
    //세일더보기버튼
    ImageView img_saleinformation;
    //네트워크
    NetworkService networkService;
    Brand brand_num;
    String user_id;

    //뷰페이저
    ViewPager viewPager;
    CircleIndicator indicator;
    ViewPagerAdapter adapter;
    ArrayList<MainSale> userImage;

    //위시리스트 리스트뷰
    ListView mainwish;
    ArrayList<ItemData> wishList;
    MainWishAdapter mainWishAdapter;



    //총합
    TextView tv_total;

    CustomDialog logoutDialog;

    BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_main);
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


        //네트워크
        networkService = ApplicationController.getInstance().getNetworkService();

        //메인 총합
        tv_total = (TextView) findViewById(R.id.tv_total);

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
        //세일정보 더보기
        img_saleinformation = (ImageView) findViewById(R.id.img_saleinformation);
        //위치알람
        btn_alarm = (ImageView) findViewById(R.id.btn_alarm);
        //위치알람 여부 검사
        if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == false) {
            btn_alarm.setImageResource(R.drawable.off_ring);
        } else if (isServiceRunning("com.sopt.BeautyPocket.map.LocationService") == true) {
            btn_alarm.setImageResource(R.drawable.on_ring);
        }


        //위시갯수
        tv_pocketcount = (TextView) findViewById(R.id.tv_pocketcount);
        //네비게이션뷰
        tv_myname = (TextView) findViewById(R.id.tv_myname);

        tv_myname.setText(ApplicationController.user_name);
        user_id = ApplicationController.user_id;
        btn();
        Log.d("내아이디", user_id);
        getBrandList();
        getMainSale();
    }

    public void btn() {
        //세일정보 더보기
        img_saleinformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SaleListActivity.class);
                startActivity(intent);
            }
        });

        //로그아웃버튼
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog = new CustomDialog(MainActivity.this, logoutCancelListener, logoutOkListener);
                logoutDialog.show();
                logoutDialog.setTitle("로그아웃");
                logoutDialog.setContent("로그아웃 하시겠습니까?");
            }
        });
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            logoutDialog.dismiss();
        }
    };

    private void getBrandList() {
        //메인 브랜드 리스트
        final Call<Brand> get_brandList = networkService.get_brandList(user_id);
        get_brandList.enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                if (response.isSuccessful()) {
                    Log.i("지현로그", "" + response.code());
                    wishList = new ArrayList<ItemData>();
                    ArrayList<Brand> brands = new ArrayList<Brand>();
                    brand_num = new Brand(brands, 0, 0);
                    brand_num = response.body();
                    /*Log.d("브랜드갯수", String.valueOf(brand_num.getBrand().get(0).getBrand_id()));*/
                    //위시 총갯수와, 위시 가격 총 합 컨트롤러에 저장하기
                    ApplicationController.getInstance().count = brand_num.getCount();
                    Log.d("카운트값", String.valueOf(brand_num.getCount()));
                    Log.d("변화1", String.valueOf(ApplicationController.count));
                    ApplicationController.total = brand_num.getTotal();
                    tv_pocketcount.setText(String.valueOf(ApplicationController.count));
                    tv_total.setText(String.valueOf(ApplicationController.total));


                    Log.d("토탈값", String.valueOf(brand_num.getTotal()));
                    for (int i = 0; i < (brand_num.getBrand()).size(); i++) {
                        for (int j = 0; j < ApplicationController.itemDataList2.size(); j++) {
                            if (brand_num.getBrand().get(i).getBrand_id() == ApplicationController.itemDataList2.get(j).get_id()) {
                                Log.d("토탈값", String.valueOf(ApplicationController.itemDataList2.get(j).get_id()));
                                wishList.add(ApplicationController.itemDataList2.get(j));
                            }
                        }
                    }
                    //리스트뷰
                    //Log.d("이니스프리 인텐트 전", String.valueOf(wishList.get(0).get_id()));
                    //Log.d("네이쳐리퍼블릭 인텐트 전", String.valueOf(wishList.get(1).get_id()));
                    mainwish = (ListView) findViewById(R.id.listview_mainwish);
                    mainWishAdapter = new MainWishAdapter(MainActivity.this, R.layout.listview_item_mainwish, wishList);
                    mainwish.setAdapter(mainWishAdapter);
                    mainWishAdapter.notifyDataSetChanged();
                    mainwish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
                            intent.putExtra("brand_id", wishList.get(position).get_id());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {

            }
        });
    }

    private void getMainSale() {
        Call<ArrayList<MainSale>> getMainSale = networkService.getMainSale();
        getMainSale.enqueue(new Callback<ArrayList<MainSale>>() {
            @Override
            public void onResponse(Call<ArrayList<MainSale>> call, Response<ArrayList<MainSale>> response) {
                if (response.isSuccessful()) {
                    ArrayList<MainSale> saleList = response.body();
                    Log.d("세일날짜값 어떻게오나요?", saleList.get(0).sale_day);
                    //뷰페이저
                    viewPager = (ViewPager) findViewById(R.id.viewpager_sale);
                    adapter = new ViewPagerAdapter(MainActivity.this, saleList);
                    viewPager.setAdapter(adapter);

                    indicator = (CircleIndicator) findViewById(R.id.indicator);
                    indicator.setViewPager(viewPager);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MainSale>> call, Throwable t) {

            }
        });
    }

    public Boolean isServiceRunning(String serviceName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

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
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void top_click1(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home1:
                //home
                break;
            case R.id.btn_up_wish1:
                Intent top_intent1 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall1:
                ApplicationController.mapAll = true;
                Intent top_intent2 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar1:
                Intent top_intent3 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more1:
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