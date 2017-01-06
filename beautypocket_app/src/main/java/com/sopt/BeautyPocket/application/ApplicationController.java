package com.sopt.BeautyPocket.application;

import android.app.Activity;
import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.auth.KakaoSDK;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.login.KakaoSDKAdapter;
import com.sopt.BeautyPocket.main.ItemData;
import com.sopt.BeautyPocket.model.Store;
import com.sopt.BeautyPocket.network.NetworkService;

import java.sql.SQLException;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 이미지를 캐시를 앱 수준에서 관리하기 위한 애플리케이션 객체이다.
 * 로그인 기반 샘플앱에서 사용한다.
 *
 * @author MJ
 */
public class ApplicationController extends Application {
    private static volatile ApplicationController instance = null;
    private static volatile Activity currentActivity = null;
    /*** 일단 주석처리 ***/
    //public DbOpenHelper mDbOpenHelper;
    public static String user_id;
    public static String user_name;
    //브랜드 위시리스트 로고아이템
    public static ArrayList<ItemData> itemDataList;
    public static ArrayList<ItemData> itemDataList2;
    public static ArrayList<ItemData> itemDataList3;
    //나의 위시리스트 갯수, 위시리스트 가격 총 합
    public static int count;
    public static int total;
    //위치알람 브로드캐스트리시버가 받을 정보
    public static Store nearStore;
    //통합지도인지 브랜드별지도 구별 변수
    public static Boolean mapAll;


    //서버 유알엘
    private static String baseUrl = "http://52.78.205.45:3000";

    private static NetworkService networkService;
    public NetworkService getNetworkService() {
        return networkService;
    }

    public static ApplicationController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSDK.init(new KakaoSDKAdapter());
        this.buildDB();
        // TODO: 2016. 11. 21. 어플리케이션 초기 실행 시, retrofit을 사전에 build한다.
        buildService();

    }

    public void buildDB() {
        // DB Create and Open
        /*** 일단 주석처리 ***/
        /*mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        nearStore = new Store();
        itemDataList = new ArrayList<ItemData>();
        itemDataList.add(new ItemData(0, R.drawable.brand001, "이니스프리"));
        itemDataList.add(new ItemData(1, R.drawable.brand002, "네이처리퍼블릭"));
        itemDataList.add(new ItemData(2, R.drawable.brand003, "더샘"));
        itemDataList.add(new ItemData(3, R.drawable.brand004, "더페이스샵"));
        itemDataList.add(new ItemData(4, R.drawable.brand005, "미샤"));
        itemDataList.add(new ItemData(5, R.drawable.brand006, "VDL"));
        itemDataList.add(new ItemData(6, R.drawable.brand007, "스킨푸드"));
        itemDataList.add(new ItemData(7, R.drawable.brand008, "아리따움"));
        itemDataList.add(new ItemData(8, R.drawable.brand009, "어퓨"));
        itemDataList.add(new ItemData(9, R.drawable.brand010, "에뛰드하우스"));
        itemDataList.add(new ItemData(10, R.drawable.brand011, "에스쁘아"));
        itemDataList.add(new ItemData(11, R.drawable.brand012, "올리브영"));
        itemDataList.add(new ItemData(12, R.drawable.brand013, "잇츠스킨"));
        itemDataList.add(new ItemData(13, R.drawable.brand014, "토니모리"));
        itemDataList.add(new ItemData(14, R.drawable.brand015, "홀리카홀리카"));

        itemDataList2 = new ArrayList<ItemData>();
        itemDataList2.add(new ItemData(0, R.mipmap.brandlogo4, "이니스프리",R.mipmap.map_brand1));
        itemDataList2.add(new ItemData(1, R.mipmap.brandlogo2, "네이처리퍼블릭",R.mipmap.map_brand2));
        itemDataList2.add(new ItemData(2, R.mipmap.brandlogo3, "더샘",R.mipmap.map_brand3));
        itemDataList2.add(new ItemData(3, R.mipmap.brandlogo6, "더페이스샵",R.mipmap.map_brand4));
        itemDataList2.add(new ItemData(4, R.mipmap.brandlogo7, "미샤",R.mipmap.map_brand5));
        itemDataList2.add(new ItemData(5, R.mipmap.brandlogo1, "VDL",R.mipmap.map_brand6));
        itemDataList2.add(new ItemData(6, R.mipmap.brandlogo8, "스킨푸드",R.mipmap.map_brand7));
        itemDataList2.add(new ItemData(7, R.mipmap.brandlogo9, "아리따움",R.mipmap.map_brand8));
        itemDataList2.add(new ItemData(8, R.mipmap.brandlogo11, "어퓨",R.mipmap.map_brand9));
        itemDataList2.add(new ItemData(9, R.mipmap.brandlogo12, "에뛰드하우스",R.mipmap.map_brand10));
        itemDataList2.add(new ItemData(10, R.mipmap.brandlogo13, "에스쁘아",R.mipmap.map_brand11));
        itemDataList2.add(new ItemData(11, R.mipmap.brandlogo14, "올리브영",R.mipmap.map_brand12));
        itemDataList2.add(new ItemData(12, R.mipmap.brandlogo10, "잇츠스킨",R.mipmap.map_brand13));
        itemDataList2.add(new ItemData(13, R.mipmap.brandlogo5, "토니모리",R.mipmap.map_brand14));
        itemDataList2.add(new ItemData(14, R.mipmap.brandlogo15, "홀리카홀리카",R.mipmap.map_brand15));

        itemDataList3 = new ArrayList<ItemData>();
        itemDataList3.add(new ItemData(0, R.drawable.brandwishlist_logo01, "이니스프리"));
        itemDataList3.add(new ItemData(1, R.drawable.brandwishlist_logo02, "네이처리퍼블릭"));
        itemDataList3.add(new ItemData(2, R.drawable.brandwishlist_logo03, "더샘"));
        itemDataList3.add(new ItemData(3, R.drawable.brandwishlist_logo04,"더페이스샵"));
        itemDataList3.add(new ItemData(4, R.drawable.brandwishlist_logo05,"미샤"));
        itemDataList3.add(new ItemData(5, R.drawable.brandwishlist_logo06, "VDL"));
        itemDataList3.add(new ItemData(6, R.drawable.brandwishlist_logo07, "스킨푸드"));
        itemDataList3.add(new ItemData(7, R.drawable.brandwishlist_logo08, "아리따움"));
        itemDataList3.add(new ItemData(8, R.drawable.brandwishlist_logo09, "어퓨"));
        itemDataList3.add(new ItemData(9, R.drawable.brandwishlist_logo10,"에뛰드하우스"));
        itemDataList3.add(new ItemData(10, R.drawable.brandwishlist_logo11, "에스쁘아"));
        itemDataList3.add(new ItemData(11, R.drawable.brandwishlist_logo12,"올리브영"));
        itemDataList3.add(new ItemData(12, R.drawable.brandwishlist_logo13,"잇츠스킨"));
        itemDataList3.add(new ItemData(13, R.drawable.brandwishlist_logo14, "토니모리"));
        itemDataList3.add(new ItemData(14, R.drawable.brandwishlist_logo15,"홀리카홀리카"));
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        ApplicationController.currentActivity = currentActivity;
    }

    /**
     * singleton 애플리케이션 객체를 얻는다.
     * @return singleton 애플리케이션 객체
     */
    public static ApplicationController getGlobalApplicationContext() {
        if(instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
    // 레트로피부분을 사전에 빌드하도록함
    public void buildService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson)) // json 사용할꺼기때문에 json컨버터를 추가적으로 넣어준것임
                .build();
        networkService = retrofit.create(NetworkService.class);
    }
}