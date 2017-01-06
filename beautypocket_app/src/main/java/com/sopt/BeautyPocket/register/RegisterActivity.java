package com.sopt.BeautyPocket.register;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.Session;
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
import com.sopt.BeautyPocket.model.Brand;
import com.sopt.BeautyPocket.model.Innisfree;
import com.sopt.BeautyPocket.model.WishId;
import com.sopt.BeautyPocket.model.WishList;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int REQ_CODE_PICK_GALLERY = 0;
    private final int REQ_CODE_PICK_CAMERA = 1;
    private final int REQ_CODE_PICK_CROP = 2;

    /** 네비게이션 드로어 **/
    LinearLayout btn_logout;
    LinearLayout nav_home;
    LinearLayout nav_wishlist;
    LinearLayout nav_notice;
    LinearLayout nav_customer;
    LinearLayout nav_setting;

    //위치알람버튼
    ImageView btn_alarm;

    //brand id
    int brandId = 0;
    int proId;
    int wish_id;
    boolean research_register = false;
    ArrayList<Integer> resultitem;
    ArrayList<WishList> searchList;

    Uri data;
    String imgUrl = "";

    /***
     * View
     ***/

    CustomDialog photoDialog;
    //Visible, InVisible Layout
    LinearLayout input_Layout;
    LinearLayout research_Layout;
    LinearLayout layout_null;
    //검색 창과 버튼 View
    EditText et_filter;
    ImageView img_searchbutton;
    //정보 직접 입력 View
    EditText et_productname;
    EditText et_price;
    EditText et_memo;
    ImageView img_register;
    ImageView img_register_click;

    /**
     * 네트워크
     **/
    NetworkService service;

    /***
     * RecyclerView and 내장 DB
     ***/
    //DB and RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    Adapter adapter;
    ArrayList<WishList> itemDatas = null;
    WishList registerDatas = null;

    /***
     * 기타 세부사항
     ***/
    //ProgressDialog
    private ProgressDialog mProgressDialog;

    boolean setimg = false;

    ImageView btn_setting;

    TextView tv_pocketcount;
    TextView tv_myname;

    //위시갯수 갱신변수
    Brand brand_num;

    CustomDialog registerDialog, logoutDialog, backDialog;
    LinearLayout back;

    String pro_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //위치알람
        btn_alarm = (ImageView) findViewById(R.id.btn_alarm);

        //위시갯수
        tv_pocketcount = (TextView) findViewById(R.id.tv_pocketcount);
        //네비게이션뷰
        tv_myname = (TextView) findViewById(R.id.tv_myname);
        tv_myname.setText(ApplicationController.user_name);
        tv_pocketcount.setText(String.valueOf(ApplicationController.count));
        navigationView.setNavigationItemSelectedListener(this);

        back = (LinearLayout)findViewById(R.id.backlayout);
        //브랜드 아이디 받아오기
        Intent intent = getIntent();
        brandId = intent.getIntExtra("brand_id", -1);

        Log.i("Mytag", String.valueOf(brandId));

        ImageView imgview = (ImageView) findViewById(R.id.img_brand_inputimg);
        imgview.setImageResource(ApplicationController.itemDataList3.get(brandId).getIcon());
        layout_null = (LinearLayout)findViewById(R.id.layout_null);

        //WishList
        itemDatas = new ArrayList<WishList>();

        //RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        /*** 일단 비어있는 itemData를 set한다 ***/
        //Set Adpater
        adapter = new Adapter(itemDatas, clickEvent, getApplicationContext());
        recyclerView.setAdapter(adapter);

        //findViewById
        input_Layout = (LinearLayout) findViewById(R.id.input_layout);
        research_Layout = (LinearLayout) findViewById(R.id.research_Layout);
        et_filter = (EditText) findViewById(R.id.et_filter);
        img_searchbutton = (ImageView) findViewById(R.id.img_searchbutton);
        et_productname = (EditText) findViewById(R.id.et_productname);
        et_price = (EditText) findViewById(R.id.et_price);
        et_memo = (EditText) findViewById(R.id.et_memo);
        img_register = (ImageView) findViewById(R.id.img_register);
        img_register_click = (ImageView) findViewById(R.id.img_register_click);

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
                logoutDialog = new CustomDialog(RegisterActivity.this, logoutCancelListener, logoutOkListener);
                logoutDialog.show();
                logoutDialog.setTitle("로그아웃");
                logoutDialog.setContent("로그아웃 하시겠습니까?");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_productname.getWindowToken(), 0);
            }
        });


        // ProgressDialog
        mProgressDialog = new ProgressDialog(RegisterActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("등록 중...");
        mProgressDialog.setIndeterminate(true);

        //네트워크
        service = ApplicationController.getInstance().getNetworkService();

        //View Listener Register
        img_searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ArrayList<Innisfree>> getInnisfree = service.getInnisfree(et_filter.getText().toString(), brandId);

                getInnisfree.enqueue(new Callback<ArrayList<Innisfree>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Innisfree>> call, Response<ArrayList<Innisfree>> response) {
                        if (response.isSuccessful()) {
                            Log.d("SEARCH", "response.code() : " + response.code());
//                            Log.d("search result", "innisfree : " + response.body().get(1).getPro_name());
                            searchList = new ArrayList<WishList>();

                            for (int i = 0; i < response.body().size(); i++) {
                                int search_id = response.body().get(i).getPro_id();
                                String search_name = response.body().get(i).getPro_name();
                                int search_price = response.body().get(i).getPro_price();
                                String search_image = response.body().get(i).getPro_image();
                                String search_url = response.body().get(i).getPro_url();

                                WishList search = new WishList(search_url,search_id, search_name, search_price, search_image);
                                searchList.add(search);
                            }
//                            Log.d("search array", "ㅎㅎㅎ" + searchList.get(1).getPro_name());
//                            Log.d("search array", "zzzzzzㅋㅋ" + searchList.get(1).getPro_price());
//                            Log.d("search array", "zzzzzzㅋㅋ" + searchList.get(1).getPro_image());
//                            Log.d("search array", "zzzzzzㅋㅋ" + searchList.get(1).getBrand_id());
//                            Log.d("search array", "zzzzzzㅋㅋ" + searchList.get(1).getWish_memo());

                            adapter.setAdaper(searchList);
                            adapter.PrintMethod();
                            if(searchList.size() == 0) {
                                layout_null.setVisibility(View.VISIBLE);
                            } else
                                layout_null.setVisibility(View.GONE);

                        } else {
                            Log.i("HJTAG", response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<Innisfree>> call, Throwable t) {
                        Log.d("실패원인", t.getMessage());
                    }
                });
            }
        });


        et_filter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //끝
                et_filter.requestFocus();

                input_Layout.setVisibility(View.INVISIBLE);
                research_Layout.setVisibility(View.VISIBLE);


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                return true;
            }
        });

        img_register_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerDialog = new CustomDialog(RegisterActivity.this, registerCancelListener, registerOkListener);
                registerDialog.show();
                registerDialog.setTitle("등록");
                registerDialog.setContent("등록하시겠습니까?");
            }
        });

        img_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialog = new CustomDialog(RegisterActivity.this, photoListener);
                photoDialog.show();
                photoDialog.cancelInVisible();
                photoDialog.setTitle("사진선택");
                photoDialog.setContent("사진을 선택해주세요.");
            }

        });


    }

    private View.OnClickListener photoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doTakeAlbumAction();
            photoDialog.dismiss();

        }
    };

    private View.OnClickListener backCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            backDialog.dismiss();
        }
    };

    private View.OnClickListener backOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            backDialog.dismiss();
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
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            logoutDialog.dismiss();
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
                    Log.d("카운트값", String.valueOf(brand_num.getCount()));
                    Log.d("변화1", String.valueOf(ApplicationController.count));
                    tv_pocketcount.setText(String.valueOf(ApplicationController.count));
                }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {

            }
        });
    }

    public void intentCall() {
        Intent intent = new Intent(RegisterActivity.this, RegisterComplete.class);
        intent.putExtra("wish_id", wish_id);
        intent.putExtra("brand_id", brandId);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private View.OnClickListener registerCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerDialog.dismiss();
        }
    };

    private View.OnClickListener registerOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (et_productname.length() == 0 || et_price.length() == 0) {
                Toast.makeText(getApplicationContext(), "제품명 및 가격을 확인해주세요.", Toast.LENGTH_SHORT).show();
            } else if (setimg == false&& proId == 0) {
                Toast.makeText(getApplicationContext(), "사진을 첨부해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                mProgressDialog.show();

                /*** NetWork 등록 후 해당 WishList의 id를 response 받음***/
//                검색해서 등록하는 경우
                if (research_register) {
                    Log.d("ddd","ssssssss"+proId);
                    WishList wishList = new WishList(ApplicationController.user_id, proId, brandId, et_memo.getText().toString(),pro_url);
                    Call<WishList> add_searchWishList = service.add_searchWishList(wishList);
                    add_searchWishList.enqueue(new Callback<WishList>() {
                        @Override
                        public void onResponse(Call<WishList> call, Response<WishList> response) {
                            if(response.isSuccessful()) {
                                WishList wishlist;
                                wishlist = response.body();
                                wish_id = wishlist.getWish_id();
                                Log.i("검색 wish_id", String.valueOf(wish_id));
                                finish();
                                mProgressDialog.dismiss();
                                intentCall();
                            }
                            else {
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<WishList> call, Throwable t) {
                            mProgressDialog.dismiss();
                        }
                    });

//                        직접 작성해서 등록하는 경우
                } else {

                    RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), ApplicationController.user_id);
                    RequestBody brand_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(brandId));
                    RequestBody wish_memo = RequestBody.create(MediaType.parse("multipart/form-data"), et_memo.getText().toString());
                    RequestBody wish_title = RequestBody.create(MediaType.parse("multipart/form-data"), et_productname.getText().toString());
                    RequestBody wish_price = RequestBody.create(MediaType.parse("multipart/form-data"), et_price.getText().toString());

                    MultipartBody.Part body;
                    if(imgUrl==""){
                        body = null;
                    }
                    else {
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        InputStream in = null;
                        try {
                            in = getContentResolver().openInputStream(data);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                        // InputStream 으로부터 Bitmap 을 만들어 준다.
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos); // 압축 옵션( JPEG, PNG ) , 품질 설정 ( 0 - 100까지의 int형 ),
                        // 사진이 100mb이고 파라메타에 20 쓰면 5분의 1정도 줄이겠다는 뚯

                        RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpg"), baos.toByteArray());

                        File photo = new File(imgUrl); // 그저 블러온 파일의 이름을 알아내려고 사용..

                        // MultipartBody.Part is used to send also the actual file name
                        body = MultipartBody.Part.createFormData("wish_image", photo.getName(), photoBody); // 두번째 파라메타는 신경 안써도됨
                        //서버쪽에서 요청할때 타이틀,콘텐트,이미지를 보내달랫져


                        Log.i("YOONYOUNG photo getname",photo.getName());
                        Log.i("YOONYOUNG imgurl",imgUrl);
                    }

                    // 직접 사진 추가
                    Call<WishList> add_wishList = service.add_wishList(body, user_id, brand_id, wish_title, wish_price, wish_memo);
                    add_wishList.enqueue(new Callback<WishList>() {
                        @Override
                        public void onResponse(Call<WishList> call, Response<WishList> response) {
                            if(response.isSuccessful()) {
                                Log.d("YOONYOUNG", "response.code() : " + response.code());
                                Log.d("YOONYOUNG", "response.body() : " + response.body());
                                WishList wishlist = new WishList(response.body().getWish_id(),ApplicationController.user_id, brandId, response.body().getPro_id(),response.body().getWish_title(), response.body().getWish_price(), response.body().getWish_image(),response.body().getWish_memo());
                                wish_id = wishlist.getWish_id();
                                finish();
                                mProgressDialog.dismiss();
                                intentCall();

                                Log.i("pro_id", String.valueOf(response.body().getPro_id()));
                                Log.i("pro_id" , String.valueOf(wishlist.getPro_id()));
                                Log.i("wish_id" , String.valueOf(wishlist.getWish_id()));
                                Log.i("wish_title" , String.valueOf(wishlist.getWish_title()));
                                Log.i("wish_price" , String.valueOf(wishlist.getWish_price()));
                                Log.i("wish_memo" , String.valueOf(wishlist.getWish_memo()));

                            }
                            else
                                Log.i("왜안되냐고니ㅏㅇ릐ㅏㄷㅂ",response.message());
                        }

                        @Override
                        public void onFailure(Call<WishList> call, Throwable t) {
                            mProgressDialog.dismiss();
                        }
                    });
                }

            }
            registerDialog.dismiss();
        }
    };

    public View.OnClickListener clickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int itemPosition = recyclerView.getChildPosition(v);
            resultitem = adapter.getResult();

            for (int i = 0; i < resultitem.size(); i++) {
                if (itemPosition == resultitem.get(i)) {
                    registerDatas = searchList.get(itemPosition);
                }
            }
            Log.d("ddd", "ddd" + registerDatas.getPro_id());
            proId = registerDatas.getPro_id();
            et_filter.setText("");
            input_Layout.setVisibility(View.VISIBLE);
            research_Layout.setVisibility(View.INVISIBLE);
            layout_null.setVisibility(View.GONE);

            et_productname.setText(registerDatas.getPro_name());
            et_price.setText(String.valueOf(registerDatas.getPro_price()));
            Glide.with(getApplicationContext())
                    .load(registerDatas.getPro_image())
                    .into(img_register);

            /*** 검색을 하였을 때 비활성화 ***/
            et_productname.setEnabled(false);
            et_price.setEnabled(false);
            img_register.setEnabled(false);
            research_register = true;
        }
    };

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (input_Layout.getVisibility() == View.INVISIBLE && research_Layout.getVisibility() == View.VISIBLE) {
            et_filter.setText("");
            adapter.clear();

            /*** Network 연결 시 쓰이지 않은 메소드 ***/
//            adapter.filter("");
            /*** Network 연결 시 쓰일 메소드***/

            input_Layout.setVisibility(View.VISIBLE);
            research_Layout.setVisibility(View.INVISIBLE);
            layout_null.setVisibility(View.GONE);
        } else {
            backDialog = new CustomDialog(RegisterActivity.this, backCancelListener, backOkListener);
            backDialog.show();
            backDialog.setTitle("등록취소");
            backDialog.setContent("등록을 취소하시겠습니까?");
        }
    }

    /***
     * 쓰이지 않는 검색 필터기능
     ***/
    public void Filter_Input_Listener() {
        et_filter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_filter.requestFocus();

                input_Layout.setVisibility(View.INVISIBLE);
                research_Layout.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                return true;
            }
        });

        et_filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String text = et_filter.getText().toString().toLowerCase(Locale.getDefault());
//                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemDatas.clear();
    }

    public void doTakeAlbumAction() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(MediaStore.Images.Media.CONTENT_TYPE);
        i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQ_CODE_PICK_GALLERY);
    }

    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempImageFile()));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        } else if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
            // 갤러리의 경우 곧바로 data 에 uri가 넘어옴.
            Uri uri = data.getData();
            this.data = data.getData();
            String name_Str = getImageNameToUri(data.getData());
            Log.d("name_str", name_Str);
            copyUriToFile(uri, getTempImageFile());
            cropImage();
            doFinalProcess();
        } else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
            // 카메라의 경우 file 로 결과물이 돌아옴.
            // 카메라 회전 보정.
            cropImage();
            doFinalProcess();
        } else if (requestCode == REQ_CODE_PICK_CROP && resultCode == Activity.RESULT_OK) {
            // crop 한 결과는 file로 돌아옴.
            doFinalProcess();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        imgUrl = imgPath;
        Log.d("imgUrl", imgUrl);

        return imgName;
    }

    private File getTempImageFile() {
        File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/temp/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, "tempimage.jpg");
        this.data = Uri.fromFile(file);
        return file;
    }

    private void copyUriToFile(Uri srcUri, File target) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            // 스트림 생성
            inputStream = (FileInputStream) getContentResolver().openInputStream(srcUri);
            outputStream = new FileOutputStream(target);

            // 채널 생성
            fcin = inputStream.getChannel();
            fcout = outputStream.getChannel();

            // 채널을 통한 스트림 전송
            long size = fcin.size();
            fcin.transferTo(0, size, fcout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fcout.close();
            } catch (IOException ioe) {
            }
            try {
                fcin.close();
            } catch (IOException ioe) {
            }
            try {
                outputStream.close();
            } catch (IOException ioe) {
            }
            try {
                inputStream.close();
            } catch (IOException ioe) {
            }
        }
    }

    private void cropImage() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> cropToolLists = getPackageManager().queryIntentActivities(intent, 0);
        int size = cropToolLists.size();
        if (size == 0) {
            // crop 을 처리할 앱이 없음. 곧바로 처리.
            doFinalProcess();
        } else {
            intent.setData(Uri.fromFile(getTempImageFile()));
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("output", Uri.fromFile(getTempImageFile()));
            Log.i("YOONYOUNG camera", this.data.toString());
            Intent i = new Intent(intent);
            ResolveInfo res = cropToolLists.get(0);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, REQ_CODE_PICK_CROP);
        }
    }

    private void doFinalProcess() {
        // sample size 를 적용하여 bitmap load.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath(), options);
        int width = options.outWidth;
        int height = options.outHeight;
        int longSide = Math.max(width, height);
        int sampleSize = 1;
        if (longSide > 500) {
            sampleSize = longSide / 500;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inPurgeable = true;
        options.inDither = false;

        Bitmap bitmap = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath(), options);

        // image boundary size 에 맞도록 이미지 축소.
        if (bitmap.getWidth() > bitmap.getHeight()) {
            if (bitmap.getWidth() > 500) {
                bitmap = resizeBitmapWithWidth(bitmap, 500);
            }
        } else {
            if (bitmap.getHeight() > 500) {
                bitmap = resizeBitmapWithHeight(bitmap, 500);
            }
        }

        // 결과 file 을 얻어갈 수 있는 메서드 제공.
        saveBitmapToFile(bitmap);

        // show image on ImageView
        Bitmap bm = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath());
        ((ImageView) findViewById(R.id.img_register)).setImageBitmap(bm);
        setimg = true;
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        File target = getTempImageFile();
        try {
            FileOutputStream fos = new FileOutputStream(target, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap resizeBitmapWithHeight(Bitmap source, int wantedHeight) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedHeight * 1f / height;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
    }

    private Bitmap resizeBitmapWithWidth(Bitmap source, int wantedWidth) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedWidth * 1f / width;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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

    public void top_click9(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home9:
                Intent top_intent1 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish9:
                Intent top_intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall9:
                ApplicationController.mapAll = true;
                Intent top_intent3 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar9:
                Intent top_intent4 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent4);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more9:
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