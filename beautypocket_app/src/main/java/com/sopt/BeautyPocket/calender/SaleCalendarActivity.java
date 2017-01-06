package com.sopt.BeautyPocket.calender;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
import com.sopt.BeautyPocket.login.LoginActivity;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.map.LocationService;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.model.CalendarSale;
import com.sopt.BeautyPocket.model.TwoDate;
import com.sopt.BeautyPocket.more.GogagCenter;
import com.sopt.BeautyPocket.more.MoreActivity;
import com.sopt.BeautyPocket.more.Notice;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SaleCalendarActivity extends AppCompatActivity
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

    ArrayList<String> day;
    private TextView tvDate;
    private SaleCalendarActivity.GridAdapter gridAdapter;
    private ArrayList<String> dayList;
    private GridView gridView;

    private Calendar mCal;

    NetworkService networkService;
    //네트워크 결과값 받아올 변수
    ArrayList<CalendarSale> calendarSaleList;

    //두 기간의 첫 날짜와 끝 날짜를 받을
    ArrayList<TwoDate> startend;
    TwoDate twoDate;

    //달력에 표시할 날짜 숫자모음
    ArrayList<ArrayList<String>> calendarCheckList;

    ArrayList<String> c;

    CalendarSaleAdapter calendarSaleAdapter;
    ListView listView;

    TextView tv_pocketcount;
    TextView tv_myname;

    //브랜드네임
    ArrayList<Integer> brandname;

    CustomDialog logoutDialog;

    BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_sale_calendar);
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
                logoutDialog = new CustomDialog(SaleCalendarActivity.this, logoutCancelListener, logoutOkListener);
                logoutDialog.show();
                logoutDialog.setTitle("로그아웃");
                logoutDialog.setContent("로그아웃 하시겠습니까?");
            }
        });

        networkService = ApplicationController.getInstance().getNetworkService();

        tvDate = (TextView) findViewById(R.id.tv_date);

        gridView = (GridView) findViewById(R.id.gridview);
        day = new ArrayList<String>();
        startend = new ArrayList<TwoDate>();
        listView = (ListView) findViewById(R.id.listview_calendar);

        brandname= new ArrayList<Integer>();


        //달력 네트워크요청
        final Call<ArrayList<CalendarSale>> getCalendarSale = networkService.getCalendarSale();
        getCalendarSale.enqueue(new Callback<ArrayList<CalendarSale>>() {
            @Override
            public void onResponse(Call<ArrayList<CalendarSale>> call, Response<ArrayList<CalendarSale>> response) {
                if (response.isSuccessful()) {
                    calendarSaleList = new ArrayList<CalendarSale>();
                    calendarSaleList = response.body();
                    for(int i = 0; i<calendarSaleList.size();i++) {
                        brandname.add(ApplicationController.itemDataList.get(calendarSaleList.get(i).getBrand_id()).get_id());
                    }
                    calendarSaleAdapter = new CalendarSaleAdapter(SaleCalendarActivity.this, R.layout.listview_item_calendar, calendarSaleList);
                    listView.setAdapter(calendarSaleAdapter);
                    calendarSaleAdapter.notifyDataSetChanged();
                    Log.d("값 갯수", String.valueOf(calendarSaleList.size()));
                    calendarCheckList = new ArrayList<ArrayList<String>>();
                    calendarFinal();
                    Log.d("달력이 성공했을까요?", "성공했습니다!");


                }

            }

            @Override
            public void onFailure(Call<ArrayList<CalendarSale>> call, Throwable t) {
                Log.d("달력이 성공했을까요?", t.getMessage());
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
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            logoutDialog.dismiss();
        }
    };

    public void calendarFinal() {
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();

        final Date date = new Date(now);

        //연,월,일을 따로 저장

        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 날짜 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(date) + "년 " + curMonthFormat.format(date)+"월의 세일 정보");
        tvDate.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);

        //gridview 요일 표시
        dayList = new ArrayList<String>();
        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        mCal = Calendar.getInstance();
        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }

        setCalendarDate(mCal.get(Calendar.MONTH) + 1);


        //네트워크를 받은 달력 알고리즘
        for (int i = 0; i < calendarSaleList.size(); i++) {
            Log.d("첫째날", calendarSaleList.get(i).getSale_day());
            Log.d("둘째날", calendarSaleList.get(i).getSale_end());
            /*twoDate.setD1(calendarSaleList.get(i).getSale_day());
            twoDate.setD2(calendarSaleList.get(i).getSale_end());
            Log.d("첫째날",twoDate.getD1());
            Log.d("둘째날",twoDate.getD2());*/

            startend.add(new TwoDate(calendarSaleList.get(i).getSale_day(), calendarSaleList.get(i).getSale_end()));
        }
        //날짜를 차이간격을 조사하여 해당하는 날짜리스트만들기
        for (int i = 0; i < startend.size(); i++) {
            int a = Integer.valueOf(startend.get(i).getD2()) - Integer.valueOf(startend.get(i).getD1());
            int b;
            c = new ArrayList<String>();
            for (int j = 0; j <= a; j++) {

                b = Integer.valueOf((startend.get(i).getD1()).substring(6, 8)) + j;
                c.add(String.valueOf(b));
                Log.d("c의 값을 봐보자", c.get(0).toString());
            }
            Log.d("값넣기전에 c의 값을 봐보자", String.valueOf(c.size()));
            calendarCheckList.add(c);
        }


        gridAdapter = new SaleCalendarActivity.GridAdapter(getApplicationContext(), dayList, calendarCheckList,brandname);

        gridView.setAdapter(gridAdapter);
    }

    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }

    }

    private class GridAdapter extends BaseAdapter {
        private final List<String> list;//원래있던거
        private final LayoutInflater inflater;
        ArrayList<ArrayList<String>> dayList;
        ArrayList<Integer> brandname;

        public GridAdapter(Context context, List<String> list, ArrayList<ArrayList<String>> calendarCheckList,ArrayList<Integer> brandname) {

            this.list = list;
            this.dayList = calendarCheckList;
            this.brandname = brandname;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {

            return list.size();

        }

        @Override

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SaleCalendarActivity.GridAdapter.ViewHolder holder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.gridview_item_calendar, parent, false);
                holder = new SaleCalendarActivity.GridAdapter.ViewHolder();
                holder.grid_item = (LinearLayout)convertView.findViewById(R.id.grid_item);
                holder.tvItemGridView = (TextView) convertView.findViewById(R.id.tv_itemday);
                holder.sale_1 = (TextView) convertView.findViewById(R.id.sale_1);
                holder.sale_2 = (TextView) convertView.findViewById(R.id.sale_2);
                holder.sale_3 = (TextView) convertView.findViewById(R.id.sale_3);
                holder.sale_4 = (TextView) convertView.findViewById(R.id.sale_4);
                holder.sale_5 = (TextView) convertView.findViewById(R.id.sale_5);

                convertView.setTag(holder);

            } else {
                holder = (SaleCalendarActivity.GridAdapter.ViewHolder) convertView.getTag();

            }

            holder.tvItemGridView.setText("" + getItem(position));

            Log.d("날짜", getItem(position));
            //해당 날짜 텍스트 컬러,배경 변경
            mCal = Calendar.getInstance();

            //오늘 day 가져옴
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);

            Log.d("뭐가 출력되는 걸까요?", sToday);

            if (!sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                holder.grid_item.setBackgroundColor(Color.argb(100,255,255,255));
            }

          /*  Log.d("현재 값", getItem(position));
            for (int i = 0; i < dayList.size(); i++) {
                for (int m = 0; m < dayList.get(i).size(); m++) {
                    Log.d("내가 받아온 값들 그냥 다 출력해버리자", dayList.get(i).get(m).toString());
                }
            }*/
        /*for(int m=0;m<dayList.get(0).size();m++){
            if (getItem(position).equals(dayList.get(0).get(m).toString())) {
                holder.sale_1.setBackgroundColor(Color.argb(99,255,0,0));
            }
        }*/
            if(getItem(position).equals("월")||getItem(position).equals("화")||getItem(position).equals("수")||getItem(position).equals("목")||
                    getItem(position).equals("금")||getItem(position).equals("토")||getItem(position).equals("일")){
                holder.tvItemGridView.setTextSize(18);
                holder.tvItemGridView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
                holder.sale_2.setVisibility(View.GONE);
                holder.sale_3.setVisibility(View.GONE);
                holder.sale_4.setVisibility(View.GONE);
                holder.sale_5.setVisibility(View.GONE);
            }
            for (int i = 0; i < dayList.size(); i++) {
                if (i == 0) {
                    for (int j = 0; j < dayList.get(i).size(); j++) {
                        if (getItem(position).equals(dayList.get(i).get(j).toString())) {

                            if(j==0){
                                Log.d("첫날 브랜드네임이 무엇일까요?", ApplicationController.itemDataList.get(brandname.get(0)).getName());
                                holder.sale_1.setText(ApplicationController.itemDataList.get(brandname.get(0)).getName());
                                holder.sale_1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            Log.d("현재 값: 월부터출력", getItem(position));
                            Log.d("현재 값:배열1의 나의 값", dayList.get(i).get(j).toString());
                            holder.sale_1.setVisibility(View.VISIBLE);
                            holder.sale_1.setBackgroundColor(Color.argb(100, 255, 223, 182));

                        }
                    }

                } else if (i == 1) {
                    for (int j = 0; j < dayList.get(i).size(); j++) {
                        if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                            if(j==0){
                                Log.d("첫날 브랜드네임이 무엇일까요?", ApplicationController.itemDataList.get(brandname.get(1)).getName());
                                holder.sale_2.setText(ApplicationController.itemDataList.get(brandname.get(1)).getName());
                                holder.sale_2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            holder.sale_2.setVisibility(View.VISIBLE);
                            holder.sale_2.setBackgroundColor(Color.argb(100, 248, 255, 182));

                        }
                    }

                } else if (i == 2) {
                    for (int j = 0; j < dayList.get(i).size(); j++) {
                        if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                            if(j==0){
                                Log.d("첫날 브랜드네임이 무엇일까요?", ApplicationController.itemDataList.get(brandname.get(2)).getName());
                                holder.sale_3.setText(ApplicationController.itemDataList.get(brandname.get(2)).getName());
                                holder.sale_3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            holder.sale_3.setVisibility(View.VISIBLE);
                            holder.sale_3.setBackgroundColor(Color.argb(100, 211, 255, 182));

                        }
                    }

                } else if (i == 3) {
                    for (int j = 0; j < dayList.get(i).size(); j++) {
                        if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                            if(j==0){
                                Log.d("첫날 브랜드네임이 무엇일까요?", ApplicationController.itemDataList.get(brandname.get(3)).getName());
                                holder.sale_4.setText(ApplicationController.itemDataList.get(brandname.get(3)).getName());
                                holder.sale_4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            holder.sale_4.setVisibility(View.VISIBLE);
                            holder.sale_4.setBackgroundColor(Color.argb(100, 216, 182, 255));

                        }
                    }

                } else if (i == 4) {
                    for (int j = 0; j < dayList.get(i).size(); j++) {
                        if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                            if(j==0){
                                Log.d("첫날 브랜드네임이 무엇일까요?", ApplicationController.itemDataList.get(brandname.get(4)).getName());
                                holder.sale_5.setText(ApplicationController.itemDataList.get(brandname.get(4)).getName());
                                holder.sale_5.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            holder.sale_5.setVisibility(View.VISIBLE);
                            holder.sale_5.setBackgroundColor(Color.argb(100, 248, 215, 215));

                        }
                    }

                }


            }
            return convertView;

        }

        private class ViewHolder {

            LinearLayout grid_item;

            TextView tvItemGridView;

            TextView sale_1;
            TextView sale_2;
            TextView sale_3;
            TextView sale_4;
            TextView sale_5;


        }


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
            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sale_calendar, menu);
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

    public void top_click4(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home4:
                Intent top_intent1 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish4:
                Intent top_intent2 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall4:
                ApplicationController.mapAll = true;
                Intent top_intent3 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar4:
                //calendar
                break;
            case R.id.btn_up_more4:
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
