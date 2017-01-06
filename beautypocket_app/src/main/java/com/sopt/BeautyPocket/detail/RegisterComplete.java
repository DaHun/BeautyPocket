package com.sopt.BeautyPocket.detail;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.Session;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.sopt.BeautyPocket.CustomDialog;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.calender.SaleCalendarActivity;
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.map.LocationService;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.model.Brand;
import com.sopt.BeautyPocket.model.WishList;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterComplete extends AppCompatActivity
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

    /***
     * 해당 액티비티가 가지고 있어야 할 itemData
     **/
    int wish_id;
    int pro_id;
    int brand_id;

    static final int[] TEXT = {R.id.tv_productname, R.id.tv_price, R.id.tv_memo};
    Button btn_show_brand;
    ImageView btn_ok;

    String name;
    int price;
    String memo;
    String img;
    //4가지 Button의 Reference, TextView
    Button btn;
    TextView[] textview = new TextView[3];
    ImageView img_complete;
    ImageView img_brand_inputimg3;
    ImageView imageView4;

    /**
     * 네트워크
     **/
    NetworkService service;
    WishList wishList;

    TextView tv_pocketcount;
    TextView tv_myname;

    //위시갯수 갱신변수
    Brand brand_num;

    CustomDialog dialog, deleteDialog;

    ImageView btn_shop,btn_kakao;

    String pro_url;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_register_complete);
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
        btn_shop = (ImageView)findViewById(R.id.btn_shop);
        btn_kakao = (ImageView)findViewById(R.id.btn_kakao);

        //위치알람
        imageView4 = (ImageView)findViewById(R.id.imageView4);
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



        final Intent intent = getIntent();
        wish_id = intent.getIntExtra("wish_id", -1);
        brand_id = intent.getIntExtra("brand_id", -1);

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),WishlistActivity.class);
                intent.putExtra("brand_id",brand_id);
                startActivity(intent);
                finish();

            }
        });

        ImageView imgview = (ImageView) findViewById(R.id.img_brand_inputimg3);
        imgview.setImageResource(ApplicationController.itemDataList3.get(brand_id).getIcon());

//        btn_show_brand = (Button) findViewById(R.id.btn_show_brand);
        btn_ok = (ImageView) findViewById(R.id.btn_ok);

        textview[0] = (TextView) findViewById(TEXT[0]);
        textview[1] = (TextView) findViewById(TEXT[1]);
        textview[2] = (TextView) findViewById(TEXT[2]);
        img_complete = (ImageView) findViewById(R.id.img_complete);
        btn_logout = (LinearLayout) findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new CustomDialog(getApplicationContext(), logoutCancelListener, logoutOkListener);
                dialog.show();
                dialog.setTitle("로그아웃");
                dialog.setContent("로그아웃 하시겠습니까?");
            }
        });


        service = ApplicationController.getInstance().getNetworkService();
        getBrandList();

        /*@GET("/wishlists/specific/{wish_id]")
        Call<WishList> getWishlist(@Path("wish_id") int wish_id);*/

        Call<WishList> getWishlist = service.getWishlist(wish_id);
        getWishlist.enqueue(new Callback<WishList>() {
            @Override
            public void onResponse(Call<WishList> call, Response<WishList> response) {
                if (response.isSuccessful()) {
                    Log.d("WISHLIST 상세보기", "response.code() : " + response.code());
                    Log.d("wishlist result", "specific : " + response.body().getWish_title());

                    name = response.body().getWish_title();
                    price = response.body().getWish_price();
                    memo = response.body().getWish_memo();
                    img = response.body().getWish_image();
                    pro_url = response.body().getPro_url();

//                    검색해서 등록한 제품인지 직접 입력한 제품인지 판별하기위한 pro_id
                    pro_id = response.body().getPro_id();

                    wishList = new WishList(brand_id, pro_id, wish_id, name, price, memo, img);

                    Log.i("name", response.body().getWish_title());
                    Log.i("price", String.valueOf(response.body().getWish_price()));
//                    Log.i("memo", response.body().getWish_memo());
                    Log.i("img", response.body().getWish_image());

                    Glide.with(getApplicationContext())
                            .load(img)
                            .into(img_complete);

                    textview[0].setText(name);
                    textview[1].setText(String.valueOf(price));
                    textview[2].setText(memo);
                } else {
                    Log.i("FAILED", response.message());
                }
            }

            @Override
            public void onFailure(Call<WishList> call, Throwable t) {

            }
        });
        /*** NetWork를 wish_id를 통해 받아온다.
         *
         * return WishList;
         * ***/

        /*** pro_id도 알고 있어야 한다. ***/

        /***
         String name= WishList.getWish_title();
         String price= itemData.getWish_price();
         String memo= itemData.getWish_memo();
         String img= itemData.getWish_image();
         ***/
        //temp
        /*String name= "name";
        String price= "price";
        String memo= "memo";
        String img= "img";*/


        /***img 도 등록***/

        /*** Show Brand Btn ***/
//        btn_show_brand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //브랜드 내 다른 다른 제품 보기
//            }
//        });
        btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), WebViewActivity.class);
                intent1.putExtra("url",pro_url);
                startActivity(intent1);
            }
        });
        /*** Ok Btn ***/
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterComplete.this, WishlistActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }
    public void sendMessage(){
        try{

            final KakaoLink kakaoLink=KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoBuilder=kakaoLink.createKakaoTalkLinkMessageBuilder();

            kakaoBuilder.addText("제품 이름 : "+name+"\n"
                    +"가 격 : "+price+"\n"
                    +"링 크 : "+pro_url);


            //String url="https://sopt-hj.s3.ap-northeast-2.amazonaws.com/1483537840489.png";
            kakaoBuilder.addImage(img, 400, 400);

            kakaoBuilder.addAppButton("앱 실행");

            kakaoBuilder.build();
//                    getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            kakaoLink.sendMessage(kakaoBuilder,this);

        }catch(KakaoParameterException e){
            e.printStackTrace();
        }
    }

    private View.OnClickListener logoutCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
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
            dialog.dismiss();
        }
    };
    private void getBrandList() {
        //메인 브랜드 리스트
        final Call<Brand> get_brandList = service.get_brandList(ApplicationController.user_id);
        get_brandList.enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                if (response.isSuccessful()) {
                    Log.i("지현로그", "" + response.code());
                    ArrayList<Brand> brands = new ArrayList<Brand>();
                    brand_num = new Brand(brands, 0, 0);
                    brand_num = response.body();
                    /*Log.d("브랜드갯수", String.valueOf(brand_num.getBrand().get(0).getBrand_id()));*/
                    //위시 총갯수와, 위시 가격 총 합 컨트롤러에 저장하기
                    ApplicationController.getInstance().count = brand_num.getCount();
                    tv_pocketcount.setText(String.valueOf(ApplicationController.count));
                }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                Log.d("실패원인", t.getMessage());
            }
        });
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
        getMenuInflater().inflate(R.menu.register_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_modify) {
            Intent intent = new Intent(RegisterComplete.this, UpdateInformation.class);
            intent.putExtra("wishList", wishList);
            startActivity(intent);
        } else if (id == R.id.action_delete) {
            deleteDialog = new CustomDialog(RegisterComplete.this, deleteCancelListener, deleteOkListener);
            deleteDialog.show();
            deleteDialog.setTitle("삭제");
            deleteDialog.setContent("삭제하시겠습니까?");
        }

        return super.onOptionsItemSelected(item);
    }
    private View.OnClickListener deleteCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteDialog.dismiss();
        }
    };

    private View.OnClickListener deleteOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Call<Object> deleteWishList = service.deleteWishList(wish_id);
            deleteWishList.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Log.d("DELETE", "response.code() : " + response.code());
                        Toast.makeText(getApplicationContext(), "삭제성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterComplete.this, WishlistActivity.class);
                        startActivity(intent);
                    } else {
                        Log.i("FAILED", response.message());
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });
            Intent intent = new Intent(RegisterComplete.this, WishlistActivity.class);
            startActivity(intent);
        }
    };

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

    public void top_click10(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home10:
                Intent top_intent1 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish10:
                Intent top_intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall10:
                ApplicationController.mapAll = true;
                Intent top_intent3 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar10:
                Intent top_intent4 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent4);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more10:
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