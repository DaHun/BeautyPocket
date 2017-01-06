package com.sopt.BeautyPocket.more;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.calender.SaleCalendarActivity;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

public class Company extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
    }

    public void top_click14(View v) {
        switch (v.getId()) {
            case R.id.btn_up_home14:
                Intent top_intent5 = new Intent(getApplicationContext(), MainActivity.class);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent5);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_wish14:
                Intent top_intent1 = new Intent(getApplicationContext(), WishlistActivity.class);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent1);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_mapall14:
                ApplicationController.mapAll = true;
                Intent top_intent2 = new Intent(getApplicationContext(), MapActivity.class);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_calendar14:
                Intent top_intent3 = new Intent(getApplicationContext(), SaleCalendarActivity.class);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                top_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(top_intent3);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.btn_up_more14:
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
