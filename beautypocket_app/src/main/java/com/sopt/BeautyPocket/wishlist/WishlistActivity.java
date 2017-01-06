package com.sopt.BeautyPocket.wishlist;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.Session;
import com.sopt.BeautyPocket.BackPressCloseHandler;
import com.sopt.BeautyPocket.CustomDialog;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.brandselect.BrandSelect;
import com.sopt.BeautyPocket.calender.SaleCalendarActivity;
import com.sopt.BeautyPocket.detail.RegisterComplete;
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.map.LocationService;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.model.WishList;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.wishlist_brand.WishlistBrand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistActivity extends AppCompatActivity
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

    /** 네트워크 **/
    NetworkService service;

    //GridView, ItemData Set, Adapter
    GridView gridView;
    ArrayList<WishList> GRID_DATA;
    ArrayList<WishList> init_GRID_DATA;

    int brandId;
    CustomGridAdapter adapter;

    //구매 완료 체크
    ImageView btn_complete_delete;
    LinearLayout layout_btn_complete_delete;
    ArrayList<WishList> deletewish;

    //Showing
    LinearLayout default_layout;
    LinearLayout nondefault_layout;
    LinearLayout brand_layout;

    ImageView btn_brandwish;

    Spinner spinner;

    TextView tv_totalmoney;
    int price;

    TextView tv_pocketcount;
    TextView tv_myname;

    CustomDialog logoutDialog;
    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_wishlist);
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

        tv_totalmoney = (TextView)findViewById(R.id.tv_totalmoney);
        //위시갯수
        tv_pocketcount = (TextView)findViewById(R.id.tv_pocketcount);
        //네비게이션뷰
        tv_myname = (TextView)findViewById(R.id.tv_myname);
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
                logoutDialog = new CustomDialog(WishlistActivity.this, logoutCancelListener, logoutOkListener);
                logoutDialog.show();
                logoutDialog.setTitle("로그아웃");
                logoutDialog.setContent("로그아웃 하시겠습니까?");
            }
        });


//        brandId = 0;
        //브랜드 아이디 받아오기
        Intent intent = getIntent();
//        brandId  = intent.getExtras().getInt("brand_id");
        brandId  = intent.getIntExtra("brand_id", -1);

        Log.i("Mytag", String.valueOf(brandId));
        int [] img_id = {R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4, R.id.img_5, R.id.img_6,
                R.id.img_7, R.id.img_8, R.id.img_9, R.id.img_10, R.id.img_11, R.id.img_12, R.id.img_13,R.id.img_14, R.id.img_15};

        int [] img_logo = {R.drawable.brandwishlist_logo01, R.drawable.brandwishlist_logo02, R.drawable.brandwishlist_logo03, R.drawable.brandwishlist_logo04, R.drawable.brandwishlist_logo05,
                R.drawable.brandwishlist_logo06, R.drawable.brandwishlist_logo07, R.drawable.brandwishlist_logo08, R.drawable.brandwishlist_logo09, R.drawable.brandwishlist_logo10, R.drawable.brandwishlist_logo11,
                R.drawable.brandwishlist_logo12, R.drawable.brandwishlist_logo13, R.drawable.brandwishlist_logo14, R.drawable.brandwishlist_logo15,};
        for(int i = 0 ; i < 15 ; i ++){
            if(brandId == i){
                ImageView imgview = (ImageView)findViewById(R.id.img_brand_img);
                imgview.setImageResource(img_logo[i]);
                break;
            }
        }

        //네트워크
        service = ApplicationController.getInstance().getNetworkService();

        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setOnItemClickListener(gridClickListener);
        gridView.setOnItemLongClickListener(gridLongClickListener);
        default_layout = (LinearLayout)findViewById(R.id.default_background);
        nondefault_layout = (LinearLayout)findViewById(R.id.nondefault_background);
        brand_layout = (LinearLayout)findViewById(R.id.brand_layout);
        btn_complete_delete = (ImageView) findViewById(R.id.btn_complete_delete);
        btn_complete_delete.setOnClickListener(btnClickListener);
        spinner = (Spinner)findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(spinnerClickListener);
        btn_brandwish = (ImageView) findViewById(R.id.btn_brandwish);
        btn_brandwish.setOnClickListener(brandwishClickListener);

        layout_btn_complete_delete = (LinearLayout)findViewById(R.id.layout_btn_complete_delete);

        GRID_DATA = new ArrayList<WishList>();
        init_GRID_DATA = new ArrayList<WishList>();
        deletewish = new ArrayList<WishList>();
        adapter = new CustomGridAdapter(getApplicationContext(), GRID_DATA);

        if( brandId == -1) {

            /** 전체 위시리스트 불러오기 **/
            Call<ArrayList<WishList>> getWishlistAll = service.getWishlistAll(ApplicationController.user_id);
            getWishlistAll.enqueue(new Callback<ArrayList<WishList>>() {
                @Override
                public void onResponse(Call<ArrayList<WishList>> call, Response<ArrayList<WishList>> response) {
                    if (response.isSuccessful()) {
                        Log.d("WISHLIST", "response.code() : " + response.code());
//                        Log.d("wishlist result", "all : " + response.body().get(1).getWish_title());

                        price = 0;
                        for (int i = 0; i < response.body().size(); i++) {
                            int wish_id = response.body().get(i).getWish_id();
                            String wish_title = response.body().get(i).getWish_title();
                            int wish_price = response.body().get(i).getWish_price();
                            String wish_image = response.body().get(i).getWish_image();
                            int brand_id = response.body().get(i).getBrand_id();
                            int pro_id = response.body().get(i).getPro_id();
                            String wish_memo = response.body().get(i).getWish_memo();

                            WishList wishlist = new WishList(wish_id, ApplicationController.user_id, brand_id, pro_id, wish_title, wish_price, wish_image, wish_memo);
                            GRID_DATA.add(wishlist);
                            price += wishlist.getWish_price();
                            Log.i("grid_data size", String.valueOf(GRID_DATA.size()));
                            init_GRID_DATA.add(wishlist);



                            Log.i("grid_data size",String.valueOf(GRID_DATA.size()));
                            if(GRID_DATA.size() !=0){
                                default_layout.setVisibility(View.GONE);
                                nondefault_layout.setVisibility(View.VISIBLE);
                            }else{
                                default_layout.setVisibility(View.VISIBLE);
                                nondefault_layout.setVisibility(View.GONE);
                            }

                            brand_layout.setVisibility(View.GONE);

                            //    brand_layout.setVisibility(View.VISIBLE);


                            adapter = new CustomGridAdapter( getApplicationContext(), GRID_DATA );
                            gridView.setAdapter(adapter);

                        }
                        tv_totalmoney.setText(String.valueOf(price));
                    } else {
                        Log.i("FAILED", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<WishList>> call, Throwable t) {

                }
            });
        }
        else {
            /** 브랜드별 위시리스트 요청
             * @GET("/wishlists/load2/{user_id}/{brand_id}")
            Call<WishList> getWishlist_brand(@Path("user_id") String user_id,@Path("brand_id") int brand_id);**/
            Call<ArrayList<WishList>> getWishlist_brand = service.getWishlist_brand(ApplicationController.user_id, brandId);
            getWishlist_brand.enqueue(new Callback<ArrayList<WishList>>() {
                @Override
                public void onResponse(Call<ArrayList<WishList>> call, Response<ArrayList<WishList>> response) {
                    if (response.isSuccessful()) {
//                        Log.d("WISHLIST", "response.code() : " + response.code());
//                        Log.d("wishlist result", "all : " + response.body().get(1).getWish_title());
                        price = 0;
                        if (response.body().size() == 0){
                            default_layout.setVisibility(View.VISIBLE);
                            nondefault_layout.setVisibility(View.GONE);
                        } else {
                            for (int i = 0; i < response.body().size(); i++) {
                                int wish_id = response.body().get(i).getWish_id();
                                String wish_title = response.body().get(i).getWish_title();
                                int wish_price = response.body().get(i).getWish_price();
                                String wish_image = response.body().get(i).getWish_image();
                                int brand_id = response.body().get(i).getBrand_id();
                                int pro_id = response.body().get(i).getPro_id();
                                String wish_memo = response.body().get(i).getWish_memo();

                                WishList wishlist = new WishList(wish_id, ApplicationController.user_id, brand_id, pro_id, wish_title, wish_price, wish_image, wish_memo);
                                GRID_DATA.add(wishlist);
                                price += wishlist.getWish_price();
                                Log.i("grid_data size", String.valueOf(GRID_DATA.size()));
                                init_GRID_DATA.add(wishlist);

                                Log.i("grid_data size", String.valueOf(GRID_DATA.size()));
                                if (GRID_DATA.size() != 0) {
                                    default_layout.setVisibility(View.GONE);
                                    nondefault_layout.setVisibility(View.VISIBLE);
                                } else {
                                    default_layout.setVisibility(View.VISIBLE);
                                    nondefault_layout.setVisibility(View.GONE);
                                }

//                            brand_layout.setVisibility(View.GONE);

                                brand_layout.setVisibility(View.VISIBLE);


                                adapter = new CustomGridAdapter(getApplicationContext(), GRID_DATA);
                                gridView.setAdapter(adapter);

                            }
                            tv_totalmoney.setText(String.valueOf(price));
                        }
                    } else {
                        Log.i("FAILED", response.message());
                    }

                }

                @Override
                public void onFailure(Call<ArrayList<WishList>> call, Throwable t) {

                }
            });
        }

        String [] spinnerarray = {"등록 순", "가격 낮은 순", "가격 높은 순"};

        ArrayAdapter<String>  spinneradapter =  new ArrayAdapter<String>(this,
                R.layout.spinner_item , spinnerarray);
        spinneradapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinneradapter);
    }

    GridView.OnItemClickListener gridClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb_grid);

            if (cb.getVisibility() == View.VISIBLE) {
                if(!cb.isChecked())
                    deletewish.add(GRID_DATA.get(position));
                else
                    deletewish.remove(GRID_DATA.get(position));

                cb.setChecked(!cb.isChecked());
            } else {
                /*** 세부 목록으로 이동 ***/
                Intent intent = new Intent(WishlistActivity.this, RegisterComplete.class);
//                    intent.putExtra("itemData", GRID_DATA.get(position));
                intent.putExtra("wish_id", GRID_DATA.get(position).getWish_id());
                intent.putExtra("brand_id", GRID_DATA.get(position).getBrand_id());
                startActivity(intent);
            }
        }
    };

    GridView.OnItemLongClickListener gridLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            deletewish.add(GRID_DATA.get(position));
            //Button Visible
            btn_complete_delete.setVisibility(View.VISIBLE);
            layout_btn_complete_delete.setVisibility(View.VISIBLE);

            //CheckBox Visible
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb_grid);
            cb.setChecked(true);
            adapter.getItem(position).isCheck = true;

            adapter.isLong = true;
            adapter.notifyDataSetChanged();
            return true;
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
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            logoutDialog.dismiss();
        }
    };

    Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for(int i=0; i<deletewish.size(); i++) {
                Call<Object> deleteWishList = service.deleteWishList(deletewish.get(i).getWish_id());
                deleteWishList.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            Log.d("DELETE", "response.code() : " + response.code());
                        } else {
                            Log.i("FAILED", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                    }
                });
            }
            Toast.makeText(getApplicationContext(), "구매완료.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WishlistActivity.this, WishlistActivity.class);
            startActivity(intent);
        }
    };

    Spinner.OnItemSelectedListener spinnerClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if(position ==0){
                adapter.setCustomGridAdpater(init_GRID_DATA);
                adapter.notifyDataSetChanged();
            }else if(position ==1){
                Collections.sort(GRID_DATA, new Comparator<WishList>() {
                    @Override
                    public int compare(WishList o1, WishList o2) {
                        if(o1.getWish_price() < o2.getWish_price()) {
                            return -1;
                        }else if(o1.getWish_price() == o2.getWish_price()){
                            return 0;
                        }else{
                            return 1;
                        }
                    }
                });
                adapter.setCustomGridAdpater(GRID_DATA);
                adapter.notifyDataSetChanged();
            }else{
                Collections.sort(GRID_DATA, new Comparator<WishList>() {
                    @Override
                    public int compare(WishList o1, WishList o2) {
                        if(o1.getWish_price() > o2.getWish_price()) {
                            return -1;
                        }else if(o1.getWish_price() == o2.getWish_price()){
                            return 0;
                        }else{
                            return 1;
                        }
                    }
                });
                adapter.setCustomGridAdpater(GRID_DATA);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    ImageView.OnClickListener brandwishClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(WishlistActivity.this, WishlistBrand.class);
            startActivity(intent);
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
        } else if(adapter.isLong) {
            //Button Visible
            btn_complete_delete.setVisibility(View.GONE);
            layout_btn_complete_delete.setVisibility(View.GONE);
            adapter.isLong = false;
            adapter.notifyDataSetChanged();
            deletewish.clear();
        }else {
            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wishlist, menu);
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


    public void top_click12(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home12:
                Intent top_intent5 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent5);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish12:
                // wishlist
                break;
            case R.id.btn_up_mapall12:
                ApplicationController.mapAll = true;
                Intent top_intent2 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar12:
                Intent top_intent3 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more12:
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