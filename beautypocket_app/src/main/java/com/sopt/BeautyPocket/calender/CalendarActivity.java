/*
package com.sopt.BeautyPocket.calender;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.model.CalendarCheck;
import com.sopt.BeautyPocket.model.CalendarSale;
import com.sopt.BeautyPocket.model.TwoDate;
import com.sopt.BeautyPocket.network.NetworkService;

import java.util.ArrayList;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import java.util.Collections;
import java.util.Date;

import java.util.List;

import java.util.Locale;


import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.GridView;

import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CalendarActivity extends Activity {

    ArrayList<String> day;
    private TextView tvDate;
    private GridAdapter gridAdapter;
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






    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        networkService = ApplicationController.getInstance().getNetworkService();

        tvDate = (TextView)findViewById(R.id.tv_date);

        gridView = (GridView)findViewById(R.id.gridview);
        day = new ArrayList<String>();
        startend = new ArrayList<TwoDate>();



        //달력 네트워크요청
        final Call<ArrayList<CalendarSale>> getCalendarSale = networkService.getCalendarSale();
        getCalendarSale.enqueue(new Callback<ArrayList<CalendarSale>>() {
            @Override
            public void onResponse(Call<ArrayList<CalendarSale>> call, Response<ArrayList<CalendarSale>> response) {
                if(response.isSuccessful()){
                    calendarSaleList = new ArrayList<CalendarSale>();
                    calendarSaleList = response.body();
                    Log.d("값 갯수", String.valueOf(calendarSaleList.size()));
                    calendarCheckList = new ArrayList<ArrayList<String>>();
                    calendarFinal();
                    Log.d("달력이 성공했을까요?","성공했습니다!");


                }

            }

            @Override
            public void onFailure(Call<ArrayList<CalendarSale>> call, Throwable t) {
                Log.d("달력이 성공했을까요?",t.getMessage());
            }
        });




    }
    public void calendarFinal(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();

        final Date date = new Date(now);

        //연,월,일을 따로 저장

        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 날짜 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));

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
        for(int i=0; i<calendarSaleList.size();i++){
            Log.d("첫째날",calendarSaleList.get(i).getSale_day());
            Log.d("둘째날",calendarSaleList.get(i).getSale_end());
            */
/*twoDate.setD1(calendarSaleList.get(i).getSale_day());
            twoDate.setD2(calendarSaleList.get(i).getSale_end());
            Log.d("첫째날",twoDate.getD1());
            Log.d("둘째날",twoDate.getD2());*//*


            startend.add(new TwoDate(calendarSaleList.get(i).getSale_day(),calendarSaleList.get(i).getSale_end()));
        }
        //날짜를 차이간격을 조사하여 해당하는 날짜리스트만들기
        for(int i=0; i<startend.size();i++){
            int a = Integer.valueOf(startend.get(i).getD2())-Integer.valueOf(startend.get(i).getD1());
            int b;
            c = new ArrayList<String>();
            for(int j=0; j<=a;j++){

                b = Integer.valueOf((startend.get(i).getD1()).substring(6,8))+j;
                c.add(String.valueOf(b));
                Log.d("c의 값을 봐보자",c.get(0).toString());
            }
                Log.d("값넣기전에 c의 값을 봐보자", String.valueOf(c.size()));
            calendarCheckList.add(c);
        }



        gridAdapter = new GridAdapter(getApplicationContext(), dayList,calendarCheckList);

        gridView.setAdapter(gridAdapter);
    }


    */
/**

     * 해당 월에 표시할 일 수 구함

     *

     * @param month

     *//*


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

    public GridAdapter(Context context, List<String> list, ArrayList<ArrayList<String>> calendarCheckList) {

        this.list = list;
        this.dayList = calendarCheckList;
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
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item_calendar, parent, false);
            holder = new ViewHolder();
            holder.tvItemGridView = (TextView) convertView.findViewById(R.id.tv_item_day);
            holder.sale_1 = (View) convertView.findViewById(R.id.sale_1);
            holder.sale_2 = (View) convertView.findViewById(R.id.sale_2);
            holder.sale_3=(View) convertView.findViewById(R.id.sale_3);
            holder.sale_4 = (View) convertView.findViewById(R.id.sale_4);
            holder.sale_5 = (View) convertView.findViewById(R.id.sale_5);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.tvItemGridView.setText("" + getItem(position));

        Log.d("날짜", getItem(position));
        //해당 날짜 텍스트 컬러,배경 변경
        mCal = Calendar.getInstance();

        //오늘 day 가져옴
        Integer today = mCal.get(Calendar.DAY_OF_MONTH);
        String sToday = String.valueOf(today);

        Log.d("뭐가 출력되는 걸까요?", sToday);

        if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
            holder.tvItemGridView.setTextColor(Color.RED);
        }

        Log.d("현재 값", getItem(position));
        for (int i = 0; i < dayList.size(); i++) {
            for (int m = 0; m < dayList.get(i).size(); m++) {
                Log.d("내가 받아온 값들 그냥 다 출력해버리자", dayList.get(i).get(m).toString());
            }
        }
        */
/*for(int m=0;m<dayList.get(0).size();m++){
            if (getItem(position).equals(dayList.get(0).get(m).toString())) {
                holder.sale_1.setBackgroundColor(Color.argb(99,255,0,0));
            }
        }*//*

        for (int i = 0; i < dayList.size(); i++) {
            if (i == 0) {
                for (int j = 0; j < dayList.get(i).size(); j++) {
                    holder.sale_1.setVisibility(View.GONE);
                    if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                        Log.d("현재 값: 월부터출력", getItem(position));
                        Log.d("현재 값:배열1의 나의 값", dayList.get(i).get(j).toString());
                        holder.sale_1.setVisibility(View.VISIBLE);
                        holder.sale_1.setBackgroundColor(Color.argb(99, 255, 0, 0));

                    }
                }

            } else if (i == 1) {
                for (int j = 0; j < dayList.get(i).size(); j++) {
                    if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                        holder.sale_2.setVisibility(View.VISIBLE);
                        holder.sale_2.setBackgroundColor(Color.argb(99, 255, 0, 0));

                    }
                }

            } else if (i == 2) {
                for (int j = 0; j < dayList.get(i).size(); j++) {
                    if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                        holder.sale_3.setVisibility(View.VISIBLE);
                        holder.sale_3.setBackgroundColor(Color.argb(99, 255, 0, 0));

                    }
                }

            } else if (i == 3) {
                for (int j = 0; j < dayList.get(i).size(); j++) {
                    if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                        holder.sale_4.setVisibility(View.VISIBLE);
                        holder.sale_4.setBackgroundColor(Color.argb(99, 255, 0, 0));

                    }
                }

            } else if (i == 4) {
                for (int j = 0; j < dayList.get(i).size(); j++) {
                    if (getItem(position).equals(dayList.get(i).get(j).toString())) {
                        holder.sale_5.setVisibility(View.VISIBLE);
                        holder.sale_5.setBackgroundColor(Color.argb(99, 255, 0, 0));

                    }
                }

            }


        }
        return convertView;

    }

    private class ViewHolder {

        TextView tvItemGridView;

        View sale_1;
        View sale_2;
        View sale_3;
        View sale_4;
        View sale_5;


    }


}}
*/
